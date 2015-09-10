package com.hissar.myrilla.app;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Fragment to display the overlay on the list about the recently copied song.
 */
public class MyrillaClipboardOverlayFragment extends Fragment {

  public interface Listener {

    /**
     * Called when a new spotify track uri {@code trackId} is copied from the clipboard.
     *
     * Example format of the uri: 5J4ZkQpzMUFojo1CtAZYpn
     * @param trackId
     */
    void onCopiedNewTrack(String trackId);

    /**
     * Called when the user has opted to add new track {@code spotifyTrack} to the current list
     * @param spotifyTrack
     */
    void onAddNewTrack(SpotifyTrack spotifyTrack);
  }

  private static final String SPOTIFY_ENDPOINT = "https://api.spotify.com";
  private static final String CLIPBOARD_CONTROLLER_FRAGMENT_TAG = "clipboard_controller_fragment";

  private Picasso mPicasso;
  private SpotifyService mSpotifyService;

  private Listener mListener;

  public void setListener(Listener listener) {
    mListener = listener;
    initClipboard();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater,
      @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.myrilla_clipboard_overlay_fragment, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    mPicasso = Picasso.with(getActivity());
    RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint(SPOTIFY_ENDPOINT)
        .build();
    mSpotifyService = restAdapter.create(SpotifyService.class);
  }

  public void displayOverlaySuggestion(String trackId) {
    mSpotifyService.getTrack(
        trackId,
        new Callback<SpotifyTrack>() {
          @Override
          public void success(SpotifyTrack spotifyTrack, Response response) {
            Log.d("akshay", "copied track fetch success: " + spotifyTrack);
            initOverlayView(spotifyTrack);
            showFragment();
          }

          @Override
          public void failure(RetrofitError retrofitError) {
            Log.d("akshay", "error = " + retrofitError);
          }
        });
  }

  private void initOverlayView(final SpotifyTrack spotifyTrack) {
    TextView subtitleView = (TextView) getView().findViewById(R.id.subtitle);
    subtitleView.setText(spotifyTrack.name);

    ImageView thumbnail = (ImageView) getView().findViewById(R.id.thumbnail);
    Uri uri = Uri.parse(spotifyTrack.album.images.get(0).url);
    mPicasso.load(uri).into(thumbnail);

    getView().setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            hideFragment();
            if (mListener != null) {
              mListener.onAddNewTrack(spotifyTrack);
            }
          }
        });
  }

  private void initClipboard() {
    ClipboardControllerFragment clipboardControllerFragment = new ClipboardControllerFragment();
    clipboardControllerFragment.setListener(
        new ClipboardControllerFragment.Listener() {
          @Override
          public void onNewTrack(String trackId) {
            Log.d("akshay", "onNewTrack: trackId = " + trackId);
            if (mListener != null) {
              mListener.onCopiedNewTrack(trackId);
            }
          }
        });

    getChildFragmentManager()
        .beginTransaction()
        .add(clipboardControllerFragment, CLIPBOARD_CONTROLLER_FRAGMENT_TAG)
        .commit();
  }

  private void hideFragment() {
    getFragmentManager()
        .beginTransaction()
        .hide(this)
        .commit();
  }

  private void showFragment() {
    getFragmentManager()
        .beginTransaction()
        .show(this)
        .commit();
  }
}
