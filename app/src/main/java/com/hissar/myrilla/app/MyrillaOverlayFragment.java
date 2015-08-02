package com.hissar.myrilla.app;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

/**
 * Fragment to display the overlay on the list about the recently copied song.
 */
public class MyrillaOverlayFragment extends Fragment {

  public interface Listener {

    /**
     * Called when a new spotify track uri is selected to be added
     * {@code trackId}.
     *
     * Example format of the uri: 5J4ZkQpzMUFojo1CtAZYpn
     * @param trackId
     */
    void onNewTrack(String trackId);
  }

  private Listener mListener;
  private Picasso mPicasso;

  @Override
  public View onCreateView(
      LayoutInflater inflater,
      @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.myrilla_overlay_fragment, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    mPicasso = Picasso.with(getActivity());

    ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
    Uri uri = Uri.parse("http://dbsjeyaraj.com/dbsj/wp-content/uploads/2014/11/Deepika_5.jpg");
    mPicasso.load(uri).into(thumbnail);
  }

  public void setListener(Listener listener) {
    mListener = listener;
  }
}
