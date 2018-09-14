package com.enhan.sabina.midtermapp;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "Enhan";
    private static final String FRAGMENT_TAG = "main_fragment";

    private VideoControllerView mMediaController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMediaController = new VideoControllerView(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragmentManager.beginTransaction().add(new MainFragment(),FRAGMENT_TAG).commit();
        }
    }


    public VideoControllerView getController() {
        return mMediaController;
    }
    public static class MainFragment extends Fragment implements MediaPlayer.OnBufferingUpdateListener,
            VideoControllerView.MediaPlayerControl,
            MediaPlayer.OnPreparedListener{
        private MediaPlayer mMediaPlayer;
        private SurfaceView mSurfaceView;
        private int mPercentage = 0;
        private VideoControllerView mMediaController;
        private Handler mHandler = new Handler();
        private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/taeyeon.mp4";
        private String mPortraitVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/protraitVideo.mp4";
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            createMediaPlayer();

        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mSurfaceView = getActivity().findViewById(R.id.surface);

            mMediaController = ((MainActivity)getActivity()).getController();
            if (getResources().getConfiguration().orientation == 1) {
                mMediaController.changeTimeout(0);
            } else {
                mMediaController.changeTimeout(3000);
            }
            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(mMediaController != null){
                        mHandler.post(new Runnable() {

                            public void run() {
//                                mMediaController.setEnabled(true);
                                mMediaController.show();
                            }
                        });
                    }
                    return false;
                }
            });

            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    mMediaPlayer.setDisplay(surfaceHolder);
                    setMediaController();
                    setSurfaceSize();


                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    mMediaPlayer.setDisplay(null);
                    mMediaController.hide();
                }
            });
        }
        private void setMediaController() {
            mMediaController.setMediaPlayer(this);
            mMediaController.setAnchorView((FrameLayout)(getActivity().findViewById(R.id.video_surface_container)));
            mHandler.post(new Runnable() {

                public void run() {
                    mMediaController.setEnabled(true);
                    mMediaController.show();
                }
            });
        }

        private void setSurfaceSize() {
            float videoWidth = mMediaPlayer.getVideoWidth();
            float videoHeight = mMediaPlayer.getVideoHeight();
            Log.d("EnHan",""+videoHeight + " " + videoWidth);

            if (videoHeight > videoWidth) {
                getActivity().findViewById(R.id.video_surface_container).setBackgroundColor(Color.BLACK);
            }
//
            View container = (View) mSurfaceView.getParent();
            float containerWidth = container.getWidth();
            float containerHeight = container.getHeight();
            android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
//            if (getResources().getConfiguration().orientation == 1) {
//                lp.width = (int) videoWidth;
//                lp.height = (int) videoHeight;
//            } else {
//                lp.width = (int) videoWidth;
//                lp.height = (int) videoHeight;
//            }
//
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

                mMediaPlayer.setDataSource(mVideoUrl);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnBufferingUpdateListener(this);
                mMediaPlayer.prepare();


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


        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            mPercentage = i;
        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mMediaPlayer.start();

            Log.d(TAG,"onpreparingmediacontroller");

        }

        @Override
        public void start() {
            mMediaPlayer.start();
        }

        @Override
        public void pause() {
            mMediaPlayer.pause();
        }

        @Override
        public int getDuration() {
            return mMediaPlayer.getDuration();
        }

        @Override
        public int getCurrentPosition() {
            return mMediaPlayer.getCurrentPosition();
        }

        @Override
        public void seekTo(int pos) {
            mMediaPlayer.seekTo(pos);
        }

        @Override
        public boolean isPlaying() {
            return mMediaPlayer.isPlaying();
        }

        @Override
        public int getBufferPercentage() {
            return mPercentage;
        }

        @Override
        public boolean canPause() {
            return true;
        }

        @Override
        public boolean canSeekBackward() {
            return true;
        }

        @Override
        public boolean canSeekForward() {
            return true;
        }

        @Override
        public boolean isFullScreen() {
            boolean isFullScreen = (getResources().getConfiguration().orientation == 2);
            return isFullScreen;
        }

        @Override
        public void toggleFullScreen() {
            if (getResources().getConfiguration().orientation == 1) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


            } else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }
        }

        @Override
        public void toggleSound(boolean sound) {
            if (sound) {
                mMediaPlayer.setVolume(1,1);
            } else {
                mMediaPlayer.setVolume(0,0);
            }
        }
//
//        @Override
//        public void start() {
//            mMediaPlayer.start();
//        }
//
//        @Override
//        public void pause() {
//            mMediaPlayer.pause();
//        }
//
//        @Override
//        public int getDuration() {
//            return mMediaPlayer.getDuration();
//        }
//
//        @Override
//        public int getCurrentPosition() {
//            return mMediaPlayer.getCurrentPosition();
//        }
//
//        @Override
//        public void seekTo(int i) {
//            mMediaPlayer.seekTo(i);
//        }
//
//        @Override
//        public boolean isPlaying() {
//            return mMediaPlayer.isPlaying();
//        }
//
//        @Override
//        public int getBufferPercentage() {
//            return 0;
//        }
//
//        @Override
//        public boolean canPause() {
//            return true;
//        }
//
//        @Override
//        public boolean canSeekBackward() {
//            return true;
//        }
//
//        @Override
//        public boolean canSeekForward() {
//            return true;
//        }
//
//        @Override
//        public int getAudioSessionId() {
//            return 0;
//        }
    }
}
