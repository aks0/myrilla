package com.hissar.myrilla.app;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshayk on 7/26/15.
 */
public class MyrillaListFragment extends ListFragment {

  public interface Listener {
    void onPlayTrack(SpotifyTrack spotifyTrack);
  }

  private MyrillaListAdapter mMyrillaListAdapter;
  private Listener mListener;
  private List<SpotifyTrack> mSpotifyTracks = new ArrayList<SpotifyTrack>();
  private int mCurrentPlayingTrackIndex = -1;

  @Override
  public View onCreateView(
      LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.myrilla_list_fragment, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mMyrillaListAdapter = new MyrillaListAdapter(getActivity());
    mMyrillaListAdapter.setItemList(mSpotifyTracks);
    setListAdapter(mMyrillaListAdapter);
  }

  public void setListener(Listener listener) {
    mListener = listener;
  }

  public boolean shouldAddNewTrack(String trackId) {
    // track is already present in the list
    for (SpotifyTrack spotifyTrack : mSpotifyTracks) {
      if (spotifyTrack.id.equals(trackId)) {
        return false;
      }
    }

    return true;
  }

  public void addNewTrack(SpotifyTrack spotifyTrack) {
    mSpotifyTracks.add(spotifyTrack);
    if (mSpotifyTracks.size() == 1) {
      mListener.onPlayTrack(spotifyTrack);
      mCurrentPlayingTrackIndex = 0;
    }

    mMyrillaListAdapter.setItemList(mSpotifyTracks);
  }

  public void onTrackEndEvent() {
    mListener.onPlayTrack(mSpotifyTracks.get(++mCurrentPlayingTrackIndex));
  }

  @Override
  public void onListItemClick(ListView list, View view, int position, long id) {
    super.onListItemClick(list, view, position, id);

    SpotifyTrack selectedItem = (SpotifyTrack) getListAdapter().getItem(position);
    Log.d("akshay", "selectedItem: id = " + selectedItem.id);
  }
}
