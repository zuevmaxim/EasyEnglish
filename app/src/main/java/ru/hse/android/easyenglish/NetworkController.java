package ru.hse.android.easyenglish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class NetworkController extends AppCompatActivity {

    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

        startSignInIntent();//жжжжжжжжжжжжжжжжжжжжжжжж
        while (flag);
        startQuickGame();
    }

    private void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, 42);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                flag = false;
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = "Тебе капец!";
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    final static String TAG = "MY SUPER TAG";

    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;

    // Client used to interact with the real time multiplayer system.
    private RealTimeMultiplayerClient mRealTimeMultiplayerClient = null;

    // Client used to interact with the Invitation system.
    private InvitationsClient mInvitationsClient = null;

    // Room ID where the currently active game is taking place; null if we're
    // not playing.
    String mRoomId = null;

    // Holds the configuration of the current room.
    RoomConfig mRoomConfig;

    // The participants in the currently active game
    ArrayList<Participant> mParticipants = null;

    // My participant ID in the currently active game
    String mMyId = null;

    private Room mRoom;

    private Activity thisActivity = this;

    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;

    // Message buffer for sending messages
    byte[] mMsgBuf = new byte[2];

    String mMyParticipantId = null;

    RoomConfig mJoinedRoomConfig = null;


    private RoomUpdateCallback mRoomUpdateCallback = new RoomUpdateCallback() {
        @Override
        public void onRoomCreated(int code, Room room) {
            // Update UI and internal state based on room updates.
            if (code == GamesCallbackStatusCodes.OK && room != null) {
                Log.d(TAG, "Room " + room.getRoomId() + " created.");
            } else {
                Log.w(TAG, "Error creating room: " + code);
                // let screen go to sleep
                //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }
        }

        @Override
        public void onJoinedRoom(int code, Room room) {
            // Update UI and internal state based on room updates.
            if (code == GamesCallbackStatusCodes.OK && room != null) {
                Log.d(TAG, "Room " + room.getRoomId() + " joined.");
            } else {
                Log.w(TAG, "Error joining room: " + code);
                // let screen go to sleep
                //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }
        }

        @Override
        public void onLeftRoom(int code, String roomId) {
            Log.d(TAG, "Left room" + roomId);
        }

        @Override
        public void onRoomConnected(int code, @Nullable Room room) {
            if (code == GamesCallbackStatusCodes.OK && room != null) {
                Log.d(TAG, "Room " + room.getRoomId() + " connected.");
            } else {
                Log.w(TAG, "Error connecting to room: " + code);
                // let screen go to sleep
                //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }
        }
    };

    private OnRealTimeMessageReceivedListener mMessageReceivedHandler =
            realTimeMessage -> {
                // Handle messages received here.
                byte[] message = realTimeMessage.getMessageData();
                Log.w(TAG, "Get message " + message.length);
                // process message contents...
            };

    private RoomStatusUpdateCallback mRoomStatusCallbackHandler = new RoomStatusUpdateCallback() {
        @Override
        public void onRoomConnecting(@Nullable Room room) {
            // Update the UI status since we are in the process of connecting to a specific room.
            Log.w(TAG, "Room connecting...");
        }

        @Override
        public void onRoomAutoMatching(@Nullable Room room) {
            // Update the UI status since we are in the process of matching other players.
            Log.w(TAG, "Room auto matching...");
        }

        @Override
        public void onPeerInvitedToRoom(@Nullable Room room, List<String> list) {
            // Update the UI status since we are in the process of matching other players.
            Log.w(TAG, "PeerInvitedToRoom...");
        }

        @Override
        public void onPeerDeclined(@Nullable Room room, List<String> list) {
            // Peer declined invitation, see if game should be canceled
            Log.w(TAG, "PeerDeclined...");
        }

        @Override
        public void onPeerJoined(@Nullable Room room, List<String> list) {
            // Update UI status indicating new players have joined!
            Log.w(TAG, "PeerJoined...");
        }

        @Override
        public void onPeerLeft(@Nullable Room room, List<String> list) {
            // Peer left, see if game should be canceled.
            Log.w(TAG, "PeerLeft...");
        }

        @Override
        public void onConnectedToRoom(@Nullable Room room) {
            // Connected to room, record the room Id.
            Log.w(TAG, "Connected to room...");
            mRoom = room;
            Games.getPlayersClient(thisActivity, GoogleSignIn.getLastSignedInAccount(thisActivity))
                    .getCurrentPlayerId().addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String playerId) {
                    mMyParticipantId = mRoom.getParticipantId(playerId);
                }
            });
        }

        @Override
        public void onDisconnectedFromRoom(@Nullable Room room) {
            // This usually happens due to a network error, leave the game.
            Log.w(TAG, "Disconnected from room...");
            Games.getRealTimeMultiplayerClient(thisActivity, GoogleSignIn.getLastSignedInAccount(thisActivity))
                    .leave(mJoinedRoomConfig, room.getRoomId());
            // show error message and return to main screen
            mRoom = null;
            mJoinedRoomConfig = null;
        }

        @Override
        public void onPeersConnected(@Nullable Room room, List<String> list) {

        }

        @Override
        public void onPeersDisconnected(@Nullable Room room, List<String> list) {
            Log.w(TAG, "Peers disconnected...");
        }

        @Override
        public void onP2PConnected(String participantId) {
            // Update status due to new peer to peer connection.
            Log.w(TAG, "P2PConnected...");
        }

        @Override
        public void onP2PDisconnected(String participantId) {
            // Update status due to  peer to peer connection being disconnected.
            Log.w(TAG, "P2PDisconnected...");
        }
    };

    public void startQuickGame() {
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, 0);
        RoomConfig roomConfig = RoomConfig.builder(mRoomUpdateCallback)
                        .setOnMessageReceivedListener(mMessageReceivedHandler)
                        .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
                        .setAutoMatchCriteria(autoMatchCriteria)
                        .build();
        mJoinedRoomConfig = roomConfig;
        Games.getRealTimeMultiplayerClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .create(roomConfig);
    }
}
