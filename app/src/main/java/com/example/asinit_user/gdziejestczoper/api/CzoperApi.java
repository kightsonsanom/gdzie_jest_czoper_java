package com.example.asinit_user.gdziejestczoper.api;


import android.arch.lifecycle.LiveData;

import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.MapGeo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.viewobjects.PositionGeoJoin;
import com.example.asinit_user.gdziejestczoper.viewobjects.RemotePositionGeoJoin;
import com.example.asinit_user.gdziejestczoper.viewobjects.User;
import com.google.android.gms.common.api.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface CzoperApi {

    @GET("position/positionForDayAndUser")
    LiveData<ApiResponse<List<Position>>> getPositionsForDayAndUser(@Query("userName") String userName, @Query("rangeFrom") long rangeFrom, @Query("rangeTo") long rangeTo);

    @GET("position")
    LiveData<ApiResponse<List<Position>>> getAllPositions();

    @Headers("Content-Type: application/json")
    @PUT("position/positionList")
    Call<List<Position>> sendPositionList(@Body List<Position> positionList);

    @PUT("position")
    Call<Position> sendPosition(@Body Position position);


    @GET("geo/latestGeoForUsers")
    LiveData<ApiResponse<List<Geo>>> getLatestGeoForDistinctUsers();

    @POST("geo")
    Call<Geo> sendGeo(@Body Geo geo);

    @Headers("Content-Type: application/json")
    @PUT("geo/geoList")
    Call<List<Geo>> sendGeoList(@Body List<Geo> geoList);


    @Headers("Content-Type: application/json")
    @POST("assignGeoToPosition")
    Call<RemotePositionGeoJoin> assignGeoToPosition(@Body RemotePositionGeoJoin remotePositionGeoJoin);

    @Headers("Content-Type: application/json")
    @POST("assignGeoToPosition/list")
    Call<List<RemotePositionGeoJoin>> assignGeoToPositionList(@Body List<RemotePositionGeoJoin> remotePositionGeoJoins);

    @GET("user")
    Call<List<User>> getUsers(@Query("login") String login, @Query("password") String password);

}
