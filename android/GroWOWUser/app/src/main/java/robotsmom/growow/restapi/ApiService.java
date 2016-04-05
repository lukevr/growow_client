package robotsmom.growow.restapi;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import robotsmom.growow.restapi.model.OKJson;
import rx.Observable;

/**
 * Created by luke on 3/28/16.
 */
public class ApiService {

    private static final String SERVER_API_URL = "http://178.214.221.154:8064/api/";
    private ServerAPIInterface apiService;

    public ApiService() {

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER_API_URL)
                .build();

        apiService = retrofit.create(ServerAPIInterface.class);
    }

    public ServerAPIInterface getApi() {
        return apiService;
    }

    public interface ServerAPIInterface {
        @GET("intent_video")
        Observable<OKJson> intentVideo();

        @GET("stop_video")
        Observable<OKJson> stopVideo();

    }

}
