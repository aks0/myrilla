package com.hissar.myrilla.app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.*;

/**
 * Controller fragment to manage the player and Spotify connection related actions.
 */
public class SpotifyPlayerFragment extends Fragment
    implements PlayerNotificationCallback, ConnectionStateCallback {

  public interface Listener {

    /**
     * Called when the spotify player {@link Player} has been initialized.
     */
    void onPlayerInitialized();

    /**
     * Called when the current playing track has ended.
     */
    void onTrackEnd();
  }

  public static final int REQUEST_CODE_AUTHENTICATION = 1337;

  private static final String SPOTIFY_CLIENT_ID = "417ecb31de084af881bab36ec17034df";
  private static final String REDIRECT_URI = "dp-music://callback";

  private Listener mListener;
  private Player mPlayer;

  public void setListener(Listener listener) {
    mListener = listener;
  }

  public void playTrack(String trackUri) {
    mPlayer.play(trackUri);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    initAuthentication();
  }

  private void initAuthentication() {
    AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(
        SPOTIFY_CLIENT_ID,
        AuthenticationResponse.Type.TOKEN,
        REDIRECT_URI);
    builder.setScopes(new String[]{"user-read-private", "streaming"});
    AuthenticationRequest request = builder.build();

    AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE_AUTHENTICATION, request);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE_AUTHENTICATION) {
      initPlayer(resultCode, data);
      return;
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  private void initPlayer(int resultCode, Intent data) {
    AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
    if (response.getType() != AuthenticationResponse.Type.TOKEN) {
      return;
    }

    Config playerConfig = new Config(getActivity(), response.getAccessToken(), SPOTIFY_CLIENT_ID);
    mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
      @Override
      public void onInitialized(Player player) {
        mPlayer.addConnectionStateCallback(SpotifyPlayerFragment.this);
        mPlayer.addPlayerNotificationCallback(SpotifyPlayerFragment.this);
        Log.d("akshay", "onInitialized()");
        mListener.onPlayerInitialized();
        // mPlayer.play("spotify:track:5J4ZkQpzMUFojo1CtAZYpn");
      }

      @Override
      public void onError(Throwable throwable) {
        Log.e("akshay", "Could not initialize player: " + throwable.getMessage());
      }
    });
  }

  @Override
  public void onDestroy() {
    Spotify.destroyPlayer(this);
    super.onDestroy();
  }

  @Override
  public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
    Log.d("akshay", "Playback event received: " + eventType.name());
    switch (eventType) {
      case END_OF_CONTEXT:
        mListener.onTrackEnd();
        break;

      // Handle event type as necessary
      default:
        break;
    }
  }

  @Override
  public void onPlaybackError(ErrorType errorType, String s) {
    Log.d("akshay", "Playback error received: " + errorType.name());
    switch (errorType) {
      // Handle error type as necessary
      default:
        break;
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
}
