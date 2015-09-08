package com.hissar.myrilla.app;

import android.support.annotation.NonNull;

import java.util.*;

/**
 * Provides the source of truth for the data of the application.
 */
public class DataSource implements Collection<SpotifyTrack> {

  private static DataSource mInstance;

  private List<SpotifyTrack> mSpotifyTrackList;
  private int mCurrentPlayingTrackIndex;

  public static DataSource getInstance() {
    if (mInstance == null) {
      mInstance = new DataSource();
    }

    return mInstance;
  }

  private DataSource() {
    mSpotifyTrackList = new ArrayList<SpotifyTrack>();
    mCurrentPlayingTrackIndex = -1;
  }

  /**
   * Returns the next {@link SpotifyTrack} to play.
   * @return next {@code SpotifyTrack} to play
   * @throws NoSuchElementException if this collection has no more tracks to play
   */
  public SpotifyTrack getPlayNext() {
    if (!canPlayNext()) {
      throw new NoSuchElementException("No more songs to play");
    }

    mCurrentPlayingTrackIndex++;
    return get(mCurrentPlayingTrackIndex);
  }

  /**
   * Returns {@code true} if there is a track present after the current for playing
   * @return
   */
  public boolean canPlayNext() {
    return (mCurrentPlayingTrackIndex + 1) < size();
  }

  /**
   * Sets the {@code spotifyTrack} that is playing currently.
   * @param spotifyTrack
   */
  public void setCurrentPlay(SpotifyTrack spotifyTrack) {
    if (!canPlay(spotifyTrack)) {
      throw new NoSuchElementException("Track not present in the list " + spotifyTrack.id);
    }

    mCurrentPlayingTrackIndex = mSpotifyTrackList.indexOf(spotifyTrack);
  }

  public boolean canPlay(SpotifyTrack spotifyTrack) {
    int trackIndex = mSpotifyTrackList.indexOf(spotifyTrack);
    return trackIndex > mCurrentPlayingTrackIndex;
  }

  public SpotifyTrack get(int index) {
    return mSpotifyTrackList.get(index);
  }

  @Override
  public boolean add(SpotifyTrack spotifyTrack) {
    return mSpotifyTrackList.add(spotifyTrack);
  }

  @Override
  public boolean addAll(Collection collection) {
    return mSpotifyTrackList.addAll(collection);
  }

  @Override
  public void clear() {
    mSpotifyTrackList.clear();
    mCurrentPlayingTrackIndex = -1;
  }

  @Override
  public boolean contains(Object object) {
    if (!(object instanceof SpotifyTrack)) {
      return false;
    }

    SpotifyTrack spotifyTrack = (SpotifyTrack) object;
    return contains(spotifyTrack.id);
  }

  /**
   * Returns {@code true} if this collection contains a {@link SpotifyTrack} with the given
   * {@code spotifyTrackId}.
   * @param spotifyTrackId track id whose presence in this collection is to be tested
   * @return {@code true} if this collection contains the specified track
   */
  public boolean contains(String spotifyTrackId) {
    for (SpotifyTrack spotifyTrack : mSpotifyTrackList) {
      if (spotifyTrack.id.equals(spotifyTrackId)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean isEmpty() {
    return mSpotifyTrackList.isEmpty();
  }

  @NonNull
  @Override
  public Iterator iterator() {
    return mSpotifyTrackList.iterator();
  }

  @Override
  public boolean remove(Object object) {
    return mSpotifyTrackList.remove(object);
  }

  @Override
  public int size() {
    return mSpotifyTrackList.size();
  }

  @NonNull
  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @NonNull
  @Override
  public SpotifyTrack[] toArray(Object[] array) {
    return mSpotifyTrackList.toArray(new SpotifyTrack[size()]);
  }

  @Override
  public boolean retainAll(Collection collection) {
    return mSpotifyTrackList.retainAll(collection);
  }

  @Override
  public boolean removeAll(Collection collection) {
    return mSpotifyTrackList.removeAll(collection);
  }

  @Override
  public boolean containsAll(Collection collection) {
    return false;
  }
}
