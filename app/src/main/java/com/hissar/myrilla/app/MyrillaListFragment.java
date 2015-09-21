package com.hissar.myrilla.app;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment to display the list of tracks in the queue.
 */
public class MyrillaListFragment extends Fragment {

  public interface Listener {

    void onPlayTrack(SpotifyTrack spotifyTrack);
  }

  private RecyclerView mRecyclerView;
  private MyrillaListAdapter mMyrillaListAdapter;
  private LinearLayoutManager mLinearLayoutManager;

  private Listener mListener;
  private DataSource mDataSource;

  @Override
  public View onCreateView(
      LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.myrilla_list_fragment, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    mRecyclerView = (RecyclerView) view.findViewById(R.id.myrilla_recycler_view);

    // use this setting to improve performance if you know that changes
    // in content do not change the layout size of the RecyclerView
    mRecyclerView.setHasFixedSize(true);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // use a linear layout manager
    mLinearLayoutManager = new LinearLayoutManager(getActivity());
    mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    mRecyclerView.setLayoutManager(mLinearLayoutManager);

    mMyrillaListAdapter = new MyrillaListAdapter(getActivity());
    mMyrillaListAdapter.setItemClickListener(
        new MyrillaListAdapter.ItemClickListener() {
          @Override
          public void onCardClick(
              View view,
              int position,
              MyrillaListAdapter.ViewHolder viewHolder) {
            SpotifyTrack nextTrackToPlay = mDataSource.get(position);
            if (mDataSource.canPlay(nextTrackToPlay)) {
              mDataSource.setCurrentPlay(nextTrackToPlay);
              mListener.onPlayTrack(nextTrackToPlay);
            }
          }
        });
    mRecyclerView.setAdapter(mMyrillaListAdapter);

    mDataSource = DataSource.getInstance();
  }

  public void setListener(Listener listener) {
    mListener = listener;
  }

  public boolean shouldAddNewTrack(String trackId) {
    // track is already present in the list
    for (SpotifyTrack spotifyTrack : mDataSource) {
      if (spotifyTrack.id.equals(trackId)) {
        return false;
      }
    }

    return true;
  }

  public void addNewTrack(SpotifyTrack spotifyTrack) {
    mDataSource.add(spotifyTrack);
    mMyrillaListAdapter.onDataSourceUpdated();

    if (mDataSource.size() == 1 && mDataSource.canPlay(spotifyTrack)) {
      mListener.onPlayTrack(spotifyTrack);
      mDataSource.setCurrentPlay(spotifyTrack);
    }
  }

  public void onTrackEndEvent() {
    if (!mDataSource.canPlayNext()) {
      return;
    }

    mListener.onPlayTrack(mDataSource.getPlayNext());
  }
}
