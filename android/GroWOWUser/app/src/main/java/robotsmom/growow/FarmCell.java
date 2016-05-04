package robotsmom.growow;

import android.graphics.PointF;

/**
 * Created by rettpop on 16-05-04.
 */
public class FarmCell
{
    private float _left;
    private float _top;
    private float _width;
    private float _height;
    private boolean _isSelected = false;
    private String _description = null;

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

    public float getHeight() {
        return _height;
    }

    public void setHeight(float height) {
        this._height = height;
    }

    public float getWidth() {
        return _width;
    }

    public void setWidth(float width) {
        this._width = width;
    }

    public float getTop() {
        return _top;
    }

    public void setTop(float top) {
        this._top = top;
    }

    public float getLeft() {
        return _left;
    }

    public void setLeft(float left) {
        this._left = left;
    }

    public String getDescription() {
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
}
