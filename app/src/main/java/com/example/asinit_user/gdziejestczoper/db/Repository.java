package com.example.asinit_user.gdziejestczoper.db;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.db.SimpleSQLiteQuery;
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
import com.example.asinit_user.gdziejestczoper.services.GeocodeAddressCallback;
import com.example.asinit_user.gdziejestczoper.ui.login.LoginManagerCallback;
import com.example.asinit_user.gdziejestczoper.utils.Converters;
import com.example.asinit_user.gdziejestczoper.viewobjects.AbsentLiveData;
import com.example.asinit_user.gdziejestczoper.viewobjects.MapGeo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.PositionGeoJoin;
import com.example.asinit_user.gdziejestczoper.services.PositionManagerCallback;
import com.example.asinit_user.gdziejestczoper.ui.search.SearchFragmentViewModelCallback;
import com.example.asinit_user.gdziejestczoper.viewobjects.RemotePositionGeoJoin;
import com.example.asinit_user.gdziejestczoper.viewobjects.Resource;
import com.example.asinit_user.gdziejestczoper.viewobjects.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class Repository {

    public static final int DAY_DURATION_INTERVAL = 86400000;
    private PositionManagerCallback positionManagerCallback;
    private SearchFragmentViewModelCallback searchFragmentViewModelCallback;
    private LoginManagerCallback loginManagerCallback;
    private GeocodeAddressCallback geocodeAddressCallback;


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

    public void setGeocodeAddressCallback(GeocodeAddressCallback geocodeAddressCallback) {
        this.geocodeAddressCallback = geocodeAddressCallback;
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

    public LiveData<Resource<List<Geo>>> getLatestGeoForUsers() {
        return new NetworkBoundResource<List<Geo>, List<Geo>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<Geo> item) {
                Timber.d("saving latestGeoForUsers call result");
                geoDao.insertAll(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Geo> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Geo>> loadFromDb() {
                Timber.d("Loading data from db");
                return Transformations.map(geoDao.getLatestGeoForDistinctUsers(new SimpleSQLiteQuery("SELECT g.* FROM geo g  INNER JOIN (\n" +
                        "    SELECT user_id, MAX(date) maxCzas\n" +
                        "    FROM `geo`\n" +
                        "    GROUP BY user_id\n" +
                        "    ) t ON g.user_id=t.user_id AND g.date = t.maxCzas")), new Function<List<Geo>, List<Geo>>() {
                    @Override
                    public List<Geo> apply(List<Geo> input) {
                        for (Geo geo : input) {
                            Timber.d("geo from db = " + geo);
                        }
                        return input;
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Geo>>> createCall() {
                return czoperApi.getLatestGeoForDistinctUsers();
            }
        }.asLiveData();
    }

    public void postPosition(Position position) {
        appExecutors.diskIO().execute(() -> {
            Timber.d("inserting position into DB ID: " + position.getId() + " czas: " + position.getLastLocationDate());
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

        Call<Void> call = czoperApi.sendPosition(sharedPreferencesRepo.getUserID(), position);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Timber.d("send single position onResponse");
                sharedPreferencesRepo.setIsPositionSend(true);
                sharedPreferencesRepo.putPositionID(0);

                Timber.d(" wasGeoSend in sendPositionToServer = " + sharedPreferencesRepo.getIsGeoSend());
                if (sharedPreferencesRepo.getIsGeoSend()) {
                    assignGeoToPositionOnServer();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.d("postPosition send position onFailure call:  " + call + "throw: " + t);
                sharedPreferencesRepo.putPositionID(position.getId());
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

        Call<Void> call = czoperApi.sendPositionList(sharedPreferencesRepo.getUserID(), positionlist);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Timber.d("send position list onResponse");
                sharedPreferencesRepo.setIsPositionSend(true);
                sharedPreferencesRepo.putPositionID(0);

                if (sharedPreferencesRepo.getIsGeoSend()) {
                    assignGeoToPositionOnServer();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.d("send position list onFailure call:  " + call + "throw: " + t);
                sharedPreferencesRepo.setIsGeoSend(false);
            }
        });
    }

    public void postGeo(Geo geo) {
        appExecutors.diskIO().execute(() -> {
            Timber.d("inserting geo into DB ID: " + geo.getId() + " czas: " + geo.getDate());
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
        Call<Void> call = czoperApi.sendGeo(sharedPreferencesRepo.getUserID(), geo);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Timber.d("send single geo onResponse");
                sharedPreferencesRepo.putGeoID(0);
                sharedPreferencesRepo.setIsGeoSend(true);

                Timber.d(" wasPositionSend in sendPositionToServer = " + sharedPreferencesRepo.getIsPositionSend());
                if (sharedPreferencesRepo.getIsPositionSend()) {
                    assignGeoToPositionOnServer();
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                sharedPreferencesRepo.putGeoID(geo.getId());
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
        Call<Void> call = czoperApi.sendGeoList(sharedPreferencesRepo.getUserID(), geolist);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Timber.d("send geo list onResponse");
                sharedPreferencesRepo.putGeoID(0);
                sharedPreferencesRepo.setIsGeoSend(true);

                if (sharedPreferencesRepo.getIsPositionSend()) {
                    assignGeoToPositionOnServer();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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
        Timber.d("remotePositionGeo from sendSingleAssignToServer = " + remotePositionGeoJoin.toString());
        Call<Void> call = czoperApi.assignGeoToPosition(remotePositionGeoJoin);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                sharedPreferencesRepo.setLastAssginedTime(0);
                Timber.d("sendSingleAssignToServer onResponse");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.d("sendSingleAssignToServer onFailure");
                saveLastAssignedTimeToPreferences();
            }
        });
    }

    private void sendAssignsToServer(List<RemotePositionGeoJoin> remotePositionGeoJoinList) {
        Timber.d("sendAssignsToServer method");
        Call<Void> call = czoperApi.assignGeoToPositionList(remotePositionGeoJoinList);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Timber.d("sendAssignsToServer onResponse");
                sharedPreferencesRepo.setLastAssginedTime(0);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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
        Timber.d("updating position ID = " + position.getId() + " czas: " + position.getLastLocationDate() + " status = " + position.getStatus());
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
            latestGeoFromDb = geoDao.loadLatestGeo(sharedPreferencesRepo.getUserID());
            if (latestGeoFromDb != null) {
                Timber.d("latestGeoFromDb ID = " + latestGeoFromDb.getId() + " czas: " + latestGeoFromDb.getDate());
            } else {
                Timber.d("latestGeoFromDb is null");
            }
            positionManagerCallback.setLatestGeoFromDb(latestGeoFromDb);
        });
    }

    public void getLatestPosition() {
        appExecutors.diskIO().execute(() -> {
            latestPositionFromDb = positionDao.loadLatestPosition(sharedPreferencesRepo.getUserID());
            if (latestPositionFromDb != null) {
                Timber.d("latestPositionFromDb ID = " + latestPositionFromDb.getId() + " czas: " + latestPositionFromDb.getLastLocationDate());
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
                Timber.d("latestGeoFromDb ID = " + latestGeoFromDb.getId() + " czas: " + latestGeoFromDb.getDate());
            } else {
                Timber.d("latestGeoFromDb is null");
            }
            positionManagerCallback.setLatestGeoFromDb(latestGeoFromDb);
        });
    }

    public void getLatestGeoForTests() {
        appExecutors.diskIO().execute(() -> {
            Geo latestGeo = geoDao.loadLatestGeo(sharedPreferencesRepo.getUserID());
            searchFragmentViewModelCallback.setLatestGeo(latestGeo);
        });
    }

    public List<Position> getAllPositions() {
        appExecutors.diskIO().execute(() -> allPositions = positionDao.getAllPositions());
        return allPositions;
    }

    public List<Geo> getAllGeos() {
        appExecutors.diskIO().execute(() -> allGeos = geoDao.getAllGeos());
        return allGeos;
    }

//    public void getPositionsFromRange(String searchFromDay, String searchToDay) {
//        Timber.d("getPositionsFromRange method searchFromDay = " + searchFromDay + " searchToDay = " + searchToDay);
//        appExecutors.diskIO().execute(() -> {
//            List<Position> positions = positionDao.getPositionsFromRange(searchFromDay, searchToDay);
//            LiveData<List<Position>> livePositions = positionDao.getLivePositionsFromRange(searchFromDay, searchToDay);
//
//            Timber.d("positions = " + positions);
//            Timber.d("livePositions = " + livePositions.getValue());
//
//            searchFragmentViewModelCallback.setObservablePositions(livePositions);
//
//        });
//    }

    public LiveData<Resource<TreeMap<String, List<Position>>>> getPositionsFromRange(String userName, long searchFromDay, long searchToDay) {
        Timber.d("getPositionsFromRange method searchFromDay = " + searchFromDay + " searchToDay = " + searchToDay);
        return new NetworkBoundResource<TreeMap<String, List<Position>>, TreeMap<String, List<Position>>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull TreeMap<String, List<Position>> item) {

            }

            @Override
            protected boolean shouldFetch(@Nullable TreeMap<String, List<Position>> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<TreeMap<String, List<Position>>> loadFromDb() {
                List<Long> days = new ArrayList<>();
                for (long i = searchFromDay; i < searchToDay; i += DAY_DURATION_INTERVAL) {
                    days.add(i);
                }
                Collections.sort(days);


                return Transformations.map(positionDao.getLivePositionsFromRangeAndUser(userName, searchFromDay, searchToDay), new Function<List<Position>, TreeMap<String, List<Position>>>() {
                    @Override
                    public TreeMap<String, List<Position>> apply(List<Position> input) {
                        TreeMap<String, List<Position>> positionMap = new TreeMap<>();

                        for (int i = 0; i < days.size(); i++) {
                            Timber.d("making transformation for day: " + days.get(i));
                            List<Position> positionsForDay = new ArrayList<>();
                            for (Position p : input) {

                                if (p.getFirstLocationDate() > days.get(i) && p.getFirstLocationDate() < days.get(i) + DAY_DURATION_INTERVAL) {
                                    Timber.d("position from transformation = " + p.toString());
                                    positionsForDay.add(p);
                                }
                            }
                            Converters.sortPositions(positionsForDay);
                            positionMap.put(Converters.getDayFromMilis(days.get(i)), positionsForDay);

                        }
                        Timber.d("map size = " + positionMap.size());
                        return positionMap;
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<TreeMap<String, List<Position>>>> createCall() {
                return AbsentLiveData.create();
            }

        }.asLiveData();
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
                Timber.d("user.getLogin = " + user.getLogin() + " " + user.getUser_id());
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

    public LiveData<Resource<List<String>>> getAllUsersNames() {
        return new NetworkBoundResource<List<String>, List<String>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<String> item) {

            }

            @Override
            protected boolean shouldFetch(@Nullable List<String> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<String>> loadFromDb() {
                return userDao.getAllUserNames();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<String>>> createCall() {
                return AbsentLiveData.create();
            }
        }.asLiveData();
    }

    public LiveData<Geo> getLatestGeoForUser(int user_id) {
        appExecutors.diskIO().execute(() -> {
            latestGeoForUser = geoDao.loadLatestGeoForUser(user_id);
        });
        return latestGeoForUser;
    }

    public LiveData<Resource<List<User>>> getAllUsers() {
        return new NetworkBoundResource<List<User>, List<User>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<User> item) {

            }

            @Override
            protected boolean shouldFetch(@Nullable List<User> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<User>> loadFromDb() {
                return userDao.getAllLiveUsers();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<User>>> createCall() {
                return AbsentLiveData.create();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Position>>> getPostionsForUserAndDay(String name, long rangeFrom, long rangeTo) {
        return new NetworkBoundResource<List<Position>, List<Position>>(appExecutors) {


            @Override
            protected void saveCallResult(@NonNull List<Position> item) {

                int userID = userDao.getUserID(name);
                Timber.d("position for day and user from network:");
                for (Position p : item) {
                    p.setUser_id(userID);
                    Timber.d("modified user = " + p.toString());
                }
                positionDao.insertAll(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Position> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Position>> loadFromDb() {


                LiveData<List<Position>> positionList = positionDao.loadPositionsForDayAndUser(name, rangeFrom, rangeTo);
                Timber.d("list of position = ");

                if (positionList.getValue() != null) {

                    for (Position p : positionList.getValue()) {
                        Timber.d("element = " + p.toString());
                    }
                }

                return positionList;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Position>>> createCall() {
                return czoperApi.getPositionsForDayAndUser(name, rangeFrom, rangeTo);
            }

        }.asLiveData();
    }

    public void setNewLocation(Location location) {
        positionManagerCallback.setNewLocation(location);
    }

    public void setMockUser() {
        List<User> mockUserList = new ArrayList<>();
        mockUserList.add(new User(1, "tomek", "tomek", "tomek"));
        saveUsersToDB(mockUserList);
        saveUserIDToPreferences("tomek", mockUserList);
        loginManagerCallback.onLoginSuccess();
    }

    public String getTestData() {
        return "repository test";
    }

    public void getReverseGeocoding(String url) {
        Call<JsonElement> call = czoperApi.getReverseGeocoding(url);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Timber.d("response from geocoding api = " + response.body());
                if (response.body() != null) {
                    Timber.d("response.body().getClass() = " + response.body().getClass());
                    geocodeAddressCallback.onSuccessGetAddress(response.body().getAsJsonObject());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Timber.d("Geocoding api failure");
                geocodeAddressCallback.onFailureGetAddress();
            }
        });
    }

    public List<User> getUserNames() {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                allUsers = userDao.getAllUsers();
            }
        });

        return allUsers;
    }
}
