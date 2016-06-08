package robotsmom.growow;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * The type Farms list fragment.
 */
public class FarmsListFragment extends Fragment
{
    private static final String LOG_TAG = "FarmsListFragment";
    public static final int ALL_GROUPS = -1;
    private OnFarmsListInteractionListener mListener;
    private ArrayList<Farm> mFarms;
    ExpandableListView mListView;
    LinearLayout mRootLayout;
    FarmsListAdapter mListAdapter;
    public FarmsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFarms = ConfigHelper.getInstance().getFarms();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        mRootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_farms_list, container, false);
        mListView = (ExpandableListView) mRootLayout.findViewById(R.id.farmsList);
        mListAdapter = new FarmsListAdapter(getContext(), mFarms);
        mListView.setAdapter(mListAdapter);
        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
        {
            @Override
            public void onGroupExpand(int groupPosition)
            {
                // collapse all groups but selected one
                collapseList(groupPosition);
            }
        });

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View listItemView, int groupPosition, long rowID)
            {
                if( !expandableListView.isGroupExpanded(groupPosition) ) {
                    farmSelected(expandableListView, listItemView, groupPosition, rowID);
                }
                else {
                    farmUnselected();
                }
                return false;
            }

        });

        // Farms list click on field listener
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                mListener.OnFarmsListInteraction(groupPosition, childPosition);
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

        // initially we do not have selected farm. Init views
        farmUnselected();

        return mRootLayout;
    }

    private void collapseList(int groupPosition)
    {
        for(int idx = 0; idx < mListView.getExpandableListAdapter().getGroupCount(); idx++)
        {
            if( (groupPosition == ALL_GROUPS) || (idx != groupPosition) ) {
                mListView.collapseGroup(idx);
            }
        }
    }

    private void farmUnselected()
    {
        TextView farmName = (TextView) mRootLayout.findViewById(R.id.farmsListDetailFarmName);
        TextView farmerName = (TextView) mRootLayout.findViewById(R.id.farmsListDetailFarmerName);
        TextView farmSite = (TextView) mRootLayout.findViewById(R.id.farmsListDetailFarmSite);
        TextView farmRaiting = (TextView) mRootLayout.findViewById(R.id.farmsListDetailFarmRaiting);
        ImageView farmLogo = (ImageView) mRootLayout.findViewById(R.id.farmsListDetailFarmLogo);
        ListView cellsList = (ListView) mRootLayout.findViewById(R.id.farmsListDetailUsersCellsList);

        farmName.setText("User name: User");
        farmerName.setText("Cells in use: 16");
        farmSite.setText("Neares harvest: 2016-06-07");
        cellsList.setVisibility(View.VISIBLE);
        farmRaiting.setText("");
    }

    // processing farm that was selected in list
    private void farmSelected(ExpandableListView expandableListView, View listItemView, int groupPosition, long rowID)
    {
        TextView farmName = (TextView) mRootLayout.findViewById(R.id.farmsListDetailFarmName);
        TextView farmerName = (TextView) mRootLayout.findViewById(R.id.farmsListDetailFarmerName);
        TextView farmSite = (TextView) mRootLayout.findViewById(R.id.farmsListDetailFarmSite);
        TextView farmRaiting = (TextView) mRootLayout.findViewById(R.id.farmsListDetailFarmRaiting);
        ImageView farmLogo = (ImageView) mRootLayout.findViewById(R.id.farmsListDetailFarmLogo);
        ListView cellsList = (ListView) mRootLayout.findViewById(R.id.farmsListDetailUsersCellsList);

        Farm farm = (Farm) expandableListView.getExpandableListAdapter().getGroup(groupPosition);

        farmName.setText("Farm name: " + farm.getName());
        farmerName.setText("Farmer name: " + farm.getOwner().getName());
        farmSite.setText("Farm site: " + farm.getSite());
        farmRaiting.setText("Raiting: " + String.format("%0" + farm.getRaiting() + "d", 0).replace("0", "★"));

        cellsList.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFarmsListInteractionListener) {
            mListener = (OnFarmsListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFarmsListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if( mListView != null )
        {
            ViewGroup parentViewGroup = (ViewGroup) mListView.getParent();
            if( parentViewGroup != null )
            {
                parentViewGroup.removeAllViews();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        collapseList(ALL_GROUPS);
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
    public interface OnFarmsListInteractionListener {
        // TODO: Update argument type and name
        void OnFarmsListInteraction(int groupPosition, int childPosition);
    }
}
