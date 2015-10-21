/************************************************************
* Project		: aMuse
* Team			: dVruhero
* Page			: ScanSound.java
* Description	: Android application class (scan sound)
* Released		: May 23th, 2013 (Third and Last Deliverable)
************************************************************/

package com.unitn.amuse;

/**
 * Android class sound (scan sound).
 * @author Ramponi Alan
 */

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ScanSound extends Activity {
    private MediaPlayer mMediaPlayer;
    
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        playAudio();
    }

    private void playAudio () {
        try {
            mMediaPlayer = MediaPlayer.create(this, R.raw.beep);
            mMediaPlayer.setLooping(false);
            Log.e("beep","started0");
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            	public void onCompletion(MediaPlayer arg0) {
            		finish();
                }
            });
        } catch (Exception e) {
            Log.e("beep", "error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}