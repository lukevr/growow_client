package robotsmom.growow.restapi.model;

/**
 * Created by luke on 3/25/16.
 */

import java.util.HashMap;
import java.util.Map;

public class OKJson {

    private String result;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The result
     */
    public String getResult() {
        return result;
    }

    /**
     *
     * @param result
     * The result
     */
    public void setResult(String result) {
        this.result = result;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
