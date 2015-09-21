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

  private boolean mIsTrackPlaying;
  private TextView mSongName;
  private TextView mArtistName;
  private ImageView mThumbnailView;
  private ImageView mMediaButton;

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

    mSongName = (TextView) view.findViewById(R.id.title);
    mArtistName = (TextView) view.findViewById(R.id.subtitle);
    mThumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
    mMediaButton = (ImageView) view.findViewById(R.id.media_actions);

    mMediaButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (mIsTrackPlaying) {
              mListener.onPauseClick();
            } else {
              mListener.onPlayClick();
            }
          }
        });
  }

  public void onPlay(SpotifyTrack spotifyTrack) {
    setUpOverlay(spotifyTrack, true);
  }

  public void onPause(SpotifyTrack spotifyTrack) {
    setUpOverlay(spotifyTrack, false);
  }

  private void setUpOverlay(SpotifyTrack spotifyTrack, boolean isTrackPlaying) {
    mIsTrackPlaying = isTrackPlaying;

    mSongName.setText(spotifyTrack.name);
    mArtistName.setText(spotifyTrack.artists.get(0).name);

    Uri uri = Uri.parse(spotifyTrack.album.images.get(0).url);
    mPicasso.setIndicatorsEnabled(true);
    mPicasso.load(uri).into(mThumbnailView);

    mMediaButton.setImageResource(
        isTrackPlaying ? R.drawable.ic_pause_black_36dp : R.drawable.ic_play_arrow_black_36dp);
  }
}
