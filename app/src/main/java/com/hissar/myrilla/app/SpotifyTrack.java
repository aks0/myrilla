package com.hissar.myrilla.app;

import java.util.List;

/**
 * Created by akshayk on 7/26/15.
 */
public class SpotifyTrack {

  public String id;
  public String uri;
  public String name;
  public int duration_ms;
  public List<SpotifyArtist> artists;
  public SpotifyAlbum album;

  @Override
  public String toString() {
    return id + " - " + uri + " - " + name + " - " + artists + " - " + duration_ms + " - " + album;
  }
}
