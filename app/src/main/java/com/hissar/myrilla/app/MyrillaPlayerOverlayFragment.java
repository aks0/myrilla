package com.hissar.myrilla.app;


import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * Fragment to display the overlay on the list for the player controls and playing song.
 */
public class MyrillaPlayerOverlayFragment extends Fragment {

  public interface Listener {
    void onPlayClick();
    void onPauseClick();
    void onNextClick();
  }

  private Picasso mPicasso;
  private Listener mListener;

  public void setListener(Listener listener) {
    mListener = listener;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater,
      @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.myrilla_player_overlay_fragment, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    mPicasso = Picasso.with(getActivity());
  }

  public void onPlay(SpotifyTrack spotifyTrack) {
    TextView songNameView = (TextView) getView().findViewById(R.id.title);
    songNameView.setText(spotifyTrack.name);

    TextView artistView = (TextView) getView().findViewById(R.id.subtitle);
    artistView.setText(spotifyTrack.artists.get(0).name);

    ImageView thumbnailView = (ImageView) getView().findViewById(R.id.thumbnail);
    Uri uri = Uri.parse(spotifyTrack.album.images.get(0).url);
    mPicasso.setIndicatorsEnabled(true);
    mPicasso.load(uri).into(thumbnailView);
  }
}
