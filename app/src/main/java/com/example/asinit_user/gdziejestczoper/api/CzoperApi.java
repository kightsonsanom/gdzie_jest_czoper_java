package com.example.asinit_user.gdziejestczoper.api;


import android.arch.lifecycle.LiveData;

import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.viewobjects.PositionGeoJoin;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface CzoperApi {

    @GET("position/{day}")
    LiveData<ApiResponse<List<Position>>> getPositionsForDay(@Path("day") String day);

    @GET("position/{positionID}")
    LiveData<ApiResponse<Position>> getPositionByID(@Path("positionID") String positionID);

    @GET("position/all")
    LiveData<ApiResponse<List<Position>>> getAllPositions();

    @POST("position")
    Call<Position> sendPosition(@Body Position position);

    @POST("geo")
    Call<Geo> sendGeo(@Body Geo geo);

    @PUT("position")
    Call<Geo> updatePosition(@Body Position position);
//
//    @POST
//    Call<PositionGeoJoin> assignGeoToPosition(@Body PositionGeoJoin positionGeoJoin);
}