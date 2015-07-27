package com.hissar.myrilla.app;

/**
 * Created by akshayk on 7/26/15.
 */
public class SpotifyTrack {

  private int track_number;
  private String type;
  private String uri;

  @Override
  public String toString() {
    return track_number + " - " + type + " - " + uri;
  }
}
