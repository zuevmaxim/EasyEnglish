package ru.hse.android.project.easyenglish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationCallback;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchUpdateCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class NetworkController extends AppCompatActivity {

    private static final String TAG = "WORD_CHAIN";
    private static final int PLAYERS_NUMBER = 1;

    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;

    // Client used to interact with the TurnBasedMultiplayer system.
    private TurnBasedMultiplayerClient mTurnBasedMultiplayerClient = null;

    // Client used to interact with the Invitation system.
    private InvitationsClient mInvitationsClient = null;

    // Local convenience pointers
    private TextView mDataView;
    private TextView mOpponentText;
    private TextView mTurnTextView;

    // For our intents
    private static final int RC_SIGN_IN = 42;
    private final static int RC_SELECT_PLAYERS = 43;
    private final static int RC_LOOK_AT_MATCHES = 44;

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;

    // This is the current match we're in; null if not loaded
    private TurnBasedMatch mMatch;

    private String opponentWord;
    private WordChain wordChain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_chain_find_opponent);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Games.SCOPE_GAMES_LITE)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        Button startGameButton = findViewById(R.id.start_game_button); //TODO : rename
        startGameButton.setOnClickListener(view -> {
            Log.d(TAG, "Start game button clicked");
            mTurnBasedMultiplayerClient.getSelectOpponentsIntent(PLAYERS_NUMBER, PLAYERS_NUMBER, false)
                    .addOnSuccessListener(intent -> startActivityForResult(intent, RC_SELECT_PLAYERS))
                    .addOnFailureListener(createFailureListener(
                            getString(R.string.error_get_select_opponents)));
        });

        Button checkGamesButton = findViewById(R.id.check_games_button);
        checkGamesButton.setOnClickListener(view -> {
            Log.d(TAG, "CheckGamesButton clicked");
            mTurnBasedMultiplayerClient.getInboxIntent()
                    .addOnSuccessListener(intent -> startActivityForResult(intent, RC_LOOK_AT_MATCHES))
                    .addOnFailureListener(createFailureListener(getString(R.string.error_get_inbox_intent)));
        });

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(view -> {
            Log.d(TAG, "Sign-in button clicked");
            startSignInIntent();
        });

        Button signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(view -> {
            Log.d(TAG, "Sign-out button clicked");
            signOut();
        });

        Button doneButton = findViewById(R.id.send_answer_button);
        doneButton.setOnClickListener(view -> {
            Log.d(TAG, "doneButton clicked");
            onDoneClicked();
        });

        Button cancelButton = findViewById(R.id.end_game_button);
        cancelButton.setOnClickListener(view -> {
            Log.d(TAG, "cancelButton clicked");
            mTurnBasedMultiplayerClient.cancelMatch(mMatch.getMatchId())
                    .addOnSuccessListener(this::onCancelMatch)
                    .addOnFailureListener(createFailureListener("There was a problem cancelling the match!"));

            isDoingTurn = false;
        });

        Button leaveButton = findViewById(R.id.end_game_button);
        leaveButton.setOnClickListener(view -> {
            Log.d(TAG, "leaveButton clicked");
            String nextParticipantId = getNextParticipantId();

            mTurnBasedMultiplayerClient.leaveMatchDuringTurn(mMatch.getMatchId(), nextParticipantId)
                    .addOnSuccessListener(aVoid -> onLeaveMatch())
                    .addOnFailureListener(createFailureListener("There was a problem leaving the match!"));
        });

        Button finishButton = findViewById(R.id.end_game_button);
        finishButton.setOnClickListener(view -> {
            Log.d(TAG, "finishButton clicked");
            mTurnBasedMultiplayerClient.finishMatch(mMatch.getMatchId())
                    .addOnSuccessListener(this::onUpdateMatch)
                    .addOnFailureListener(createFailureListener("There was a problem finishing the match!"));

            isDoingTurn = false;
        });

        mDataView = findViewById(R.id.answer_word_text);
        mOpponentText = findViewById(R.id.opponent_word);
        mTurnTextView = findViewById(R.id.turn_status_text);
    }

    private void changeLayout() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        signInSilently();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the invitation callbacks; they will be re-registered via
        // onResume->signInSilently->onConnected.
        if (mInvitationsClient != null) {
            mInvitationsClient.unregisterInvitationCallback(mInvitationCallback);
        }

        if (mTurnBasedMultiplayerClient != null) {
            mTurnBasedMultiplayerClient.unregisterTurnBasedMatchUpdateCallback(mMatchUpdateCallback);
        }
    }

    private String mDisplayName; // TODO show name
    private String mPlayerId;

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");

        mTurnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this, googleSignInAccount);
        mInvitationsClient = Games.getInvitationsClient(this, googleSignInAccount);

        Games.getPlayersClient(this, googleSignInAccount)
                .getCurrentPlayer()
                .addOnSuccessListener(
                        player -> {
                            mDisplayName = player.getDisplayName();
                            mPlayerId = player.getPlayerId();
                        }
                )
                .addOnFailureListener(createFailureListener("There was a problem getting the player!"));

        Log.d(TAG, "onConnected(): Connection successful");

        GamesClient gamesClient = Games.getGamesClient(this, googleSignInAccount);
        gamesClient.getActivationHint()
                .addOnSuccessListener(hint -> {
                    if (hint != null) {
                        TurnBasedMatch match = hint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);
                        if (match != null) {
                            updateMatch(match);
                        }
                    }
                })
                .addOnFailureListener(createFailureListener(
                        "There was a problem getting the activation hint!"));

        mInvitationsClient.registerInvitationCallback(mInvitationCallback);
        mTurnBasedMultiplayerClient.registerTurnBasedMatchUpdateCallback(mMatchUpdateCallback);
    }

    private void onDisconnected() {
        Log.d(TAG, "onDisconnected()");

        mTurnBasedMultiplayerClient = null;
        mInvitationsClient = null;
    }

    private OnFailureListener createFailureListener(final String string) {
        return e -> handleException(e, string);
    }



    // Upload your new gamestate, then take a turn, and pass it on to the next
    // player.
    public void onDoneClicked() {
        String nextParticipantId = getNextParticipantId();
        String word = mDataView.getText().toString();
        if (!wordChain.isMyTurn()) {
            Toast.makeText(this, "Not your turn!", Toast.LENGTH_LONG).show();
            return;
        }
        int code = wordChain.isValidMove(word);
        if (code != WordChain.RESULT_OK) {
            String message = "Error.";
            switch (code) {
                case WordChain.RESULT_REPETITION:
                    message = "Such word had been used.";
                    break;
                case WordChain.RESULT_NOT_A_NOUN:
                    message = "Word is not a noun.";
                    break;
                case WordChain.RESULT_EMPTY:
                    message = "Word is empty";
                    break;
                case WordChain.RESULT_WRONG_FIRST_LETTER:
                    message = "Wrong first letter.";
                    break;
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return;
        }
        mTurnBasedMultiplayerClient.takeTurn(mMatch.getMatchId(),
                wordChain.hash(word), nextParticipantId)
                .addOnSuccessListener(turnBasedMatch -> {
                    Log.d(TAG, "Send data.");
                    wordChain.makeMove(word);
                    wordChain.changeTurn();
                    setGameplayUI();
                    onUpdateMatch(turnBasedMatch);
                })
                .addOnFailureListener(createFailureListener("There was a problem taking a turn!"));
    }

    // Switch to gameplay view.
    public void setGameplayUI() {
        isDoingTurn = true;
        mOpponentText.setText(opponentWord);
        mTurnTextView.setText(String.format("Turn %s", wordChain.isMyTurn() ? "My" : "Opponent"));
    }


    // Generic warning/info dialog
    public void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                (dialog, id) -> {
                });

        // create alert dialog
        AlertDialog mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }

    /**
     * Start a sign in activity.  To properly handle the result, call tryHandleSignInResult from
     * your Activity's onActivityResult function
     */
    public void startSignInIntent() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    /**
     * Try to sign in without displaying dialogs to the user.
     * <p>
     * If the user has already signed in previously, it will not show dialog.
     */
    public void signInSilently() {
        Log.d(TAG, "signInSilently()");

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInSilently(): success");
                        onConnected(task.getResult());
                    } else {
                        Log.d(TAG, "signInSilently(): failure", task.getException());
                        onDisconnected();
                    }
                });
    }

    public void signOut() {
        Log.d(TAG, "signOut()");

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signOut(): success");
                    } else {
                        handleException(task.getException(), "signOut() failed!");
                    }
                    onDisconnected();
                });
    }

    private void handleException(Exception exception, String details) {
        int status = 0;

        if (exception instanceof TurnBasedMultiplayerClient.MatchOutOfDateApiException) {
            TurnBasedMultiplayerClient.MatchOutOfDateApiException matchOutOfDateApiException =
                    (TurnBasedMultiplayerClient.MatchOutOfDateApiException) exception;

            Log.e(TAG, "Match was out of date, updating with latest match data...");

            TurnBasedMatch match = matchOutOfDateApiException.getMatch();
            updateMatch(match);

            return;
        }

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }

        if (!checkStatusCode(status)) {
            return;
        }

        String message = getString(R.string.status_exception_error, details, status, exception);
        Log.e(TAG, message);
        new AlertDialog.Builder(this)
                .setMessage("Game error. Sorry.")
                .setNeutralButton(android.R.string.ok, (dialogInterface, i) -> finish())
                .show();
    }


    private void logBadActivityResult(int requestCode, int resultCode, String message) {
        Log.e(TAG, "Bad activity result(" + resultCode + ") for request (" + requestCode + "): "
                + message);
    }

    // This function is what gets called when you return from either the Play
    // Games built-in inbox, or else the create game built-in interface.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
                Log.d(TAG, "Logged in successfully.");
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }

                onDisconnected();
                handleException(apiException, message);
            }
        } else if (requestCode == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog
            if (resultCode != Activity.RESULT_OK) {
                logBadActivityResult(requestCode, resultCode,
                        "User cancelled returning from the 'Select Match' dialog.");
                return;
            }

            TurnBasedMatch match = intent
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                guestCreateMatch(match);
            }

            Log.d(TAG, "Match = " + match);
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // Returning from 'Select players to Invite' dialog
            if (resultCode != Activity.RESULT_OK) {
                // user canceled
                logBadActivityResult(requestCode, resultCode,
                        "User cancelled returning from 'Select players to Invite' dialog");
                return;
            }

            // get the invitee list
            ArrayList<String> invitees = intent
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get automatch criteria
            Bundle autoMatchCriteria;

            int minAutoMatchPlayers = intent.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = intent.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers,
                        maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria).build();

            // Start the match
            mTurnBasedMultiplayerClient.createMatch(tbmc)
                    .addOnSuccessListener(turnBasedMatch -> {
                        Log.d(TAG, "Match inited.");
                        onInitiateMatch(turnBasedMatch);
                    })
                    .addOnFailureListener(createFailureListener("There was a problem creating a match!"));
        }
    }

    public void guestCreateMatch(TurnBasedMatch match) {
        wordChain = new WordChain();
        wordChain.setTurn(false);
        updateMatch(match);
    }

    public void startMatch(TurnBasedMatch match) {
        wordChain = new WordChain();
        wordChain.setTurn(true);
        mMatch = match;
        opponentWord = "";
        setGameplayUI();

        String myParticipantId = mMatch.getParticipantId(mPlayerId);

        mTurnBasedMultiplayerClient.takeTurn(match.getMatchId(),
                wordChain.hash("test"), myParticipantId)
                .addOnSuccessListener(match1 -> {
                    wordChain.changeTurn();
                    updateMatch(match1);
                    setGameplayUI();
                })
                .addOnFailureListener(createFailureListener("There was a problem taking a turn!"));
    }


    public String getNextParticipantId() {
        String myParticipantId = mMatch.getParticipantId(mPlayerId);
        ArrayList<String> participantIds = mMatch.getParticipantIds();
        int desiredIndex = - 1;
        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }
        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }
        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            return participantIds.get(0);
        } else {
            return null;
        }
    }

    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) {
        Log.d(TAG, "Update match.");
        mMatch = match;
        if (wordChain == null) {
            Log.e(TAG, "Error : wordChain == null unexpectedly.");
            guestCreateMatch(match);
        }

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showWarning("Waiting for auto-match...",
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    showWarning("Complete!",
                            "This game is over; someone finished it, and so did you!  " +
                                    "There is nothing to be done.");
                    break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                showWarning("Complete!",
                        "This game is over; someone finished it!  You can only finish it now.");
        }

        // OK, it's active. Check on turn status.
        if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
            opponentWord = wordChain.unhash(mMatch.getData());
            mDataView.setText("");
            wordChain.makeMove(opponentWord);
            wordChain.changeTurn();
            Log.d(TAG, "Get data");
            setGameplayUI();
        }
    }

    private void onCancelMatch(String matchId) {

        isDoingTurn = false;

        showWarning("Match", "This match (" + matchId + ") was canceled.  " +
                "All other players will have their game ended.");
    }

    private void onInitiateMatch(TurnBasedMatch match) {
        if (match.getData() != null) {
            updateMatch(match);
            return;
        }

        startMatch(match);
    }

    private void onLeaveMatch() {
        isDoingTurn = false;
        showWarning("Left", "You've left this match.");
    }

    public void onUpdateMatch(TurnBasedMatch match) {
        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);

        if (isDoingTurn) {
            updateMatch(match);
        }
    }

    private InvitationCallback mInvitationCallback = new InvitationCallback() {
        @Override
        public void onInvitationReceived(@NonNull Invitation invitation) { }

        @Override
        public void onInvitationRemoved(@NonNull String invitationId) { }
    };

    private TurnBasedMatchUpdateCallback mMatchUpdateCallback = new TurnBasedMatchUpdateCallback() {
        @Override
        public void onTurnBasedMatchReceived(@NonNull TurnBasedMatch turnBasedMatch) {
            if (turnBasedMatch.getMatchId().equals(mMatch.getMatchId())) {
                updateMatch(turnBasedMatch);
            }
        }

        @Override
        public void onTurnBasedMatchRemoved(@NonNull String matchId) { }
    };

    public void showErrorMessage(int stringId) {
        Log.e(TAG, getResources().getString(stringId));
    }

    private boolean checkStatusCode(int statusCode) {
        switch (statusCode) {
            case GamesCallbackStatusCodes.OK:
                return true;

            case GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(R.string.match_error_already_rematched);
                break;
            case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(R.string.network_error_operation_failed);
                break;
            case GamesClientStatusCodes.INTERNAL_ERROR:
                showErrorMessage(R.string.internal_error);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(R.string.match_error_inactive_match);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }
}
