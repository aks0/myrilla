package com.hissar.dpmusic.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
  public void onResume() {
    super.onResume();

//    testClipboardData();
  }

  private void testClipboardData() {
    ClipboardManager clipboardManager = (ClipboardManager)
        getSystemService(Context.CLIPBOARD_SERVICE);
    /**
     D/akshay  (27943): hasPrimaryClip = true
     D/akshay  (27943): clipData = ClipData { text/plain "Spotify Link" {T:http://open.spotify.com/track/32OlwWuMpZ6b0aN2RZOeMS} }
     D/akshay  (27943): description = ClipDescription { text/plain "Spotify Link" }
     D/akshay  (27943): mimeType = text/plain
     D/akshay  (27943): label = Spotify Link
     D/akshay  (28916): item = ClipData.Item { T:http://open.spotify.com/track/32OlwWuMpZ6b0aN2RZOeMS }
     D/akshay  (28916): itemText = http://open.spotify.com/track/32OlwWuMpZ6b0aN2RZOeMS
     */

    Log.d("akshay", "hasPrimaryClip = " + clipboardManager.hasPrimaryClip());

    if (clipboardManager.hasPrimaryClip()) {
      ClipData clipData = clipboardManager.getPrimaryClip();
      Log.d("akshay", "clipData = " + clipData);
      Log.d("akshay", "description = " + clipboardManager.getPrimaryClipDescription());
      Log.d("akshay", "mimeType = " + clipboardManager.getPrimaryClipDescription().getMimeType(0));
      Log.d("akshay", "label = " + clipboardManager.getPrimaryClipDescription().getLabel());
      Log.d("akshay", "item = " + clipData.getItemAt(0));
      Log.d("akshay", "itemText = " + clipData.getItemAt(0).getText());
    }
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
