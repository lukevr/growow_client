package robotsmom.growow;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by rettpop on 16-06-06.
 */
public class CellsListAdapter extends BaseExpandableListAdapter
{
    private static final String LOG_TAG = "CellsListAdapter";
    ArrayList<FarmCell> mCells;
    Context mContext;
    LayoutInflater mInflater;

    public CellsListAdapter(Context context, ArrayList<FarmCell> mCells) {
        this.mContext = context;
        this.mCells = mCells;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        Log.d(LOG_TAG, "getGroupCount() = " +  mCells.size());
        return mCells.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Log.d(LOG_TAG, "getChildrenCount() = " + mCells.get(groupPosition).getPlants().size());
        return mCells.get(groupPosition).getPlants().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCells.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCells.get(groupPosition).getPlants().get(childPosition);
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
        Log.d(LOG_TAG, "getGroupView()");
        if( null == convertView )
        {
            convertView = mInflater.inflate(R.layout.cells_list_parent_item, null);
        }

        FarmCell farmCell = (FarmCell) getGroup(groupPosition);
        TextView cellID = (TextView) convertView.findViewById(R.id.cellsListParentCellID);
        TextView plantName = (TextView) convertView.findViewById(R.id.cellsListParentPlantName);

        cellID.setText("Cell #" + farmCell.getId());
        plantName.setText("Plant: " + farmCell.getPlants().get(0).getPlantName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if( null == convertView )
        {
            convertView = mInflater.inflate(R.layout.cells_list_child_item, null);
        }

        FarmCell farmCell = (FarmCell) getGroup(groupPosition);
        FarmPlant plant = farmCell.getPlants().get(0);
        TextView plantName = (TextView) convertView.findViewById(R.id.cellsListChildPlantName);
        TextView plantVariety = (TextView) convertView.findViewById(R.id.cellsListChildPlantVariety);
        TextView plantDate = (TextView) convertView.findViewById(R.id.cellsListChildPlantDate);
        TextView harvestDate = (TextView) convertView.findViewById(R.id.cellsListChildHarvestDate);

        plantName.setText("Plant: " + plant.getPlantName());
        plantVariety.setText("Variety: " + plant.getVariety());

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        plantDate.setText("Plant date: " + format.format(plant.getPlantingDate()));
        harvestDate.setText("Harvest date: " + format.format(plant.getHarvestDate()));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
