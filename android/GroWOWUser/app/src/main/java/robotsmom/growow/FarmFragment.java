package robotsmom.growow;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    private RelativeLayout mRoot;
    private RelativeLayout mContainerView;
    private VideoView mVidView;
    private FarmGridView mGridView;
    private ProgressBar mCircleProgress;
    FarmField mField;
    Farm mFarm;

    private ServerAPIInterface mAPIService;
    private Subscription mSubscription;

//    private FarmFragment.RenderingThread mThread;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAPIService = new ApiService().getApi();

        try {
            JSONObject json = JSONLoader.getResourceConfiguration(R.raw.testfield, getContext());
            mFarm = new Farm(json.getJSONObject("farm"));
            mField = mFarm.getFarmFields().get(0);
        } catch (IOException e)
        {
            Log.e(LOG_TAG, "Error reading config JSON");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.farm_view, container, false);
        mRoot = (RelativeLayout) view.findViewById(R.id.root_layout);

        MediaController mc = new MediaController(getContext());
        mc.setEnabled(false);

        mVidView = new VideoView(getContext(), this);
        mVidView.setVideoPath(mField.getStreamURL());
        mVidView.setDistorsion(mField.getDistorsion());
        mVidView.setMediaController(mc);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mVidView.setLayoutParams(params);

        mContainerView = (RelativeLayout) view.findViewById(R.id.grid_container);
        ViewTreeObserver vto = mContainerView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                Log.d("TEST", "Height = " + mContainerView.getHeight() + " Width = " + mContainerView.getWidth());
                ViewTreeObserver obs = mContainerView.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                mContainerView.setLayoutParams(params);
                // make container proportional to bed size
                float bedAspect = mField.getWidth() / mField.getHeight();
                params = new RelativeLayout.LayoutParams((int)(mContainerView.getHeight() * bedAspect), mContainerView.getHeight());
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mContainerView.setLayoutParams(params);

                mContainerView.addView(mVidView);
                mVidView.start();
            }
        });


        mCircleProgress = (ProgressBar) view.findViewById(R.id.circleProgress);
        mCircleProgress.bringToFront();

//        intentVideoCall();

        return view;
    }

//    @Override
//    public boolean onTouch(View view, MotionEvent event)
//    {
//        Log.d(LOG_TAG, "Got touch in (" + (event.getX() - mGridView.getLeft()) + ", " + (event.getY() - mGridView.getTop()) + ")");
//        Log.d(LOG_TAG, "view.Left(" + view.getLeft() + "), Right(" + view.getRight() + ")" + ", Top(" + view.getTop() + ")");
//        Log.d(LOG_TAG, "textureView.Left(" + mGridView.getLeft() + "), Right(" + mGridView.getRight() + ")" + ", Top(" + mGridView.getTop() + ")");
//        drawGrid(mGridView, 5, 3, 1.f, 1.f);
//
//        return false;
//    }
//


    @Override
    public void onStop() {
//        stopVideoCall();
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        if(mSubscription != null)
            mSubscription.unsubscribe();
        mAPIService = null;
        super.onDestroy();
    }


    private void intentVideoCall() {

        mSubscription = mAPIService
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

        mSubscription = mAPIService
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
//        mThread = new RenderingThread(mGridView);
//        mThread.start();
        //mGridView.drawGrid(mGridView, 5, 4, .0f, .0f);
        //mVidView.setLayoutParams(new RelativeLayout.LayoutParams(height, height));
        mVidView.setViewScale(height, height);
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
            case(VideoView.STATE_PLAYING):
            {
                mCircleProgress.setVisibility(View.GONE);
                mGridView = new FarmGridView(getContext(), mField.getCells(), mContainerView.getWidth() / mField.getWidth(), mContainerView.getHeight() / mField.getHeight() );
                mGridView.setSurfaceTextureListener(this);
                mGridView.setOpaque(false);
                mGridView.setCells(mField.getCells());
                mGridView.setLayoutParams(new FrameLayout.LayoutParams(mVidView.getWidth(), mVidView.getHeight(), FrameLayout.LayoutParams.MATCH_PARENT));
                this.getView().setOnTouchListener(mGridView);
                mGridView.setFieldW(mField.getWidth());
                mGridView.setFieldH(mField.getHeight());
                mContainerView.addView(mGridView);

                Log.d(LOG_TAG, "Container size: " + mContainerView.getWidth() + "x" + mContainerView.getHeight());
                Log.d(LOG_TAG, "Field size: " + mField.getWidth() + "x" + mField.getHeight());
                Log.d(LOG_TAG, "Video view size: " + mVidView.getWidth() + "x" + mVidView.getHeight());
            }
        }
    }

    public void setResizeStream(boolean resizeStream) {
        mVidView.setResizeStream(resizeStream);
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
