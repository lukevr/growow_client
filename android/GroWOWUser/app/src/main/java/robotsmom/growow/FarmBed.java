package robotsmom.growow;


import java.util.ArrayList;

/**
 * Created by rettpop on 16-05-23.
 */
public class FarmBed
{
    private String bedID;
    private float width;
    private float height;
    private String units;
    private String streamURL;
    private ArrayList<FarmCell> cells;

    public FarmBed(float width, float height, String streamURL)
    {
        this.width = width;
        this.height = height;
        this.streamURL = streamURL;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }

}
