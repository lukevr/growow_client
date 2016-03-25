package robotsmom.growow;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.MediaController;

/**
 * Created by luke on 3/24/16. It is not possible to draw over SurfaceView should be replaced with VideoView (TextureView)
 */
public class FarmFragment extends Fragment implements TextureView.SurfaceTextureListener, VideoStateListener {

    private RelativeLayout root;
    private VideoView vidView;
    private TextureView mTextureView;
    private ProgressBar circleProgress;
    private String vSource = "rtsp://178.214.221.154:1935/live/myStream";

    private FarmFragment.RenderingThread mThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.farm_view, container, false);
        root = (RelativeLayout) view.findViewById(R.id.root_layout);

        vidView = new VideoView(getContext(), this);
        vidView.setVideoPath(vSource);
        vidView.setMediaController(new MediaController(getContext()));

        root.addView(vidView);

        vidView.start();

        circleProgress = (ProgressBar) view.findViewById(R.id.circleProgress);
        circleProgress.bringToFront();

        return view;
    }



    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mThread = new RenderingThread(mTextureView);
        mThread.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mThread != null) mThread.stopRendering();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void videoStateCallback(int state) {

        switch (state) {
            case(VideoView.STATE_PLAYING):
                circleProgress.setVisibility(View.GONE);
                mTextureView = new TextureView(getContext());
                mTextureView.setSurfaceTextureListener(this);
                mTextureView.setOpaque(false);
                root.addView(mTextureView);

        }

    }

    private static class RenderingThread extends Thread {
        private final TextureView mSurface;
        private volatile boolean mRunning = true;

        public RenderingThread(TextureView surface) {
            mSurface = surface;
        }

        @Override
        public void run() {
            float x = 0.0f;
            float y = 0.0f;
            float speedX = 5.0f;
            float speedY = 3.0f;

            Paint paint = new Paint();
            paint.setColor(0xff00ff00);

            while (mRunning && !Thread.interrupted()) {
                final Canvas canvas = mSurface.lockCanvas(null);
                try {
                    canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
                    canvas.drawRect(x, y, x + 20.0f, y + 20.0f, paint);
                } finally {
                    mSurface.unlockCanvasAndPost(canvas);
                }

                if (x + 20.0f + speedX >= mSurface.getWidth() || x + speedX <= 0.0f) {
                    speedX = -speedX;
                }
                if (y + 20.0f + speedY >= mSurface.getHeight() || y + speedY <= 0.0f) {
                    speedY = -speedY;
                }

                x += speedX;
                y += speedY;

                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    // Interrupted
                }
            }
        }

        void stopRendering() {
            interrupt();
            mRunning = false;
        }
    }


}
