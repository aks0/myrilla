package com.hissar.myrilla.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.parse.Parse;
import com.parse.ParseObject;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.*;

public class MainActivity extends Activity implements
    PlayerNotificationCallback, ConnectionStateCallback {

  private static final String SPOTIFY_CLIENT_ID = "417ecb31de084af881bab36ec17034df";
  private static final String REDIRECT_URI = "dp-music://callback";

  private static final String PARSE_CLIENT_ID = "pCpf1WSUDP2AWFU99nQA5bOR2Jjq71ZbfY34HSqM";
  private static final String PARSE_APPLICATION_ID = "fh1ToF8l7fGtAyX1lw3rJWtsPne19jrXAdmqEIXJ";

  private static final int REQUEST_CODE = 1337;

  private Player mPlayer;
  private MyrillaListFragment mMyrillaListFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    hideOverlayFragment();
    AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(
        SPOTIFY_CLIENT_ID,
        AuthenticationResponse.Type.TOKEN,
        REDIRECT_URI);
    builder.setScopes(new String[]{"user-read-private", "streaming"});
    AuthenticationRequest request = builder.build();

    AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_ID);

    ParseObject testObject = new ParseObject("TestObject");
    testObject.put("foo", "bar");
    testObject.saveInBackground();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);

    // Check if result comes from the correct activity
    if (requestCode == REQUEST_CODE) {
      AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
      if (response.getType() == AuthenticationResponse.Type.TOKEN) {
        Config playerConfig = new Config(this, response.getAccessToken(), SPOTIFY_CLIENT_ID);
        mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
          @Override
          public void onInitialized(Player player) {
            mPlayer.addConnectionStateCallback(MainActivity.this);
            mPlayer.addPlayerNotificationCallback(MainActivity.this);
            MainActivity.this.onPlayerInitialized();
//            mPlayer.play("spotify:track:5J4ZkQpzMUFojo1CtAZYpn");
          }

          @Override
          public void onError(Throwable throwable) {
            Log.e("akshay", "Could not initialize player: " + throwable.getMessage());
          }
        });
      }
    }
  }

  private void onPlayerInitialized() {
    mMyrillaListFragment = (MyrillaListFragment) getFragmentManager()
        .findFragmentById(R.id.myrilla_list_fragment);
    mMyrillaListFragment.setListener(
        new MyrillaListFragment.Listener() {
          @Override
          public void onPlayTrack(SpotifyTrack spotifyTrack) {
            mPlayer.play(spotifyTrack.uri);
          }
        });

    final MyrillaOverlayFragment myrillaOverlayFragment = (MyrillaOverlayFragment)
        getFragmentManager().findFragmentById(R.id.myrilla_overlay_fragment);
    myrillaOverlayFragment.setListener(
        new MyrillaOverlayFragment.Listener() {
          @Override
          public void onCopiedNewTrack(String trackId) {
            Log.d("akshay", "onNewTrack: trackId = " + trackId);
            if (mMyrillaListFragment.shouldAddNewTrack(trackId)) {
              myrillaOverlayFragment.displayOverlaySuggestion(trackId);
            }
          }

          @Override
          public void onAddNewTrack(SpotifyTrack spotifyTrack) {
            mMyrillaListFragment.addNewTrack(spotifyTrack);
          }
        });
  }

  @Override
  public void onLoggedIn() {
    Log.d("akshay", "User logged in");
  }

  @Override
  public void onLoggedOut() {
    Log.d("akshay", "User logged out");
  }

  @Override
  public void onLoginFailed(Throwable error) {
    Log.d("akshay", "Login failed");
  }

  @Override
  public void onTemporaryError() {
    Log.d("akshay", "Temporary error occurred");
  }

  @Override
  public void onConnectionMessage(String message) {
    Log.d("akshay", "Received connection message: " + message);
  }

  @Override
  public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
    Log.d("akshay", "Playback event received: " + eventType.name());
    switch (eventType) {
      case END_OF_CONTEXT:
        mMyrillaListFragment.onTrackEndEvent();
        break;

      // Handle event type as necessary
      default:
        break;
    }
  }

  @Override
  public void onPlaybackError(ErrorType errorType, String errorDetails) {
    Log.d("akshay", "Playback error received: " + errorType.name());
    switch (errorType) {
      // Handle error type as necessary
      default:
        break;
    }
  }

  @Override
  protected void onDestroy() {
    Spotify.destroyPlayer(this);
    super.onDestroy();
  }

  private void hideOverlayFragment() {
    MyrillaOverlayFragment myrillaOverlayFragment = (MyrillaOverlayFragment)
        getFragmentManager().findFragmentById(R.id.myrilla_overlay_fragment);
    getFragmentManager()
        .beginTransaction()
        .hide(myrillaOverlayFragment)
        .commit();
  }
}
