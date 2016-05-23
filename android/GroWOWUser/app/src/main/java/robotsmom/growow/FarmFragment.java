package robotsmom.growow;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

import retrofit2.adapter.rxjava.HttpException;
import robotsmom.growow.restapi.ApiService;
import robotsmom.growow.restapi.ApiService.ServerAPIInterface;
import robotsmom.growow.restapi.model.OKJson;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by luke on 3/24/16. It is not possible to draw over SurfaceView should be replaced with VideoView (TextureView)
 */
public class FarmFragment extends Fragment implements TextureView.SurfaceTextureListener, VideoStateListener {

    private static final String LOG_TAG = "FarmFragment";
    private RelativeLayout root;
    private RelativeLayout _containerView;
    private VideoView vidView;
    private FarmGridView gridView;
    private ProgressBar circleProgress;
//    private String vSource = "rtsp://178.214.221.154:1935/live/myStream";
    private String vSource = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";

    FarmBed _bed;

    private ServerAPIInterface apiService;
    private Subscription subscription;

//    private FarmFragment.RenderingThread mThread;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        apiService = new ApiService().getApi();
        _bed = new FarmBed(690, 450, vSource);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        Log.d(LOG_TAG, "Vew created with container size: " + _containerView.getWidth() + "x" + _containerView.getHeight());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.farm_view, container, false);
        root = (RelativeLayout) view.findViewById(R.id.root_layout);

        MediaController mc = new MediaController(getContext());
        mc.setEnabled(false);

        vidView = new VideoView(getContext(), this);
        vidView.setVideoPath(_bed.getStreamURL());
        vidView.setMediaController(mc);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        vidView.setLayoutParams(params);

        _containerView = (RelativeLayout) view.findViewById(R.id.grid_container);
        _containerView.addView(vidView);
        vidView.start();

        circleProgress = (ProgressBar) view.findViewById(R.id.circleProgress);
        circleProgress.bringToFront();

        intentVideoCall();

        return view;
    }

//    @Override
//    public boolean onTouch(View view, MotionEvent event)
//    {
//        Log.d(LOG_TAG, "Got touch in (" + (event.getX() - gridView.getLeft()) + ", " + (event.getY() - gridView.getTop()) + ")");
//        Log.d(LOG_TAG, "view.Left(" + view.getLeft() + "), Right(" + view.getRight() + ")" + ", Top(" + view.getTop() + ")");
//        Log.d(LOG_TAG, "textureView.Left(" + gridView.getLeft() + "), Right(" + gridView.getRight() + ")" + ", Top(" + gridView.getTop() + ")");
//        drawGrid(gridView, 5, 3, 1.f, 1.f);
//
//        return false;
//    }
//


    @Override
    public void onStop() {
        stopVideoCall();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        apiService = null;
        super.onDestroy();
    }


    private void intentVideoCall() {

        subscription = apiService
                .intentVideo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<OKJson>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getContext(),
                                "Completed",
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // cast to retrofit.HttpException to get the response code
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            int code = response.code();
                        }
                    }

                    @Override
                    public void onNext(OKJson okJson) {
                        Toast.makeText(getContext(),
                                "Result is " + okJson.getResult(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void stopVideoCall() {

        subscription = apiService
                .stopVideo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<OKJson>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getContext(),
                                "Stream is Completed",
                                Toast.LENGTH_SHORT)
                                .show();
                        Log.d(LOG_TAG, "Stream is completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        // cast to retrofit.HttpException to get the response code
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            int code = response.code();
                        }
                        Toast.makeText(getContext(),
                                "Error in stream",
                                Toast.LENGTH_SHORT)
                                .show();
                        Log.d(LOG_TAG, "Stream experienced an error");
                    }

                    @Override
                    public void onNext(OKJson okJson) {
                        Toast.makeText(getContext(),
                                "Result is " + okJson.getResult(),
                                Toast.LENGTH_SHORT)
                                .show();
                        Log.d(LOG_TAG, "Something new arrived");
                    }
                });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//        mThread = new RenderingThread(gridView);
//        mThread.start();
        //gridView.drawGrid(gridView, 5, 4, .0f, .0f);
        //vidView.setLayoutParams(new RelativeLayout.LayoutParams(height, height));
        vidView.setViewScale(height, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(LOG_TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        if (mThread != null)
//            mThread.stopRendering();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.i(LOG_TAG, "onSurfaceTextureUpdated");
    }

    @Override
    public void videoStateCallback(int state)
    {
        Log.i(LOG_TAG, "Videostate was changed to: " + state);
        switch (state)
        {
            case(VideoView.STATE_PREPARED):
            {
                circleProgress.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                vidView.setLayoutParams(params);
                gridView = new FarmGridView(getContext());
                gridView.setSurfaceTextureListener(this);
                gridView.setOpaque(false);
                gridView.setLeft(10);
                gridView.setTop(10);
                gridView.setLayoutParams(new FrameLayout.LayoutParams(vidView.getWidth(), vidView.getHeight(), FrameLayout.LayoutParams.MATCH_PARENT));
                this.getView().setOnTouchListener(gridView);

                // adding fake perspective to grid
                gridView.setPerspective(0.f, 0.f, 0.f, .0f, .0f, .0f, .0f, .0f);
                _containerView.addView(gridView);
                // make container proportional to bed size
                float bedAspect = _bed.getWidth() / _bed.getHeight();
                params = new RelativeLayout.LayoutParams((int)(_containerView.getHeight() * bedAspect),
                                                        _containerView.getHeight());
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                _containerView.setLayoutParams(params);
            }
        }
    }

    public void setResizeStream(boolean resizeStream) {
        vidView.setResizeStream(resizeStream);
    }

//    private static class RenderingThread extends Thread
//    {
//        private final FarmGridView mSurface;
//        private volatile boolean mRunning = true;
//
//        public RenderingThread(FarmGridView surface) {
//            mSurface = surface;
//        }
//
//        @Override
//        public void run()
//        {
//            Random rnd =  new Random(23);
//            float x = (float)rnd.nextInt(mSurface.getWidth());
//            float y = (float)rnd.nextInt(mSurface.getHeight());
//
//            float speedX = (float)rnd.nextInt(10);//5.0f;
//            float speedY = (float)rnd.nextInt(10);//3.0f;
//
//            float deltaX = 20.f;
//            float deltaY = 20.f;
//
//            Paint paint = new Paint();
//            paint.setColor(0xff00ff00);
//
//            while (mRunning && !Thread.interrupted())
//            {
//                final Canvas canvas = mSurface.lockCanvas(null);
//                try {
//                    canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
//                    canvas.drawRect(x, y, x + deltaX, y + deltaY, paint);
//                } finally {
//                    mSurface.unlockCanvasAndPost(canvas);
//                }
//
//                if (x + deltaX + speedX >= mSurface.getWidth() || x + speedX <= 0.0f) {
//                    speedX = -speedX;
//                }
//                if (y + deltaY + speedY >= mSurface.getHeight() || y + speedY <= 0.0f) {
//                    speedY = -speedY;
//                }
//
//                x += speedX;
//                y += speedY;
//
//                try {
//                    Thread.sleep(15);
//                } catch (InterruptedException e) {
//                    // Interrupted
//                }
//            }
//        }
//
//        void stopRendering() {
//            interrupt();
//            mRunning = false;
//        }
//    }


}
