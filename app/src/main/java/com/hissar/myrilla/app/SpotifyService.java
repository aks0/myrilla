package com.hissar.myrilla.app;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by akshayk on 7/26/15.
 */
public interface SpotifyService {

  @GET("/v1/tracks/{id}")
  void getTrack(@Path("id") String trackId, Callback<SpotifyTrack> callback);
}
