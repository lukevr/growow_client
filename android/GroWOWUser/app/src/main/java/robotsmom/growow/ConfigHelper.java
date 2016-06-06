package robotsmom.growow;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by rettpop on 16-06-06.
 */
public class ConfigHelper
{
    private static final String LOG_TAG = "ConfigHelper";
    private static ConfigHelper ourInstance = new ConfigHelper();
    private ArrayList<Farm> mFarms;

    public static ConfigHelper getInstance() {
        return ourInstance;
    }

    private ConfigHelper()
    {
        Log.d(LOG_TAG, "Instantiate");
    }

    public ArrayList<Farm> getFarms()
    {
        if( null == mFarms)
        {
            mFarms = new ArrayList<Farm>(1);

            Log.d(LOG_TAG, "Reading farm config from resources JSON");
            try
            {
                JSONObject json = JSONLoader.getResourceConfiguration(R.raw.testfield, App.getInstance().getApplicationContext());
                JSONArray farms = json.getJSONArray("farms");
                for (int idx = 0; idx < farms.length(); idx++)
                {
                    JSONObject oneFarm = farms.getJSONObject(idx);
                    mFarms.add(new Farm(oneFarm));
                }
            } catch (IOException e)
            {
                Log.e(LOG_TAG, "Error reading config JSON");
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(LOG_TAG, "Returning " + mFarms.size() + " farms");
        return mFarms;
    }

}
