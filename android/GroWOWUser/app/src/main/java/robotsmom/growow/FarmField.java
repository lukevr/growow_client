package robotsmom.growow;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by rettpop on 16-05-23.
 */
public class FarmField
{
    private String id;
    private float width;
    private float height;
    private String units;
    private String streamURL;
    private String timelapsURL;

    private ArrayList<FarmCell> cells;
    private FarmFieldDistorsion distorsion;

    public FarmField(JSONObject json)
    {
        try {
            // properties
            id = json.getString("id");
            streamURL = json.getString("liveURL");
            timelapsURL = json.getString("timelapseURL");

            // size
            JSONObject tmpObj = json.getJSONObject("size");
            width = BigDecimal.valueOf(tmpObj.getDouble("width")).floatValue();
            height = BigDecimal.valueOf(tmpObj.getDouble("height")).floatValue();
            units = tmpObj.getString("units");

            // distortion
            tmpObj = json.getJSONObject("distorsion");
            distorsion = new FarmFieldDistorsion(tmpObj);

            // cells
            cells = new ArrayList<FarmCell>();
            JSONArray cellsArr = json.getJSONArray("cells");
            for (int idx = 0; idx < cellsArr.length(); idx++) {
                cells.add(new FarmCell(cellsArr.getJSONObject(idx)));
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public FarmField(float width, float height, String streamURL)
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

    public FarmFieldDistorsion getDistorsion() {
        return distorsion;
    }

    public void setDistorsion(FarmFieldDistorsion distorsion) {
        this.distorsion = distorsion;
    }

    public ArrayList<FarmCell> getCells() {
        return cells;
    }

    public String getTimelapsURL() {
        return timelapsURL;
    }

    public String getId() {
        return id;
    }

}
