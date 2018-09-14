package com.enhan.sabina.midtermapp;

import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Enhan";
    private static final String FRAGMENT_TAG = "main_fragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragmentManager.beginTransaction().add(new MainFragment(),FRAGMENT_TAG).commit();
        }
    }

    public static class MainFragment extends Fragment {
        private MediaPlayer mMediaPlayer;
        private SurfaceView mSurfaceView;
        private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/taeyeon.mp4";
        private String mPortraitVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/protraitVideo.mp4";
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            createMediaPlayer();
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mSurfaceView = getActivity().findViewById(R.id.surface);
            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    mMediaPlayer.setDisplay(surfaceHolder);
                    setSurfaceSize();
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    mMediaPlayer.setDisplay(null);
                }
            });
        }

        private void setSurfaceSize() {
            float videoWidth = mMediaPlayer.getVideoWidth();
            float videoHeight = mMediaPlayer.getVideoHeight();

            View container = (View) mSurfaceView.getParent();
            float containerWidth = container.getWidth();
            float containerHeight = container.getHeight();

            android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
            lp.width = (int) containerWidth;
            lp.height = (int) ((videoHeight / videoWidth) * containerWidth);
            if(lp.height > containerHeight) {
                lp.width = (int) ((videoWidth / videoHeight) * containerHeight);
                lp.height = (int) containerHeight;
            }
            mSurfaceView.setLayoutParams(lp);
        }

        private void createMediaPlayer() {
            Log.d(TAG,"create media player");
            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(mPortraitVideoUrl);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void destroyMediaPlayer() {
            Log.d(TAG, "destroyMediaPlayer");
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
