package com.hissar.myrilla.app;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshayk on 7/26/15.
 */
public class MyrillaListAdapter extends BaseAdapter {

  private static final int MS_PER_SECOND = 1000;
  private static final int MS_PER_MINUTE = 60 * MS_PER_SECOND;

  private final Context mContext;
  private final Picasso mPicasso;

  private List<SpotifyTrack> mItemList = new ArrayList<SpotifyTrack>();

  public MyrillaListAdapter(Context context) {
    mContext = context;
    mPicasso = Picasso.with(mContext);
  }

  public void setItemList(List<SpotifyTrack> itemList) {
    mItemList = itemList;
    notifyDataSetChanged();
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

    SpotifyTrack spotifyTrack = mItemList.get(position);

    TextView songNameView = (TextView) convertView.findViewById(R.id.song_name);
    songNameView.setText(spotifyTrack.name);

    TextView artistView = (TextView) convertView.findViewById(R.id.artist);
    artistView.setText(spotifyTrack.artists.get(0).name);

    TextView timeView = (TextView) convertView.findViewById(R.id.time);
    int minutes = spotifyTrack.duration_ms / MS_PER_MINUTE;
    int seconds = (spotifyTrack.duration_ms % MS_PER_MINUTE) / MS_PER_SECOND;
    timeView.setText(String.format("%d:%d", minutes, seconds));

    ImageView thumbnailView = (ImageView) convertView.findViewById(R.id.thumbnail);
    Uri uri = Uri.parse(spotifyTrack.album.images.get(0).url);
    mPicasso.setIndicatorsEnabled(true);
    mPicasso.load(uri).into(thumbnailView);
    return convertView;
  }
}
