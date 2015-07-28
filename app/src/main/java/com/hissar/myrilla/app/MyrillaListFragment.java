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

  private MyrillaListAdapter mMyrillaListAdapter;
  private List<MusicItem> mMusicItemList = new ArrayList<MusicItem>();

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
    mMyrillaListAdapter.setItemList(mMusicItemList);

    setListAdapter(mMyrillaListAdapter);
  }

  public void maybeAddNewTrack(String trackUri) {
    // track is already present in the list
    for (MusicItem musicItem : mMusicItemList) {
      if (musicItem.trackId.equals(trackUri)) {
        return;
      }
    }

    MusicItem newMusicItem = new MusicItem("New Title", trackUri);
    mMusicItemList.add(newMusicItem);
    mMyrillaListAdapter.setItemList(mMusicItemList);
  }

  @Override
  public void onListItemClick(ListView list, View view, int position, long id) {
    super.onListItemClick(list, view, position, id);

    MusicItem selectedItem = (MusicItem) getListAdapter().getItem(position);
    Log.d("akshay", "selectedItem: title = " + selectedItem.title);
  }
}
