package com.hissar.myrilla.app;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller fragment to manage the data copied to the clipboard by other applications.
 */
public class ClipboardFragment extends Fragment {

  public interface Listener {

    /**
     * Called when a new spotify track uri is parsed from the Clipboard.
     *
     * Example format of the uri: spotify:track:5J4ZkQpzMUFojo1CtAZYpn
     * @param trackUri
     */
    void onNewTrack(String trackUri);
  }

  private static final String LABEL = "Spotify Link";
  private static final String REGEX = "^http://open\\.spotify\\.com/track/([\\d\\w\\W]+)$";
  private static final int REGEX_TRACK_ID_GROUP = 1;
  private static final String PLAYER_URI_FORMAT = "spotify:track:%s";

  private Listener mListener;

  public void setListener(Listener listener) {
    mListener = listener;
  }

  @Override
  public void onResume() {
    super.onResume();

    maybeExtractTextFromClipboard();
  }

  private void maybeExtractTextFromClipboard() {
    ClipboardManager clipboard =
        (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

    // Verify that there is a copied clip on the clipboard
    if (!clipboard.hasPrimaryClip()) {
      return;
    }

    // Verify that there is a plain text copy present on the clipboard
    if (!clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
      return;
    }

    // Verify that the copied text is from Spotify
    if (!LABEL.equals(clipboard.getPrimaryClipDescription().getLabel())) {
      return;
    }

    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
    String webUri = item.getText().toString();
    String trackId = getSpotifyTrackId(webUri);
    String trackUri = String.format(PLAYER_URI_FORMAT, trackId);
    if (mListener != null) {
      mListener.onNewTrack(trackUri);
    }
  }

  /**
   * Parses the track id from the spotify {@code webUri}.
   * @param webUri web link for the track from Spotify
   * @return
   */
  private String getSpotifyTrackId(String webUri) {
    Pattern pattern = Pattern.compile(REGEX);
    Matcher matcher = pattern.matcher(webUri);
    if (matcher.find()) {
      return matcher.group(REGEX_TRACK_ID_GROUP);
    }

    throw new IllegalStateException("Unable to find trackId from the spotify webUri " + webUri);
  }
}
