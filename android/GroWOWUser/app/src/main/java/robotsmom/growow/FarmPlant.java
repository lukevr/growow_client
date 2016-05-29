package robotsmom.growow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by rettpop on 16-05-29.
 */

enum PlantType
{
    PLANT_TYPE_VEGETABLE,
    PLANT_TYPE_BERRY,
    PLANT_TYPE_TREE,
    PLANT_TYPE_FRUIT
};

public class FarmPlant {
    private String type; // replace with enum later
    private String descriptionURL;
    private String plantName;
    private String variety;
    private Date plantingDate;
    private Date ripeningPlannedDate;
    private boolean readyForHarvest;

    public FarmPlant(JSONObject json)
    {
        try {
            type = json.getString("type");
            descriptionURL = json.getString("descriptionURL");
            plantName = json.getString("plantName");
            variety = json.getString("variety");
            plantingDate = new Date(json.getLong("plantingDate") * 1000); // convert from Unix time to millisecs
            ripeningPlannedDate = new Date(json.getLong("ripeningPlannedDate") * 1000);
            readyForHarvest = json.getBoolean("readyForHarvest");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescriptionURL() {
        return descriptionURL;
    }

    public void setDescriptionURL(String descriptionURL) {
        this.descriptionURL = descriptionURL;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public Date getPlantingDate() {
        return plantingDate;
    }

    public void setPlantingDate(Date plantingDate) {
        this.plantingDate = plantingDate;
    }

    public Date getRipeningPlannedDate() {
        return ripeningPlannedDate;
    }

    public void setRipeningPlannedDate(Date ripeningPlannedDate) {
        this.ripeningPlannedDate = ripeningPlannedDate;
    }

    public boolean isReadyForHarvest() {
        return readyForHarvest;
    }

    public void setReadyForHarvest(boolean readyForHarvest) {
        this.readyForHarvest = readyForHarvest;
    }
}
