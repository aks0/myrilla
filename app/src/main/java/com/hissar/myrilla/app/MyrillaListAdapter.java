package com.hissar.myrilla.app;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * Adapter for the list displayed to the user
 */
public class MyrillaListAdapter extends RecyclerView.Adapter<MyrillaListAdapter.ViewHolder> {

  public interface ItemClickListener {
    void onCardClick(View view, int position, ViewHolder viewHolder);
  }

  private static final int MS_PER_SECOND = 1000;
  private static final int MS_PER_MINUTE = 60 * MS_PER_SECOND;

  private final Context mContext;
  private final Picasso mPicasso;

  private DataSource mDataSource;
  private ItemClickListener mItemClickListener;

  public MyrillaListAdapter(Context context) {
    mContext = context;
    mPicasso = Picasso.with(mContext);
    mDataSource = DataSource.getInstance();
  }

  public void onDataSourceUpdated() {
    mDataSource = DataSource.getInstance();
    notifyDataSetChanged();
  }

  public void setItemClickListener(ItemClickListener itemClickListener) {
    mItemClickListener = itemClickListener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
    View itemView = LayoutInflater.from(mContext)
        .inflate(R.layout.myrilla_list_item, parent, false);
    return new ViewHolder(itemView, mItemClickListener);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    SpotifyTrack spotifyTrack = mDataSource.get(position);

    View convertView = viewHolder.itemView;

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
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return mDataSource.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ItemClickListener itemClickListener;

    public ViewHolder(View itemView, ItemClickListener itemClickListener) {
      super(itemView);

      this.itemClickListener = itemClickListener;
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      itemClickListener.onCardClick(view, getPosition(), this);
    }
  }
}
