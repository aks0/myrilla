package com.hissar.myrilla.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.*;

public class MainActivity extends Activity implements
    PlayerNotificationCallback, ConnectionStateCallback {

  private static final String CLIENT_ID = "417ecb31de084af881bab36ec17034df";
  private static final String REDIRECT_URI = "dp-music://callback";

  private static final String CLIPBOARD_FRAGMENT_TAG = "clipboard_fragment";

  private static final int REQUEST_CODE = 1337;

  private Player mPlayer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(
        CLIENT_ID,
        AuthenticationResponse.Type.TOKEN,
        REDIRECT_URI);
    builder.setScopes(new String[]{"user-read-private", "streaming"});
    AuthenticationRequest request = builder.build();

    AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    ClipboardFragment clipboardFragment = new ClipboardFragment();
    clipboardFragment.setListener(
        new ClipboardFragment.Listener() {
          @Override
          public void onNewTrack(String trackUri) {
            Log.d("akshay", "onNewTrack: trackUri = " + trackUri);
            if (mPlayer != null) {
              mPlayer.play(trackUri);
            }
          }
        });

    getFragmentManager()
        .beginTransaction()
        .add(clipboardFragment, CLIPBOARD_FRAGMENT_TAG)
        .commit();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);

    // Check if result comes from the correct activity
    if (requestCode == REQUEST_CODE) {
      AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
      if (response.getType() == AuthenticationResponse.Type.TOKEN) {
        Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
        mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
          @Override
          public void onInitialized(Player player) {
            mPlayer.addConnectionStateCallback(MainActivity.this);
            mPlayer.addPlayerNotificationCallback(MainActivity.this);
            mPlayer.play("spotify:track:5J4ZkQpzMUFojo1CtAZYpn");
          }

          @Override
          public void onError(Throwable throwable) {
            Log.e("akshay", "Could not initialize player: " + throwable.getMessage());
          }
        });
      }
    }
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
}
