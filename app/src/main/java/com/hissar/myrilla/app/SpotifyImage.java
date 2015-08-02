package com.hissar.myrilla.app;

/**
 * Created by akshayk on 8/1/15.
 */
public class SpotifyImage {

  public int height;
  public int width;
  public String url;

  @Override
  public String toString() {
    return height + " x " + width + ": " + url;
  }
}
