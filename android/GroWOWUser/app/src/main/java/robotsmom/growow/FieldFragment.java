package robotsmom.growow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class FieldFragment extends Fragment implements TextureView.SurfaceTextureListener, VideoStateListener
{
    private static final String LOG_TAG = "FarmFragment";
//    private RelativeLayout mRoot;
    private RelativeLayout mContainerView;
    private VideoView mVidView;
    private FarmGridView mGridView;
    private ProgressBar mCircleProgress;
    FarmField mField;
    Farm mFarm;
    private OnFieldViewInteractionListener mListener;
    ExpandableListView mListView;
    RelativeLayout mRootLayout;
    CellsListAdapter mListAdapter;
    private ServerAPIInterface mAPIService;
    private Subscription mSubscription;

    private int mCurrentFarmID;
    private int mCurrentFieldID;

    public static final String SELECTED_FARM_ID = "SELECTED_FARM_ID";
    public static final String SELECTED_FIELD_ID = "SELECTED_FIELD_ID";
    private String mCurrentStream;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAPIService = new ApiService().getApi();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootLayout = (RelativeLayout) inflater.inflate(R.layout.field_view, container, false);

        MediaController mc = new MediaController(getContext());
        mc.setEnabled(false);

        mCurrentFarmID = this.getArguments().getInt(SELECTED_FARM_ID);
        mCurrentFieldID = this.getArguments().getInt(SELECTED_FIELD_ID);
        mFarm = ConfigHelper.getInstance().getFarms().get(mCurrentFarmID); // taking just first farm at the moment
        mField = mFarm.getFarmFields().get(mCurrentFieldID);

        setupCellsList(inflater, container, savedInstanceState);

        mVidView = new VideoView(getContext(), this);
        mVidView.setVideoPath(mField.getStreamURL());
        mCurrentStream = mField.getStreamURL();
        mVidView.setDistorsion(mField.getDistorsion());
        mVidView.setMediaController(mc);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mVidView.setLayoutParams(params);

        mContainerView = (RelativeLayout) mRootLayout.findViewById(R.id.grid_container);


        final FieldFragment textureListener = this;
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


        mCircleProgress = (ProgressBar) mRootLayout.findViewById(R.id.circleProgress);
        mCircleProgress.bringToFront();

//        intentVideoCall();

        // set farm and field names
        TextView farmName = (TextView) mRootLayout.findViewById(R.id.fieldViewFarmName);
        TextView fieldID = (TextView) mRootLayout.findViewById(R.id.fieldViewFieldID);

        farmName.setText("Farm name: " + mFarm.getName());
        fieldID.setText("Farm field #" + mField.getId());

        return mRootLayout;
    }

    private void setupCellsList(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mListView = (ExpandableListView) mRootLayout.findViewById(R.id.cellsList);
        mListAdapter = new CellsListAdapter(getContext(), mField.getCells());
        mListView.setAdapter(mListAdapter);
        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
        {
            @Override
            public void onGroupExpand(int groupPosition)
            {
                // collapse all groups but selected one
//                collapseList(groupPosition);
            }
        });

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View listItemView, int groupPosition, long rowID)
            {
                if( mGridView != null)
                {
                    FarmCell cell = mField.getCells().get(groupPosition);
                    mGridView.selectCell(cell);
                }
                return false;
            }

        });

        // Farms list click on field listener
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                mListener.OnFieldViewInteraction();
                return true;
            }
        });

        // Processing layout issues when will have concrete screen and controls size
        ViewTreeObserver vto = mListView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                ViewTreeObserver obs = mListView.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);

                // Move expanding indicator to right side of cell
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
                    mListView.setIndicatorBounds(
                            (int) (mListView.getWidth() - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics())),
                            mListView.getWidth()
                    );
                }
                else{
                    mListView.setIndicatorBoundsRelative(
                            (int) (mListView.getWidth() - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics())),
                            mListView.getWidth()
                    );
                }
            }
        });

        Button plantButton = (Button) mRootLayout.findViewById(R.id.buttonStartPlant);
        plantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Plant the cell")
                        .setMessage("Are you sure you want to plant out this cell?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        // initially we do not have selected farm. Init views
//        farmUnselected();

    }

    public void swapStreams()
    {
        if( mCurrentStream.equals(mField.getStreamURL()) )
        {
            mCurrentStream = mField.getTimelapsURL();
            Snackbar.make(mRootLayout, "Now trying timelaps", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        else {
            mCurrentStream = mField.getStreamURL();
            Snackbar.make(mRootLayout, "Now trying live", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        mVidView.stopPlayback();
        mContainerView.removeView(mVidView);

        MediaController mc = new MediaController(getContext());
        mc.setEnabled(false);
        mVidView = new VideoView(getContext(), this);
        mVidView.setVideoPath(mCurrentStream);
        mVidView.setDistorsion(mField.getDistorsion());
        mVidView.setMediaController(mc);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mVidView.setLayoutParams(params);

        mContainerView = (RelativeLayout) mRootLayout.findViewById(R.id.grid_container);
        mContainerView.addView(mVidView);
        mVidView.start();
        mCircleProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context)
    {
        Log.d(LOG_TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof OnFieldViewInteractionListener) {
            mListener = (OnFieldViewInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFieldViewInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }


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
        mVidView.setViewScale(height, height);
        mGridView.invalidate();
        mGridView.drawCells(mField.getCells());
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(LOG_TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(LOG_TAG, "onSurfaceTextureDestroyed");
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
                mGridView.setFieldSize(mField.getWidth(), mField.getHeight());
                mGridView.setLayoutParams(new FrameLayout.LayoutParams(mVidView.getWidth(), mVidView.getHeight(), FrameLayout.LayoutParams.MATCH_PARENT));
                this.getView().setOnTouchListener(mGridView);
                mContainerView.addView(mGridView);

                Log.d(LOG_TAG, "Container size: " + mContainerView.getWidth() + "x" + mContainerView.getHeight());
                Log.d(LOG_TAG, "Field size: " + mField.getWidth() + "x" + mField.getHeight());
                Log.d(LOG_TAG, "Video view size: " + mVidView.getWidth() + "x" + mVidView.getHeight());
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFieldViewInteractionListener {
        // TODO: Update argument type and name
        void OnFieldViewInteraction();
    }

}
