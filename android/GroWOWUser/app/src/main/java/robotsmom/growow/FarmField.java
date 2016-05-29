package robotsmom.growow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by rettpop on 16-05-30.
 */
public class FarmField
{
    private String fieldID;
    private String name;
    private Owner owner;
    private Geo geo;
    private Address address;
    private ArrayList<FarmBed> farmBeds;

    public FarmField(JSONObject json)
    {
        try {
            // properties
            fieldID = json.getString("id");
            name = json.getString("name");
            owner = new Owner(json.getJSONObject("owner"));
            geo = new Geo(json.getJSONObject("geo"));
            address = new Address(json.getJSONObject("address"));

            // beds
            farmBeds = new ArrayList<FarmBed>();
            JSONArray bedsArr = json.getJSONArray("beds");
            for (int idx = 0; idx < bedsArr.length(); idx++) {
                farmBeds.add(new FarmBed(bedsArr.getJSONObject(idx)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

class Owner
{
    private String name;
    private String URL;

    public Owner(String name, String URL) {
        this.name = name;
        this.URL = URL;
    }

    public Owner(JSONObject json)
    {
        try {
            this.setName(json.getString("name"));
            this.setURL(json.getString("site"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

class Geo
{
    private double latitude;
    private double longitude;
    private String timeZone; //TODO: change to TimeZone later

    public Geo(JSONObject json) {
        try {
            latitude = json.getDouble("latitude");
            longitude = json.getDouble("longitude");
            timeZone = json.getString("timezone");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Geo(double latitude, double longitude, String timeZone) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeZone = timeZone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}

class Address
{
    private String country;
    private String city;

    public Address(String country, String city) {
        this.country = country;
        this.city = city;
    }

    public Address(JSONObject json) {
        try {
            this.country = json.getString("country");
            this.city = json.getString("city");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
