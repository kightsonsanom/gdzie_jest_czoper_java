package pl.tolichwer.gdziejestczoper.api;


import androidx.lifecycle.LiveData;

import pl.tolichwer.gdziejestczoper.viewobjects.Geo;
import pl.tolichwer.gdziejestczoper.viewobjects.Position;
import pl.tolichwer.gdziejestczoper.viewobjects.RemotePositionGeoJoin;
import pl.tolichwer.gdziejestczoper.viewobjects.User;
import com.google.gson.JsonElement;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface CzoperApi {

    @GET("position/positionForDayAndUser")
    LiveData<ApiResponse<List<Position>>> getLivePositionsForDayAndUser(@Query("userName") String userName, @Query("rangeFrom") long rangeFrom, @Query("rangeTo") long rangeTo);

    @GET("position")
    LiveData<ApiResponse<List<Position>>> getAllPositions();

    @Headers("Content-Type: application/json")
    @PUT("position/positionList/{userid}")
    Call<Void> sendPositionList(@Path("userid") int userid, @Body List<Position> positionList);

    @PUT("position/{userid}")
    Call<Void> sendPosition(@Path("userid") int userid, @Body Position position);


    @GET("geo/latestGeoForUsers")
    LiveData<ApiResponse<List<Geo>>> getLatestGeoForDistinctUsers();

    @PUT("geo/{userid}")
    Call<Void> sendGeo(@Path("userid") int userid, @Body Geo geo);

    @Headers("Content-Type: application/json")
    @PUT("geo/geoList/{userid}")
    Call<Void> sendGeoList(@Path("userid") int userid, @Body List<Geo> geoList);


    @Headers("Content-Type: application/json")
    @POST("assignGeoToPosition")
    Call<Void> assignGeoToPosition(@Body RemotePositionGeoJoin remotePositionGeoJoin);

    @Headers("Content-Type: application/json")
    @POST("assignGeoToPosition/list")
    Call<Void> assignGeoToPositionList(@Body List<RemotePositionGeoJoin> remotePositionGeoJoins);

    @GET("user")
    Call<List<User>> getUsers(@Query("login") String login, @Query("password") String password);

    @GET("position/positionForDayAndUser")
    Call<List<Position>> getPositionsForDayAndUser(@Query("userName") String userName, @Query("rangeFrom") long rangeFrom, @Query("rangeTo") long rangeTo);

    @GET
    Call<JsonElement> getReverseGeocoding(@Url String url);

    @Multipart
    @POST("file")
    Call<Void> uploadLogs(@Part MultipartBody.Part file);

}
