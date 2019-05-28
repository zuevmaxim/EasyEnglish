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

        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);


        Button startGameButton = findViewById(R.id.button_quick_game); //TODO : rename
        startGameButton.setOnClickListener(view -> {
            Log.d(TAG, "Start game button clicked");
            mTurnBasedMultiplayerClient.getSelectOpponentsIntent(1, 1, false)
                    .addOnSuccessListener(intent -> startActivityForResult(intent, RC_SELECT_PLAYERS))
                    .addOnFailureListener(createFailureListener(
                            getString(R.string.error_get_select_opponents)));
        });

        Button checkGamesButton = findViewById(R.id.checkGamesButton);
        checkGamesButton.setOnClickListener(view -> {
            Log.d(TAG, "CheckGamesButton clicked");
            mTurnBasedMultiplayerClient.getInboxIntent()
                    .addOnSuccessListener(intent -> startActivityForResult(intent, RC_LOOK_AT_MATCHES))
                    .addOnFailureListener(createFailureListener(getString(R.string.error_get_inbox_intent)));
        });

        SignInButton signInButton = findViewById(R.id.button_sign_in);
        signInButton.setOnClickListener(view -> {
            Log.d(TAG, "Sign-in button clicked");
            startSignInIntent();
        });

        Button signOutButton = findViewById(R.id.button_sign_out);
        signOutButton.setOnClickListener(view -> {
            Log.d(TAG, "Sign-out button clicked");
            signOut();
        });

        Button doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(view -> {
            Log.d(TAG, "doneButton clicked");
            onDoneClicked();
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> {
            Log.d(TAG, "cancelButton clicked");
            mTurnBasedMultiplayerClient.cancelMatch(mMatch.getMatchId())
                    .addOnSuccessListener(this::onCancelMatch)
                    .addOnFailureListener(createFailureListener("There was a problem cancelling the match!"));

            isDoingTurn = false;
        });

        Button leaveButton = findViewById(R.id.leaveButton);
        leaveButton.setOnClickListener(view -> {
            Log.d(TAG, "leaveButton clicked");
            String nextParticipantId = getNextParticipantId();

            mTurnBasedMultiplayerClient.leaveMatchDuringTurn(mMatch.getMatchId(), nextParticipantId)
                    .addOnSuccessListener(aVoid -> onLeaveMatch())
                    .addOnFailureListener(createFailureListener("There was a problem leaving the match!"));
        });

        Button finishButton = findViewById(R.id.finishButton);
        finishButton.setOnClickListener(view -> {
            Log.d(TAG, "finishButton clicked");
            mTurnBasedMultiplayerClient.finishMatch(mMatch.getMatchId())
                    .addOnSuccessListener(this::onUpdateMatch)
                    .addOnFailureListener(createFailureListener("There was a problem finishing the match!"));

            isDoingTurn = false;
        });

        mDataView = findViewById(R.id.data_view);
        mOpponentText = findViewById(R.id.opponent_text);
        mTurnTextView = findViewById(R.id.turn_counter_view);
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

        // Retrieve the TurnBasedMatch from the connectionHint
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


        // As a demonstration, we are registering this activity as a handler for
        // invitation and match events.

        // This is *NOT* required; if you do not register a handler for
        // invitation events, you will get standard notifications instead.
        // Standard notifications may be preferable behavior in many cases.
        mInvitationsClient.registerInvitationCallback(mInvitationCallback);

        // Likewise, we are registering the optional MatchUpdateListener, which
        // will replace notifications you would get otherwise. You do *NOT* have
        // to register a MatchUpdateListener.
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

        //mWordChainData = null;
    }

    // Switch to gameplay view.
    public void setGameplayUI() {
        isDoingTurn = true;
        mOpponentText.setText(opponentWord);
        mTurnTextView.setText("Turn " + (wordChain.isMyTurn() ? "My" : "Opponent"));
    }


    // Generic warning/info dialog
    public void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                (dialog, id) -> {
                    // if this button is clicked, close
                    // current activity
                    //finish();
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

            new AlertDialog.Builder(this)
                    .setMessage("Match was out of date, updating with latest match data...")
                    .setNeutralButton(android.R.string.ok, null)
                    .show();

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

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }


    private void logBadActivityResult(int requestCode, int resultCode, String message) {
        Log.i(TAG, "Bad activity result(" + resultCode + ") for request (" + requestCode + "): "
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

                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
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

    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(TurnBasedMatch match) {
        wordChain = new WordChain();
        wordChain.setTurn(true);
        mMatch = match;
        setGameplayUI();

        String myParticipantId = mMatch.getParticipantId(mPlayerId);


        mTurnBasedMultiplayerClient.takeTurn(match.getMatchId(),
                wordChain.hash("test"), myParticipantId)
                .addOnSuccessListener(match1 -> {
                    updateMatch(match1);
                    wordChain.changeTurn();
                    setGameplayUI();
                })
                .addOnFailureListener(createFailureListener("There was a problem taking a turn!"));
    }


    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public String getNextParticipantId() {

        String myParticipantId = mMatch.getParticipantId(mPlayerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) {
        Log.d(TAG, "Update match.");
        mMatch = match;

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
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                //mWordChainData = WordChain.unpersist(mMatch.getData());
                opponentWord = wordChain.unhash(mMatch.getData());
                mDataView.setText("");
                wordChain.makeMove(opponentWord);
                wordChain.changeTurn();
                Log.d(TAG, "Get data");
                setGameplayUI();
                return;
        }
    }

    private void onCancelMatch(String matchId) {

        isDoingTurn = false;

        showWarning("Match", "This match (" + matchId + ") was canceled.  " +
                "All other players will have their game ended.");
    }

    private void onInitiateMatch(TurnBasedMatch match) {
        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
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
            return;
        }
    }

    private InvitationCallback mInvitationCallback = new InvitationCallback() {
        // Handle notification events.
        @Override
        public void onInvitationReceived(@NonNull Invitation invitation) {
            Toast.makeText(
                    NetworkController.this,
                    "An invitation has arrived from "
                            + invitation.getInviter().getDisplayName(), Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onInvitationRemoved(@NonNull String invitationId) {
            Toast.makeText(NetworkController.this, "An invitation was removed.", Toast.LENGTH_SHORT)
                    .show();
        }
    };

    private TurnBasedMatchUpdateCallback mMatchUpdateCallback = new TurnBasedMatchUpdateCallback() {
        @Override
        public void onTurnBasedMatchReceived(@NonNull TurnBasedMatch turnBasedMatch) {
            if (turnBasedMatch.getMatchId().equals(mMatch.getMatchId())) {
                updateMatch(turnBasedMatch);
            }
            Toast.makeText(NetworkController.this, "A match was updated.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTurnBasedMatchRemoved(@NonNull String matchId) {
            Toast.makeText(NetworkController.this, "A match was removed.", Toast.LENGTH_SHORT).show();
        }
    };

    public void showErrorMessage(int stringId) {
        showWarning("Warning", getResources().getString(stringId));
    }

    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
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
