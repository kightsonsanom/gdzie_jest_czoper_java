package com.example.asinit_user.gdziejestczoper.di;


import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.asinit_user.gdziejestczoper.api.CzoperApi;
import com.example.asinit_user.gdziejestczoper.db.AppDatabase;
import com.example.asinit_user.gdziejestczoper.db.SharedPreferencesRepository;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionDao;
import com.example.asinit_user.gdziejestczoper.db.dao.GeoDao;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionGeoJoinDao;
import com.example.asinit_user.gdziejestczoper.db.dao.UserDao;
import com.example.asinit_user.gdziejestczoper.utils.GeoDeserializingAdapter;
import com.example.asinit_user.gdziejestczoper.utils.GeoSerializingAdapter;
import com.example.asinit_user.gdziejestczoper.utils.LiveDataCallAdapterFactory;
//import com.example.asinit_user.gdziejestczoper.utils.PositionAdapter;
import com.example.asinit_user.gdziejestczoper.utils.PositionAdapter;
import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Module(includes = ViewModelModule.class)
public class AppModule {


    @Provides
    @Singleton
    SharedPreferencesRepository provideSharedPreferences(Application application){
        return new SharedPreferencesRepository(application.getApplicationContext());
    }

    @Provides
    @Singleton
    Gson provideGson(){

        return new GsonBuilder()
                .registerTypeAdapter(Geo.class, new GeoSerializingAdapter())
                .registerTypeAdapter(Geo.class, new GeoDeserializingAdapter())
                .registerTypeAdapter(Position.class, new PositionAdapter())
                .serializeNulls()
                .setLenient()
                .create();
    }

    @Provides
    @Singleton
    CzoperApi provideCzoperApi(Gson gson) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://94.23.53.86:8585/czoper/api/")
                .baseUrl("http://192.168.1.54:8585/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();
        return retrofit.create(CzoperApi.class);
    }


    @Provides
    @Singleton
    PositionDao providePositionDao(AppDatabase database) {
        return database.positionDao();
    }

    @Provides
    @Singleton
    GeoDao provideGeoDao(AppDatabase database) {
        return database.geoDao();
    }

    @Provides
    @Singleton
    PositionGeoJoinDao providePositionGeoJoinDao(AppDatabase database) {
        return database.positionGeoJoinDao();
    }

    @Provides
    @Singleton
    UserDao provideUserDao(AppDatabase database) {
        return database.userDao();
    }

//    @Provides
//    @Singleton
//    Repository provideRepository(GeoDao geoDao, PositionDao positionDao, PositionGeoJoinDao positionGeoJoinDao, AppExecutors appExecutors){
//        return new Repository(positionDao, geoDao, positionGeoJoinDao, appExecutors);
//    }

    @Provides
    @Singleton
    AppDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(
                application,
                AppDatabase.class,
                "actions.db"
        ).build();
    }
}
