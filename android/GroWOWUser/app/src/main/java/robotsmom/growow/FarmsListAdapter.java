package robotsmom.growow;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rettpop on 16-06-06.
 */
public class FarmsListAdapter extends BaseExpandableListAdapter
{
    private static final String LOG_TAG = "FarmsListAdapter";
    ArrayList<Farm> mFarms;
    Context mContext;
    LayoutInflater mInflater;

    public FarmsListAdapter(Context context, ArrayList<Farm> mFarms) {
        this.mContext = context;
        this.mFarms = mFarms;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return mFarms.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mFarms.get(groupPosition).getFarmFields().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mFarms.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mFarms.get(groupPosition).getFarmFields().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if( null == convertView )
        {
            convertView = mInflater.inflate(R.layout.farms_list_parent_item, null);
        }

        Farm farm = (Farm) getGroup(groupPosition);
        TextView farmName = (TextView) convertView.findViewById(R.id.farmsListFarmName);
        TextView cellsAvailable = (TextView) convertView.findViewById(R.id.farmsListCellsAvailable);
        TextView farmAddress = (TextView) convertView.findViewById(R.id.farmsListFarmAddress);

        farmName.setText("Farm name: " + farm.getName());

        //TODO:Distinguish cells overall and not occupied
        // calculate cells overall
        int cellsOverall = 0;
        for (FarmField oneField:farm.getFarmFields()) {
            cellsOverall += oneField.getCells().size();
        }
        cellsAvailable.setText("Cells available: " + cellsOverall);

        farmAddress.setText("Address: " + farm.getAddress().toString());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if( null == convertView )
        {
            convertView = mInflater.inflate(R.layout.farms_list_child_item, null);
        }

        Farm farm1 = (Farm) getGroup(groupPosition);
        FarmField field = (FarmField) getChild(groupPosition, childPosition);
        TextView fieldID = (TextView) convertView.findViewById(R.id.farmsListChildFieldID);
        TextView cellsAvailable = (TextView) convertView.findViewById(R.id.farmsListChildCellsAvailable);
        TextView userCells = (TextView) convertView.findViewById(R.id.farmsListChildUsersCells);

        fieldID.setText("Field ID: " + field.getId());
        //TODO:Distinguish cells overall and not occupied
        cellsAvailable.setText("Cells available: " + field.getCells().size());
        userCells.setText("User' cells" + field.getCells().size());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
