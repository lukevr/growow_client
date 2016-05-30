package robotsmom.growow;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by rettpop on 16-05-30.
 */
public class JSONLoader
{
    /**
     * Gets resource configuration.
     *
     * @param id      resource id
     * @param context the application context
     * @return JSONObject
     * @throws IOException   the io exception
     * @throws JSONException the json exception
     */
    public static JSONObject getResourceConfiguration(int id, Context context) throws IOException, JSONException
    {
        InputStream is = context.getResources().openRawResource(id);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        String jsonString = writer.toString();
        JSONObject object = new JSONObject(jsonString);
        return object;
    }

    /**
     * Loads JSON string from file
     *
     * @param file    file object representing .json file
     * @return JSON object read from file
     * @throws IOException
     * @throws JSONException
     */

    public static JSONObject getExternalJson(File file) throws IOException, JSONException
    {
        InputStream is = new FileInputStream(file);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        String jsonString = writer.toString();
        JSONObject object = new JSONObject(jsonString);
        return object;
    }
}
