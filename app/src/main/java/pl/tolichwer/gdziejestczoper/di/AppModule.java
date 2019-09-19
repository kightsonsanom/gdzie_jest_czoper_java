package pl.tolichwer.gdziejestczoper.di;


import android.app.Application;
import android.location.Geocoder;

import androidx.room.Room;

import pl.tolichwer.gdziejestczoper.api.CzoperApi;
import pl.tolichwer.gdziejestczoper.db.AppDatabase;
import pl.tolichwer.gdziejestczoper.db.SharedPreferencesRepository;
import pl.tolichwer.gdziejestczoper.db.dao.PositionDao;
import pl.tolichwer.gdziejestczoper.db.dao.GeoDao;
import pl.tolichwer.gdziejestczoper.db.dao.PositionGeoJoinDao;
import pl.tolichwer.gdziejestczoper.db.dao.UserDao;
import pl.tolichwer.gdziejestczoper.utils.GeoDeserializingAdapter;
import pl.tolichwer.gdziejestczoper.utils.GeoSerializingAdapter;
import pl.tolichwer.gdziejestczoper.utils.LiveDataCallAdapterFactory;
import pl.tolichwer.gdziejestczoper.utils.PositionAdapter;
import pl.tolichwer.gdziejestczoper.viewobjects.Geo;
import pl.tolichwer.gdziejestczoper.viewobjects.Position;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

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
    SharedPreferencesRepository provideSharedPreferences(Application application) {
        return new SharedPreferencesRepository(application.getApplicationContext());
    }

    @Provides
    @Singleton
    Gson provideGson() {

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
    Geocoder provideGeocoder(Application application) {
        return new Geocoder(application.getApplicationContext(), new Locale("pl_PL"));
    }

    @Provides
    @Singleton
    CzoperApi provideCzoperApi(Gson gson) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://94.23.53.86:8585/czoper/api/")
//                .baseUrl("http://192.168.1.132:8585/api/")
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
