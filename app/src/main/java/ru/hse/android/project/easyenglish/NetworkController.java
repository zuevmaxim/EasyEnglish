package ru.hse.android.project.easyenglish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.adapters.WordChainHistoryAdapter;

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
    private TextView mDataFirstLetterText;
    private TextView mOpponentText;
    private TextView mOpponentLastLetterText;
    private TextView mTurnTextView;

    // For our intents
    private static final int RC_SIGN_IN = 42;
    private final static int RC_SELECT_PLAYERS = 43;
    private final static int RC_LOOK_AT_MATCHES = 44;

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;

    private boolean isGame = false;

    // This is the current match we're in; null if not loaded
    private TurnBasedMatch mMatch;

    private String opponentWord = "";
    private WordChain wordChain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        changeLayout();


        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Games.SCOPE_GAMES_LITE)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initSingInLayout() {
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(view -> {
            Log.d(TAG, "Sign-in button clicked");
            startSignInIntent();
        });
    }

    private void initMenuLayout() {
        Button startGameButton = findViewById(R.id.start_game_button);
        startGameButton.setOnClickListener(view -> {
            Log.d(TAG, "Start game button clicked");
            mTurnBasedMultiplayerClient.getSelectOpponentsIntent(PLAYERS_NUMBER, PLAYERS_NUMBER, false)
                    .addOnSuccessListener(intent -> startActivityForResult(intent, RC_SELECT_PLAYERS))
                    .addOnFailureListener(createFailureListener(
                            getString(R.string.error_get_select_opponents)));
        });

        Button checkGamesButton = findViewById(R.id.check_games_button);
        checkGamesButton.setOnClickListener(view -> {
            Log.d(TAG, "Check games button clicked");
            mTurnBasedMultiplayerClient.getInboxIntent()
                    .addOnSuccessListener(intent -> startActivityForResult(intent, RC_LOOK_AT_MATCHES))
                    .addOnFailureListener(createFailureListener(getString(R.string.error_get_inbox_intent)));
        });

        Button signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(view -> {
            Log.d(TAG, "Sign-out button clicked");
            signOut();
        });
    }

    private void initGameLayout() {
        Button sendAnswerButton = findViewById(R.id.send_answer_button);
        sendAnswerButton.setOnClickListener(view -> {
            Log.d(TAG, "send answer button clicked");
            /*n
            LinearLayout opponentLayout = findViewById(R.id.opponent_layout);
            LinearLayout playerLayout = findViewById(R.id.player_layout);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.translation);
            opponentLayout.startAnimation(animation);
            playerLayout.startAnimation(animation);
            */
            onDoneClicked();
        });

        Button endGameButton = findViewById(R.id.end_game_button);
        endGameButton.setOnClickListener(view -> {
            Log.d(TAG, "cancel button clicked");

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("End game");
            adb.setMessage("Press ok to finish the game.");
            adb.setIcon(android.R.drawable.ic_dialog_alert);
            adb.setPositiveButton("OK", (dialog, which) -> cancelMatch());
            adb.setNegativeButton("Cancel", (dialog, which) -> {});
            adb.show();
        });

        Button historyButton = findViewById(R.id.history_button);
        historyButton.setOnClickListener(view -> {
            AlertDialog.Builder adb = new AlertDialog.Builder(NetworkController.this);
            adb.setTitle("Game history");
            View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
            adb.setView(dialogView).setPositiveButton("OK", (dialogInterface, i) -> { });
            RecyclerView recyclerView = dialogView.findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(NetworkController.this));
            List<Pair<String, String>> history = new ArrayList<>();
            for (Iterator<String> it = wordChain.getPreviousWords().iterator(); it.hasNext();) {
                String word1 = it.next();
                String word2 = it.hasNext() ? it.next() : "-";
                history.add(new Pair<>(word1, word2));
            }
            WordChainHistoryAdapter adapter = new WordChainHistoryAdapter(NetworkController.this, history);
            recyclerView.setAdapter(adapter);
            adb.show();
        });

        mDataFirstLetterText = findViewById(R.id.player_first_letter);
        mDataView = findViewById(R.id.answer_word_text);
        mOpponentText = findViewById(R.id.opponent_word);
        mOpponentLastLetterText = findViewById(R.id.opponent_last_letter);
        mTurnTextView = findViewById(R.id.turn_status_text);


        Button showHintsButton = findViewById(R.id.hints_button);
        showHintsButton.setOnClickListener(v -> {
            Log.d(TAG, "hints button clicked");

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            if (wordChain.getHintsNumber() == 0) {
                adb.setTitle("Hints")
                        .setMessage("All hints had been used.")
                        .setPositiveButton("OK", (dialog, which) -> { })
                        .show();
            } else {
                if (wordChain == null) {
                    adb.setTitle("Hints")
                            .setMessage("No hints available.")
                            .setPositiveButton("OK", (dialog, which) -> { })
                            .show();
                    return;
                }
                List<String> hints = wordChain.getHint();
                int availableHints = Math.min(wordChain.getHintsNumber(), hints.size());
                if (availableHints == 0) {
                    adb.setTitle("Hints")
                            .setMessage("No hints available.")
                            .setPositiveButton("OK", (dialog, which) -> { })
                            .show();
                } else {
                    adb.setTitle("Hints")
                            .setMessage(availableHints + " hints available.\nDo you want a hint?")
                            .setPositiveButton("YES", (dialog, which) -> {
                                wordChain.useHint();
                                String word = hints.get(0);
                                Toast.makeText(this, word, Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {})
                            .show();
                }

            }
        });
    }

    private void changeLayout() {
        boolean isSignedIn = mTurnBasedMultiplayerClient != null;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!isSignedIn) {
            setContentView(R.layout.activity_sign_in);
            initSingInLayout();
            return;
        }

        if (isGame) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setContentView(R.layout.activity_word_chain);
            initGameLayout();
        } else {
            setContentView(R.layout.activity_word_chain_menu);
            initMenuLayout();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        signInSilently();
    }

    private String mDisplayName;
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
        if (!isGame) {
            changeLayout();
        }

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
        changeLayout();
    }

    private OnFailureListener createFailureListener(final String string) {
        return e -> handleException(e, string);
    }

    // Upload your new gamestate, then take a turn, and pass it on to the next
    // player.
    public void onDoneClicked() {
        String nextParticipantId = getNextParticipantId();
        String word = mDataFirstLetterText.getText().toString().toLowerCase() + mDataView.getText().toString().toLowerCase();
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
        if (opponentWord.length() > 0) {
            String c = opponentWord.substring(opponentWord.length() - 1);
            mOpponentLastLetterText.setText(c.toUpperCase());
            mDataFirstLetterText.setText(c.toUpperCase());
            mOpponentText.setText(opponentWord.substring(0, opponentWord.length() - 1));
        } else {
            mDataFirstLetterText.setText("");
            mOpponentText.setText("");
        }
        mTurnTextView.setText(String.format("%s turn", wordChain.isMyTurn() ? "Your" : "Opponent"));
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

        if (checkStatusCode(status)) {
            return;
        }

        String message = getString(R.string.status_exception_error, details, status, exception);
        Log.e(TAG, message);
        new AlertDialog.Builder(this)
                .setMessage("Game error. Sorry.")
                .setCancelable(false)
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
        isGame = true;
        changeLayout();
        wordChain = new WordChain();
        wordChain.setTurn(false);
        updateMatch(match);
    }

    public void startMatch(TurnBasedMatch match) {
        isGame = true;
        changeLayout();
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
        String opDisplayName = mMatch.getParticipants().stream().filter(participant -> !participant.getParticipantId().equals(mMatch.getParticipantId(mPlayerId))).collect(Collectors.toList()).get(0).getDisplayName();
        String opPlayerId = mMatch.getParticipants().stream().filter(participant -> !participant.getParticipantId().equals(mPlayerId)).collect(Collectors.toList()).get(0).getParticipantId();
        ((TextView) findViewById(R.id.first_player_name_text)).setText(mDisplayName);
        ((TextView) findViewById(R.id.second_player_name_text)).setText(opDisplayName);
        if (wordChain == null) {
            Log.e(TAG, "Error : wordChain == null unexpectedly.");
            guestCreateMatch(match);
        }

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        if (status == TurnBasedMatch.MATCH_STATUS_CANCELED) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder
                    .setTitle("End game!")
                    .setMessage("You win!")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> endGame());
            AlertDialog mAlertDialog = alertDialogBuilder.create();
            mAlertDialog.show();
            return;
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

    private void onInitiateMatch(TurnBasedMatch match) {
        if (match.getData() != null) {
            updateMatch(match);
            return;
        }
        startMatch(match);
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
            if (mMatch != null && turnBasedMatch.getMatchId().equals(mMatch.getMatchId())) {
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

    @Override
    public void onBackPressed() {
        if (isGame) {
            new AlertDialog.Builder(this)
                    .setTitle("Exiting game")
                    .setMessage("Are you sure?")
                    .setPositiveButton("YES", (dialog, whichButton) -> cancelMatch())
                    .setNegativeButton("NO", (dialog, whichButton) -> dialog.dismiss()).show();
        } else {
            super.onBackPressed();
        }
    }

    public void cancelMatch() {
        if (mTurnBasedMultiplayerClient != null && mMatch != null) {
            Log.d(TAG, "Cancel game");
            mTurnBasedMultiplayerClient.cancelMatch(mMatch.getMatchId())
                    .addOnSuccessListener(s -> endGame())
                    .addOnFailureListener(createFailureListener("There was a problem cancelling the match!"));
        }
    }

    public void endGame() {
        Log.d(TAG, "endGame");
        isGame = false;
        isDoingTurn = false;
        wordChain = null;
        mMatch = null;
        opponentWord = "";
        changeLayout();
    }

    public void onPause() {
        Log.d(TAG, "onPause");
        if (isFinishing()) {
            cancelMatch();
        }
        super.onPause();
    }
}
