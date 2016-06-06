package robotsmom.growow;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class FarmsListFragment extends Fragment
{
    private static final String LOG_TAG = "FarmsListFragment";
    private OnFragmentInteractionListener mListener;
    private ArrayList<Farm> mFarms;
    ExpandableListView mListView;

    public FarmsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mFarms = ConfigHelper.getInstance().getFarms();
        Log.d(LOG_TAG, "onCreate finish");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(LOG_TAG, "onCreateView");

//        super.onCreateView(inflater, container, savedInstanceState);

//        LinearLayout farmsListLayout = (LinearLayout) inflater.inflate(R.layout.fragment_farms_list, container, false);
//        mListView = (ExpandableListView) farmsListLayout.findViewById(R.id.farmsList);
//        FarmsListAdapter listAdapter = new FarmsListAdapter(getContext(), mFarms);
//        mListView.setAdapter(listAdapter);

        LinearLayout farmsListLayout = (LinearLayout) inflater.inflate(R.layout.fragment_farms_list, container, false);
        mListView = (ExpandableListView) farmsListLayout.findViewById(R.id.farmsList);
        FarmsListAdapter listAdapter = new FarmsListAdapter(getContext(), mFarms);
        mListView.setAdapter(listAdapter);
        // Inflate the layout for this fragment
        Log.d(LOG_TAG, "onCreateView finish");
        return farmsListLayout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(LOG_TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        Log.d(LOG_TAG, "onAttach finish");
    }

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, "onDetach");
        super.onDetach();
        mListener = null;
        Log.d(LOG_TAG, "onDetach finish");
    }

    @Override
    public void onDestroyView()
    {
        Log.d(LOG_TAG, "onDestroyView");
        super.onDestroyView();
        if( mListView != null )
        {
            ViewGroup parentViewGroup = (ViewGroup) mListView.getParent();
            if( parentViewGroup != null )
            {
                parentViewGroup.removeAllViews();
            }
        }
        Log.d(LOG_TAG, "onDestroyView finish");
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
