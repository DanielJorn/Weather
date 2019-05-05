package example.yuratoxa.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JSONPlaceHolderApi {

    @GET("/data/2.5/forecast")
    Call<WeatherDay> getDataFiveDays(@Query("units") String units, @Query("APPID") String APPID,
                                     @Query("lang") String lang, @Query("lat") double lat,
                                     @Query("lon") double lon);
}

