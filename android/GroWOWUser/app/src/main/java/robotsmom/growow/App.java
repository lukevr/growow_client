package robotsmom.growow;

import android.app.Application;

/**
 * Created by rettpop on 16-06-06.
 */
public class App extends Application
{
    private static App mInstance;
    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
    }
}
