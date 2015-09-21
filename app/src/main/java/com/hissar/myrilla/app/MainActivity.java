package com.hissar.myrilla.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.parse.Parse;
import com.parse.ParseObject;

public class MainActivity extends Activity {

  private static final String SPOTIFY_PLAYER_FRAGMENT_TAG = "spotify_player_fragment_tag";

  private static final String PARSE_CLIENT_ID = "pCpf1WSUDP2AWFU99nQA5bOR2Jjq71ZbfY34HSqM";
  private static final String PARSE_APPLICATION_ID = "fh1ToF8l7fGtAyX1lw3rJWtsPne19jrXAdmqEIXJ";

  private MyrillaListFragment mMyrillaListFragment;
  private SpotifyPlayerFragment mSpotifyPlayerFragment;
  private MyrillaPlayerOverlayFragment mMyrillaPlayerOverlayFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    hidePlayerOverlayFragment();
    hideClipboardOverlayFragment();
    testParse();
    initSpotifyPlayerFragment();
  }

  private void testParse() {
    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_ID);

    ParseObject testObject = new ParseObject("TestObject");
    testObject.put("foo", "bar");
    testObject.saveInBackground();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == SpotifyPlayerFragment.REQUEST_CODE_AUTHENTICATION) {
      mSpotifyPlayerFragment.onActivityResult(requestCode, resultCode, data);
      return;
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  private void onPlayerInitialized() {
    initListFragment();
    initClipboardOverlayFragment();
  }

  private void initListFragment() {
    mMyrillaListFragment = (MyrillaListFragment) getFragmentManager()
        .findFragmentById(R.id.myrilla_list_fragment);
    mMyrillaListFragment.setListener(
        new MyrillaListFragment.Listener() {
          @Override
          public void onPlayTrack(SpotifyTrack spotifyTrack) {
            mSpotifyPlayerFragment.playTrack(spotifyTrack.uri);
          }
        });
  }

  private void initSpotifyPlayerFragment() {
    mSpotifyPlayerFragment = (SpotifyPlayerFragment) getFragmentManager()
        .findFragmentByTag(SPOTIFY_PLAYER_FRAGMENT_TAG);
    if (mSpotifyPlayerFragment == null) {
      mSpotifyPlayerFragment = new SpotifyPlayerFragment();

      getFragmentManager()
          .beginTransaction()
          .add(mSpotifyPlayerFragment, SPOTIFY_PLAYER_FRAGMENT_TAG)
          .commit();
    }

    mSpotifyPlayerFragment.setListener(
        new SpotifyPlayerFragment.Listener() {
          @Override
          public void onPlayerInitialized() {
            MainActivity.this.onPlayerInitialized();
          }

          @Override
          public void onTrackEnd() {
            mMyrillaListFragment.onTrackEndEvent();
          }

          @Override
          public void onPlay(String trackUri) {
            if (mMyrillaPlayerOverlayFragment == null) {
              initPlayerOverlayFragment();
            }

            SpotifyTrack spotifyTrack = DataSource.getInstance().get(trackUri);
            if (spotifyTrack != null) {
              mMyrillaPlayerOverlayFragment.onPlay(spotifyTrack);
            }
          }

          @Override
          public void onPause(String trackUri) {
            if (mMyrillaPlayerOverlayFragment == null) {
              initPlayerOverlayFragment();
            }

            SpotifyTrack spotifyTrack = DataSource.getInstance().get(trackUri);
            if (spotifyTrack != null) {
              mMyrillaPlayerOverlayFragment.onPause(spotifyTrack);
            }
          }
        });
  }

  private void initPlayerOverlayFragment() {
    mMyrillaPlayerOverlayFragment = (MyrillaPlayerOverlayFragment) getFragmentManager()
        .findFragmentById(R.id.myrilla_player_overlay_fragment);
    mMyrillaPlayerOverlayFragment.setListener(
        new MyrillaPlayerOverlayFragment.Listener() {
          @Override
          public void onPlayClick() {
            mSpotifyPlayerFragment.resumePlay();
          }

          @Override
          public void onPauseClick() {
            mSpotifyPlayerFragment.pausePlay();
          }

          @Override
          public void onNextClick() {

          }
        });

    getFragmentManager()
        .beginTransaction()
        .show(mMyrillaPlayerOverlayFragment)
        .commit();
  }

  private void initClipboardOverlayFragment() {
    final MyrillaClipboardOverlayFragment myrillaClipboardOverlayFragment =
        (MyrillaClipboardOverlayFragment) getFragmentManager()
            .findFragmentById(R.id.myrilla_clipboard_overlay_fragment);
    myrillaClipboardOverlayFragment.setListener(
        new MyrillaClipboardOverlayFragment.Listener() {
          @Override
          public void onCopiedNewTrack(String trackId) {
            Log.d("akshay", "onNewTrack: trackId = " + trackId);
            if (mMyrillaListFragment.shouldAddNewTrack(trackId)) {
              myrillaClipboardOverlayFragment.displayOverlaySuggestion(trackId);
            }
          }

          @Override
          public void onAddNewTrack(SpotifyTrack spotifyTrack) {
            mMyrillaListFragment.addNewTrack(spotifyTrack);
          }
        });
  }

  private void hidePlayerOverlayFragment() {
    MyrillaPlayerOverlayFragment myrillaPlayerOverlayFragment =
        (MyrillaPlayerOverlayFragment) getFragmentManager()
            .findFragmentById(R.id.myrilla_player_overlay_fragment);
    getFragmentManager()
        .beginTransaction()
        .hide(myrillaPlayerOverlayFragment)
        .commit();
  }

  private void hideClipboardOverlayFragment() {
    MyrillaClipboardOverlayFragment myrillaClipboardOverlayFragment =
        (MyrillaClipboardOverlayFragment) getFragmentManager()
            .findFragmentById(R.id.myrilla_clipboard_overlay_fragment);
    getFragmentManager()
        .beginTransaction()
        .hide(myrillaClipboardOverlayFragment)
        .commit();
  }
}
