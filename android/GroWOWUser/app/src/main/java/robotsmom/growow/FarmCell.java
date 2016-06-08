package robotsmom.growow;

import android.graphics.PointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by rettpop on 16-05-04.
 */
public class FarmCell
{
    private int mId;
    private float mLeft;
    private float mTop;
    private float mWidth;
    private float mHeight;
    private boolean mIsSelected = false;
    private boolean mIsActive = true;
    private String mDescription = null;
    private String mType = null;

    private ArrayList<FarmPlant> mPlants;

    public FarmCell(JSONObject json)
    {
        try
        {
            // properties
            mId = json.getInt("id");
            mLeft = BigDecimal.valueOf(json.getDouble("x")).floatValue();
            mTop = BigDecimal.valueOf(json.getDouble("y")).floatValue();
            mWidth = BigDecimal.valueOf(json.getDouble("width")).floatValue();
            mHeight = BigDecimal.valueOf(json.getDouble("height")).floatValue();
            mIsActive = json.getBoolean("isActive");
            mType = json.getString("type");

            // filling plants array
            mPlants = new ArrayList<FarmPlant>();
            JSONArray plantsArr = json.getJSONArray("plants");
            for (int idx = 0; idx < plantsArr.length(); idx++) {
                mPlants.add(new FarmPlant(plantsArr.getJSONObject(idx)));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public FarmCell(float left, float top, float width, float height) {
        this.mLeft = left;
        this.mTop = top;
        this.mWidth = width;
        this.mHeight = height;
    }

    public boolean containsPoint(PointF point)
    {
        return     (point.x > mLeft && point.x < mLeft + mWidth)
                && (point.y > mTop && point.y < mTop + mHeight);
    }

    // Accessors
    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public float height() {
        return mHeight;
    }

    public void setHeight(float height) {
        this.mHeight = height;
    }

    public float width() {
        return mWidth;
    }

    public void setWidth(float width) {
        this.mWidth = width;
    }

    public float top() {
        return mTop;
    }

    public void setTop(float top) {
        this.mTop = top;
    }

    public float left() {
        return mLeft;
    }

    public void setLeft(float left) {
        this.mLeft = left;
    }

    public String description() {
        StringBuilder str = new StringBuilder();
        for (int idx = 0; idx < mPlants.size(); idx++) {
            str.append(mPlants.get(idx).getPlantName() + "; ");
        }
        return str.toString();
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public float right()
    {
        return mLeft + mWidth;
    }

    public float bottom()
    {
        return mTop + mHeight;
    }

    public PointF[] points()
    {
        // At the moment we have only 4 points per cell.
        // But later we can make 5, 6, or even ✡ like cells.
        // For farm nerds.
        PointF lt = new PointF(mLeft, mTop);
        PointF rt = new PointF(right(), mTop);
        PointF rb = new PointF(right(), bottom());
        PointF lb = new PointF(mLeft, bottom());

        return (new PointF[]{lt, rt, rb, lb});
    }

    public ArrayList<FarmPlant> getPlants() {
        return mPlants;
    }

    public int getId() {
        return mId;
    }

}
