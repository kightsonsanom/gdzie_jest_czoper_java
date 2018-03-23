package com.example.asinit_user.gdziejestczoper.api;


import android.arch.lifecycle.LiveData;

import com.example.asinit_user.gdziejestczoper.viewobjects.Position;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CzoperApi {

    @GET("position/{day}")
    LiveData<ApiResponse<List<Position>>> getPositionsForDay(@Path("day") String day);

    @GET("position/{positionID}")
    LiveData<ApiResponse<Position>> getPositionByID(@Path("positionID") String positionID);

    @GET("position/all")
    LiveData<ApiResponse<List<Position>>> getAllPositions();
}
