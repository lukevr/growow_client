package robotsmom.growow;

import android.graphics.PointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rettpop on 16-05-04.
 */
public class FarmCell
{
    private int _id;
    private float _left;
    private float _top;
    private float _width;
    private float _height;
    private boolean _isSelected = false;
    private boolean _isActive = true;
    private String _description = null;
    private String _type = null;
    private ArrayList<FarmPlant> _plants;

    public FarmCell(JSONObject json)
    {
        try
        {
            // properties
            _id = json.getInt("id");
            _left = BigDecimal.valueOf(json.getDouble("x")).floatValue();
            _top = BigDecimal.valueOf(json.getDouble("y")).floatValue();
            _width = BigDecimal.valueOf(json.getDouble("width")).floatValue();
            _height = BigDecimal.valueOf(json.getDouble("height")).floatValue();
            _isActive = json.getBoolean("isActive");
            _type = json.getString("type");

            // filling plants array
            _plants = new ArrayList<FarmPlant>();
            JSONArray plantsArr = json.getJSONArray("plants");
            for (int idx = 0; idx < plantsArr.length(); idx++) {
                _plants.add(new FarmPlant(plantsArr.getJSONObject(idx)));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public FarmCell(float left, float top, float width, float height) {
        this._left = left;
        this._top = top;
        this._width = width;
        this._height = height;
    }

    public boolean containsPoint(PointF point)
    {
        return     (point.x > _left && point.x < _left + _width)
                && (point.y > _top && point.y < _top + _height);
    }

    // Accessors
    public boolean isSelected() {
        return _isSelected;
    }

    public void setSelected(boolean selected) {
        _isSelected = selected;
    }

    public float height() {
        return _height;
    }

    public void setHeight(float height) {
        this._height = height;
    }

    public float width() {
        return _width;
    }

    public void setWidth(float width) {
        this._width = width;
    }

    public float top() {
        return _top;
    }

    public void setTop(float top) {
        this._top = top;
    }

    public float left() {
        return _left;
    }

    public void setLeft(float left) {
        this._left = left;
    }

    public String description() {
        return _description;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public float right()
    {
        return _left + _width;
    }

    public float bottom()
    {
        return _top + _height;
    }

    public PointF[] points()
    {
        // At the moment we have only 4 points per cell.
        // But later we can make 5, 6, or even ✡ like cells.
        // For farm nerds.
        PointF lt = new PointF(_left, _top);
        PointF rt = new PointF(right(), _top);
        PointF rb = new PointF(right(), bottom());
        PointF lb = new PointF(_left, bottom());

        return (new PointF[]{lt, rt, rb, lb});
    }
}
