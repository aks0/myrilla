package com.hissar.myrilla.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshayk on 7/26/15.
 */
public class MyrillaListAdapter extends BaseAdapter {

  private final Context mContext;

  private List<MusicItem> mItemList = new ArrayList<MusicItem>();

  public MyrillaListAdapter(Context context) {
    mContext = context;
  }

  public void setItemList(List<MusicItem> itemList) {
    mItemList = itemList;
  }

  @Override
  public int getCount() {
    return mItemList.size();
  }

  @Override
  public Object getItem(int position) {
    return mItemList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext)
          .inflate(R.layout.myrilla_list_item, parent, false);
    }

    TextView titleView = (TextView) convertView.findViewById(R.id.title);
    titleView.setText("Position #" + position);
    return convertView;
  }
}
