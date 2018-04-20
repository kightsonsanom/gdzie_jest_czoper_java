package com.example.asinit_user.gdziejestczoper.db;


import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.location.Location;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.asinit_user.gdziejestczoper.AppExecutors;
import com.example.asinit_user.gdziejestczoper.api.ApiResponse;
import com.example.asinit_user.gdziejestczoper.api.CzoperApi;
import com.example.asinit_user.gdziejestczoper.api.NetworkBoundResource;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionDao;
import com.example.asinit_user.gdziejestczoper.db.dao.GeoDao;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionGeoJoinDao;
import com.example.asinit_user.gdziejestczoper.db.dao.UserDao;
import com.example.asinit_user.gdziejestczoper.ui.login.LoginManagerCallback;
import com.example.asinit_user.gdziejestczoper.viewobjects.MapGeo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.PositionGeoJoin;
import com.example.asinit_user.gdziejestczoper.services.PositionManagerCallback;
import com.example.asinit_user.gdziejestczoper.ui.search.SearchFragmentViewModelCallback;
import com.example.asinit_user.gdziejestczoper.viewobjects.RemotePositionGeoJoin;
import com.example.asinit_user.gdziejestczoper.viewobjects.Resource;
import com.example.asinit_user.gdziejestczoper.viewobjects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class Repository {

    private PositionManagerCallback positionManagerCallback;
    private SearchFragmentViewModelCallback searchFragmentViewModelCallback;
    private LoginManagerCallback loginManagerCallback;

    private MediatorLiveData<List<Position>> observablePositions;
    private MediatorLiveData<List<Geo>> observableGeos;
    private MediatorLiveData<Geo> observableGeo;

    private UserDao userDao;
    private PositionDao positionDao;
    private GeoDao geoDao;
    private PositionGeoJoinDao positionGeoJoinDao;
    private CzoperApi czoperApi;
    private AppExecutors appExecutors;
    private SharedPreferencesRepo sharedPreferencesRepo;

    private Geo latestGeoFromDb;
    private Position latestPositionFromDb;
    private List<Geo> allGeos;
    private List<Position> allPositions;
    private List<User> allUsers;
    private LiveData<Geo> latestGeoForUser;

    private final Object lock = new Object();

    private Thread getLatestMapGeo;
    private LiveData<List<MapGeo>> mapGeos;

    @Inject
    public Repository(PositionDao positionDao, GeoDao geoDao, PositionGeoJoinDao positionGeoJoinDao, UserDao userDao, AppExecutors appExecutors, CzoperApi czoperApi, SharedPreferencesRepo sharedPreferencesRepo) {
        this.appExecutors = appExecutors;
        this.positionDao = positionDao;
        this.positionGeoJoinDao = positionGeoJoinDao;
        this.geoDao = geoDao;
        this.userDao = userDao;
        this.czoperApi = czoperApi;
        this.sharedPreferencesRepo = sharedPreferencesRepo;

        observablePositions = new MediatorLiveData<>();
        observablePositions.addSource(positionDao.loadPositions(),
                positions -> observablePositions.postValue(positions));

        observableGeos = new MediatorLiveData<>();
        observableGeos.addSource(geoDao.loadGeos(),
                geos -> observableGeos.postValue(geos));


        observableGeo = new MediatorLiveData<>();
        observableGeo.addSource(geoDao.loadLatestLiveDataGeo(),
                geo -> observableGeo.postValue(geo));

    }

    public void setPositionManagerCallback(PositionManagerCallback positionManagerCallback) {
        this.positionManagerCallback = positionManagerCallback;
    }

    public void setSearchFragmentViewModelCallback(SearchFragmentViewModelCallback searchFragmentViewModelCallback) {
        this.searchFragmentViewModelCallback = searchFragmentViewModelCallback;
    }

    public void setLoginManagerCallback(LoginManagerCallback loginManagerCallback) {
        this.loginManagerCallback = loginManagerCallback;
    }

    public LiveData<Resource<List<Position>>> getPositions() {
        return new NetworkBoundResource<List<Position>, List<Position>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<Position> item) {
                positionDao.insertAll(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Position> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Position>> loadFromDb() {
                return positionDao.loadPositions();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Position>>> createCall() {
                return czoperApi.getAllPositions();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<MapGeo>>> getMapGeos() {
        return new NetworkBoundResource<List<MapGeo>, List<MapGeo>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<MapGeo> item) {
                for (MapGeo mapGeo : item) {
                    geoDao.insertGeo(mapGeo.getGeo());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<MapGeo> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<MapGeo>> loadFromDb() {
                getLatestMapGeo = new Thread(() ->{
                    Timber.d("started getLatestMapGeo thread");
                    List<User> userList = userDao.getAllUsers();
                    List<MapGeo> mapGeoList = new ArrayList<>();

                    for (User u : userList) {
                        synchronized(lock) {
                            Timber.d("get geo for user = " + u);
                            mapGeos = Transformations.map(geoDao.loadLatestGeoForUser(u.getUser_id()), geo -> {
                                MapGeo mapGeo = new MapGeo(u, geo);
                                mapGeoList.add(mapGeo);
                                Timber.d("mapGeo = " + mapGeo.toString());
                                return mapGeoList;
                            });
                        }
                    }

                    Timber.d("mapGeo: " + mapGeoList.size());
                    Timber.d("mapGeos: " + mapGeos.getValue());
                    for (MapGeo mapGeo : mapGeoList) {
                        Timber.d("mapGeo: " + mapGeo.toString());
                    }
                });
                getLatestMapGeo.start();

                try {
                    getLatestMapGeo.join();
                    Timber.d("finished getLatestMapGeoThread");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return mapGeos;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<MapGeo>>> createCall() {
                return czoperApi.getMapGeos();
            }
        }.asLiveData();
    }

    public void postPosition(Position position) {
        appExecutors.diskIO().execute(() -> {
            Timber.d("inserting position into DB ID: " + position.getPosition_id() + " czas: " + position.getLastLocationDate());
            positionDao.insertPosition(position);
        });

        long positionIDFromPreferences = sharedPreferencesRepo.getPositionID();

        if (positionIDFromPreferences == 0) {
            sendPositionToServer(position);
        } else {
            getPositionsToSend(positionIDFromPreferences);
        }
    }

    private void sendPositionToServer(Position position) {
        Call<Position> call = czoperApi.sendPosition(position);

        call.enqueue(new Callback<Position>() {
            @Override
            public void onResponse(Call<Position> call, Response<Position> response) {
                Timber.d("send single position onResponse");
                sharedPreferencesRepo.setIsPositionSend(true);
                sharedPreferencesRepo.putPositionID(0);

                Timber.d(" wasGeoSend in sendPositionToServer = " + sharedPreferencesRepo.getIsGeoSend());
                if (sharedPreferencesRepo.getIsGeoSend()) {
                    assignGeoToPositionOnServer();
                }
            }

            @Override
            public void onFailure(Call<Position> call, Throwable t) {
                Timber.d("postPosition send position onFailure call:  " + call + "throw: " + t);
                sharedPreferencesRepo.putPositionID(position.getPosition_id());
                sharedPreferencesRepo.setIsGeoSend(false);
                saveLastAssignedTimeToPreferences();
            }
        });
    }

    private void getPositionsToSend(long positionIDFromPreferences) {
        appExecutors.diskIO().execute(() -> {
            Timber.d("positionIDFromPreferences: " + positionIDFromPreferences);
            List<Position> positionList = positionDao.getPositionsSinceFailure(positionIDFromPreferences);
            Timber.d("positionlist to send");
            for (Position p : positionList) {
                Timber.d("position to send: " + p);
            }
            sendPositionsListToServer(positionList);
        });
    }

    private void sendPositionsListToServer(List<Position> positionlist) {

        Call<List<Position>> call = czoperApi.sendPositionList(positionlist);

        call.enqueue(new Callback<List<Position>>() {
            @Override
            public void onResponse(Call<List<Position>> call, Response<List<Position>> response) {
                Timber.d("send position list onResponse");
                sharedPreferencesRepo.setIsPositionSend(true);
                sharedPreferencesRepo.putPositionID(0);

                if (sharedPreferencesRepo.getIsGeoSend()) {
                    assignGeoToPositionOnServer();
                }
            }

            @Override
            public void onFailure(Call<List<Position>> call, Throwable t) {
                Timber.d("send position list onFailure call:  " + call + "throw: " + t);
                sharedPreferencesRepo.setIsGeoSend(false);
            }
        });
    }

    public void postGeo(Geo geo) {
        appExecutors.diskIO().execute(() -> {
            Timber.d("inserting geo into DB ID: " + geo.getGeo_id() + " czas: " + geo.getDate());
            geoDao.insertGeo(geo);
        });

        long geoIDFromPreferences = sharedPreferencesRepo.getGeoID();

        if (geoIDFromPreferences == 0) {
            sendGeoToServer(geo);
        } else {
            getGeosToSend(geoIDFromPreferences);
        }
    }

    private void sendGeoToServer(Geo geo) {
        Call<Geo> call = czoperApi.sendGeo(geo);
        call.enqueue(new Callback<Geo>() {
            @Override
            public void onResponse(Call<Geo> call, Response<Geo> response) {
                Timber.d("send single geo onResponse");
                sharedPreferencesRepo.putGeoID(0);
                sharedPreferencesRepo.setIsGeoSend(true);

                Timber.d(" wasPositionSend in sendPositionToServer = " + sharedPreferencesRepo.getIsPositionSend());
                if (sharedPreferencesRepo.getIsPositionSend()) {
                    assignGeoToPositionOnServer();
                }
            }


            @Override
            public void onFailure(Call<Geo> call, Throwable t) {
                sharedPreferencesRepo.putGeoID(geo.getGeo_id());
                Timber.d("send single geo onFailure call:  " + call + "throw: " + t);
                saveLastAssignedTimeToPreferences();
                sharedPreferencesRepo.setIsPositionSend(false);
            }
        });
    }

    private void getGeosToSend(long geoIDFromPreferences) {
        appExecutors.diskIO().execute(() -> {
            Timber.d("geoIDFromPreferences: " + geoIDFromPreferences);
            List<Geo> geoList = geoDao.getGeosSinceFailure(geoIDFromPreferences);
            Timber.d("geoList to send");
            for (Geo g : geoList) {
                Timber.d("geo to send: %s", g);
            }
            sendGeoListToServer(geoList);
        });
    }

    private void sendGeoListToServer(List<Geo> geolist) {
        Call<List<Geo>> call = czoperApi.sendGeoList(geolist);

        call.enqueue(new Callback<List<Geo>>() {
            @Override
            public void onResponse(Call<List<Geo>> call, Response<List<Geo>> response) {
                Timber.d("send geo list onResponse");
                sharedPreferencesRepo.putGeoID(0);
                sharedPreferencesRepo.setIsGeoSend(true);

                if (sharedPreferencesRepo.getIsPositionSend()) {
                    assignGeoToPositionOnServer();
                }
            }

            @Override
            public void onFailure(Call<List<Geo>> call, Throwable t) {
                sharedPreferencesRepo.setIsPositionSend(false);
                Timber.d("send position list onFailure call:  " + call + "throw: " + t);
            }
        });
    }


    private void assignGeoToPositionOnServer() {
        sharedPreferencesRepo.setIsPositionSend(false);
        sharedPreferencesRepo.setIsGeoSend(false);
        Timber.d("assignGeoToPositionOnServer method");
        long lastAssignTimeFromPreferences = sharedPreferencesRepo.getLastAssignedTime();
        Timber.d("lastAssignTimeFromPreferences = " + lastAssignTimeFromPreferences);

        if (lastAssignTimeFromPreferences != 0) {
            appExecutors.diskIO().execute(() -> {
                List<PositionGeoJoin> positionGeoJoinList = positionGeoJoinDao.getAssignsSinceFailure(lastAssignTimeFromPreferences);
                List<RemotePositionGeoJoin> remotePositionGeoJoinList = new ArrayList<>();
                for (PositionGeoJoin p : positionGeoJoinList) {
                    Timber.d("assignListToSend = " + p);
                    remotePositionGeoJoinList.add(new RemotePositionGeoJoin(p.positionId, p.geoId));
                }
                sendAssignsToServer(remotePositionGeoJoinList);
            });

        } else {
            getSingleAssignToSend();
        }
    }

    private void getSingleAssignToSend() {
        appExecutors.diskIO().execute(() -> {
            PositionGeoJoin lastAssignment = positionGeoJoinDao.getLastAssignment();

            sendSingleAssignToServer(new RemotePositionGeoJoin(lastAssignment.getPositionId(), lastAssignment.getGeoId()));
        });
    }


    private void sendSingleAssignToServer(RemotePositionGeoJoin remotePositionGeoJoin) {
        Timber.d("sendSingleAssignToServer method");
        Call<RemotePositionGeoJoin> call = czoperApi.assignGeoToPosition(remotePositionGeoJoin);

        call.enqueue(new Callback<RemotePositionGeoJoin>() {
            @Override
            public void onResponse(Call<RemotePositionGeoJoin> call, Response<RemotePositionGeoJoin> response) {
                sharedPreferencesRepo.setLastAssginedTime(0);
                Timber.d("sendSingleAssignToServer onResponse");
            }

            @Override
            public void onFailure(Call<RemotePositionGeoJoin> call, Throwable t) {
                Timber.d("sendSingleAssignToServer onFailure");
                saveLastAssignedTimeToPreferences();
            }
        });
    }

    private void sendAssignsToServer(List<RemotePositionGeoJoin> remotePositionGeoJoinList) {
        Timber.d("sendAssignsToServer method");
        Call<List<RemotePositionGeoJoin>> call = czoperApi.assignGeoToPositionList(remotePositionGeoJoinList);

        call.enqueue(new Callback<List<RemotePositionGeoJoin>>() {
            @Override
            public void onResponse(Call<List<RemotePositionGeoJoin>> call, Response<List<RemotePositionGeoJoin>> response) {
                Timber.d("sendAssignsToServer onResponse");
                sharedPreferencesRepo.setLastAssginedTime(0);
            }

            @Override
            public void onFailure(Call<List<RemotePositionGeoJoin>> call, Throwable t) {
                Timber.d("sendAssignsToServer onFailure");
            }
        });
    }

    private void saveLastAssignedTimeToPreferences() {
        appExecutors.diskIO().execute(() -> {
            PositionGeoJoin lastAssignment = positionGeoJoinDao.getLastAssignment();
            long lastAssignedTime = lastAssignment.getAssignTime();
            Timber.d("lastAssignedTime = %s", lastAssignedTime);
            sharedPreferencesRepo.setLastAssginedTime(lastAssignedTime);
        });
    }


    public void assignGeoToPosition(PositionGeoJoin positionGeoJoin) {
        appExecutors.diskIO().execute(() -> positionGeoJoinDao.insert(positionGeoJoin));
    }


    public void updatePosition(Position position) {
        Timber.d("updating position ID = " + position.getPosition_id() + " czas: " + position.getLastLocationDate() + " status = " + position.getStatus());
        appExecutors.diskIO().execute(() -> positionDao.updatePosition(position));


        long positionIDFromPreferences = sharedPreferencesRepo.getPositionID();

        if (positionIDFromPreferences == 0) {
            sendPositionToServer(position);
        } else {
            getPositionsToSend(positionIDFromPreferences);
        }
    }

    public void getLatestGeo() {
        appExecutors.diskIO().execute(() -> {
            latestGeoFromDb = geoDao.loadLatestGeo();
            if (latestGeoFromDb != null) {
                Timber.d("latestGeoFromDb ID = " + latestGeoFromDb.getGeo_id() + " czas: " + latestGeoFromDb.getDate());
            } else {
                Timber.d("latestGeoFromDb is null");
            }
            positionManagerCallback.setLatestGeoFromDb(latestGeoFromDb);
        });
    }

    public void getLatestPosition() {
        appExecutors.diskIO().execute(() -> {
            latestPositionFromDb = positionDao.loadLatestPosition();
            if (latestPositionFromDb != null) {
                Timber.d("latestPositionFromDb ID = " + latestPositionFromDb.getPosition_id() + " czas: " + latestPositionFromDb.getLastLocationDate());
            } else {
                Timber.d("latestPositionFromDb is null");
            }
            positionManagerCallback.setLatestPositionFromDb(latestPositionFromDb);
        });
    }

    public void getOldestGeoForPosition(long positionID) {
        appExecutors.diskIO().execute(() -> {
            latestGeoFromDb = positionGeoJoinDao.getOldestGeoForPosition(positionID);
            if (latestGeoFromDb != null) {
                Timber.d("latestGeoFromDb ID = " + latestGeoFromDb.getGeo_id() + " czas: " + latestGeoFromDb.getDate());
            } else {
                Timber.d("latestGeoFromDb is null");
            }
            positionManagerCallback.setLatestGeoFromDb(latestGeoFromDb);
        });
    }

    public void getLatestGeoForTests() {
        appExecutors.diskIO().execute(() -> {
            Geo latestGeo = geoDao.loadLatestGeo();
            searchFragmentViewModelCallback.setLatestGeo(latestGeo);
        });
    }

    public List<Position> getAllPositions() {
        appExecutors.diskIO().execute(() -> {
            allPositions = positionDao.getAllPositions();
        });
        return allPositions;
    }

    public List<Geo> getAllGeos() {
        appExecutors.diskIO().execute(() -> {
            allGeos = geoDao.getAllGeos();
        });
        return allGeos;
    }

    public void getPositionsFromRange(String searchFromDay, String searchToDay) {
        Timber.d("getPositionsFromRange method searchFromDay = " + searchFromDay + " searchToDay = " + searchToDay);
        appExecutors.diskIO().execute(() -> {
            List<Position> positions = positionDao.getPositionsFromRange(searchFromDay, searchToDay);
            LiveData<List<Position>> livePositions = positionDao.getLivePositionsFromRange(searchFromDay, searchToDay);

            Timber.d("positions = " + positions);
            Timber.d("livePositions = " + livePositions.getValue());

            searchFragmentViewModelCallback.setObservablePositions(livePositions);

        });
    }

    public void getUsers(String login, String password) {
        Call<List<User>> call = czoperApi.getUsers(login, password);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Timber.d("getUsers onResponse");
                if (response.body() != null && response.body().size() > 0) {
                    saveUsersToDB(response.body());
                    saveUserIDToPreferences(login, response.body());
                    loginManagerCallback.onLoginSuccess();
                } else {
                    loginManagerCallback.onLoginFailure();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Timber.d("getUsers onFailure");
                loginManagerCallback.onLoginFailure();
            }
        });

    }

    private void saveUserIDToPreferences(String login, List<User> userList) {
        for (User user : userList) {
            if (user.getLogin().equals(login)) {
                sharedPreferencesRepo.setUserID(user.getUser_id());
            }
        }
    }

    private void saveUsersToDB(List<User> userList) {
        appExecutors.diskIO().execute(() -> {
            userDao.insertAll(userList);
        });
    }

    public void isUserLoggedIn() {
        appExecutors.diskIO().execute(() -> {
            List<User> userList = userDao.getAllUsers();
            if (userList != null && userList.size() > 0) {
                Timber.d("są userzy w bazie, a pierwszy to = " + userList.get(0).toString());
                loginManagerCallback.onLoginSuccess();
            }

        });

    }

    public List<User> getAllUsers() {
        appExecutors.diskIO().execute(() -> {
            allUsers = userDao.getAllUsers();
        });
        return allUsers;
    }

    public LiveData<Geo> getLatestGeoForUser(int user_id) {
        appExecutors.diskIO().execute(() -> {
            latestGeoForUser = geoDao.loadLatestGeoForUser(user_id);
        });
        return latestGeoForUser;
    }
}
