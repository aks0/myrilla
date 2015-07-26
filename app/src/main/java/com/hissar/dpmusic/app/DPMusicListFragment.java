package com.hissar.dpmusic.app;

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
public class DPMusicListFragment extends ListFragment {

  private DPMusicListAdapter mDPMusicListAdapter;
  private List<MusicItem> mMusicItemList;

  @Override
  public View onCreateView(
      LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dpmusic_list_fragment, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mMusicItemList = new ArrayList<MusicItem>();
    mMusicItemList.add(new MusicItem("item 1", "track 1"));
    mMusicItemList.add(new MusicItem("item 2", "track 2"));
    mMusicItemList.add(new MusicItem("item 3", "track 3"));

    mDPMusicListAdapter = new DPMusicListAdapter(getActivity());
    mDPMusicListAdapter.setItemList(mMusicItemList);

    setListAdapter(mDPMusicListAdapter);
  }

  @Override
  public void onListItemClick(ListView list, View view, int position, long id) {
    super.onListItemClick(list, view, position, id);

    MusicItem selectedItem = (MusicItem) getListAdapter().getItem(position);
    Log.d("akshay", "selectedItem: title = " + selectedItem.title);
  }
}
