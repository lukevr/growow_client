package robotsmom.growow;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by rettpop on 16-05-23.
 * Describing 4 point of shifted bed corners
 */
public class FarmBedDistorsion
{

    /**
     * Coordinates of LeftTop, RightTop, LeftBottom, RightBottom points
     */
    float ltX, ltY;
    float rtX, rtY;
    float lbX, lbY;
    float rbX, rbY;

    public FarmBedDistorsion(JSONObject json)
    {
        try {
            ltX = BigDecimal.valueOf(json.getDouble("leftTopX")).floatValue();
            ltY = BigDecimal.valueOf(json.getDouble("leftTopY")).floatValue();
            rtX = BigDecimal.valueOf(json.getDouble("rightTopX")).floatValue();
            rtY = BigDecimal.valueOf(json.getDouble("rightTopY")).floatValue();
            lbX = BigDecimal.valueOf(json.getDouble("leftBottomX")).floatValue();
            lbY = BigDecimal.valueOf(json.getDouble("leftBottomY")).floatValue();
            rbX = BigDecimal.valueOf(json.getDouble("rightBottomX")).floatValue();
            rbY = BigDecimal.valueOf(json.getDouble("rightBottomY")).floatValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
