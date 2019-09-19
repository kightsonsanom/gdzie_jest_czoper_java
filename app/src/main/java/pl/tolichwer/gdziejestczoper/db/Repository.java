package pl.tolichwer.gdziejestczoper.db;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import android.location.Location;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.sqlite.db.SimpleSQLiteQuery;

import pl.tolichwer.gdziejestczoper.AppExecutors;
import pl.tolichwer.gdziejestczoper.api.ApiResponse;
import pl.tolichwer.gdziejestczoper.api.CzoperApi;
import pl.tolichwer.gdziejestczoper.api.NetworkBoundResource;
import pl.tolichwer.gdziejestczoper.db.dao.PositionDao;
import pl.tolichwer.gdziejestczoper.db.dao.GeoDao;
import pl.tolichwer.gdziejestczoper.db.dao.PositionGeoJoinDao;
import pl.tolichwer.gdziejestczoper.db.dao.UserDao;
import pl.tolichwer.gdziejestczoper.services.GeocodeAddressCallback;
import pl.tolichwer.gdziejestczoper.ui.login.LoginManagerCallback;
import pl.tolichwer.gdziejestczoper.utils.Converters;
import pl.tolichwer.gdziejestczoper.viewobjects.AbsentLiveData;
import pl.tolichwer.gdziejestczoper.viewobjects.Position;
import pl.tolichwer.gdziejestczoper.viewobjects.Geo;
import pl.tolichwer.gdziejestczoper.viewobjects.PositionGeoJoin;
import pl.tolichwer.gdziejestczoper.services.PositionManagerCallback;
import pl.tolichwer.gdziejestczoper.ui.search.SearchFragmentViewModelCallback;
import pl.tolichwer.gdziejestczoper.viewobjects.RemotePositionGeoJoin;
import pl.tolichwer.gdziejestczoper.viewobjects.Resource;
import pl.tolichwer.gdziejestczoper.viewobjects.User;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private SharedPreferencesRepository sharedPreferencesRepository;

    private Geo latestGeoFromDb;
    private Position latestPositionFromDb;
    private List<Geo> allGeos;
    private List<Position> allPositions;
    private LiveData<Geo> latestGeoForUser;

    @Inject
    public Repository(PositionDao positionDao,
                      GeoDao geoDao,
                      PositionGeoJoinDao positionGeoJoinDao,
                      UserDao userDao,
                      AppExecutors appExecutors,
                      CzoperApi czoperApi,
                      SharedPreferencesRepository sharedPreferencesRepository) {

        this.appExecutors = appExecutors;
        this.positionDao = positionDao;
        this.positionGeoJoinDao = positionGeoJoinDao;
        this.geoDao = geoDao;
        this.userDao = userDao;
        this.czoperApi = czoperApi;
        this.sharedPreferencesRepository = sharedPreferencesRepository;

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
                geoDao.insertAll(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Geo> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Geo>> loadFromDb() {
                return Transformations.map(geoDao.getLatestGeoForDistinctUsers(new SimpleSQLiteQuery("SELECT g.* FROM geo g  INNER JOIN (\n" +
                        "    SELECT userID, MAX(date) maxCzas\n" +
                        "    FROM `geo`\n" +
                        "    GROUP BY userID\n" +
                        "    ) t ON g.userID=t.userID AND g.date = t.maxCzas")), new Function<List<Geo>, List<Geo>>() {
                    @Override
                    public List<Geo> apply(List<Geo> input) {
                        for (Geo geo : input) {
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
            positionDao.insertPosition(position);
        });

        long positionIdFromPreferences = sharedPreferencesRepository.getPositionID();

        if (positionIdFromPreferences == 0) {
            sendPositionToServer(position);
        } else {
            getPositionsToSend(positionIdFromPreferences);
        }
    }


    public void updatePosition(Position position) {
        appExecutors.diskIO().execute(() -> positionDao.updatePosition(position));


        long positionIdFromPreferences = sharedPreferencesRepository.getPositionID();

        if (positionIdFromPreferences == 0) {
            sendPositionToServer(position);
        } else {
            getPositionsToSend(positionIdFromPreferences);
        }
    }

    private void sendPositionToServer(Position position) {

        Call<Void> call = czoperApi.sendPosition(position.getUserID(), position);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                sharedPreferencesRepository.setIsPositionSend(true);
                sharedPreferencesRepository.putPositionID(0);
                if (sharedPreferencesRepository.getIsGeoSend()) {
                    assignGeoToPositionOnServer();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                sharedPreferencesRepository.putPositionID(position.getId());
                sharedPreferencesRepository.setIsGeoSend(false);
                saveLastAssignedTimeToPreferences();
            }
        });
    }

    private void getPositionsToSend(long positionIdFromPreferences) {
        appExecutors.diskIO().execute(() -> {
            List<Position> positionList = positionDao.getPositionsSinceFailure(positionIdFromPreferences);
            for (Position p : positionList) {
            }
            sendPositionsListToServer(positionList);
        });
    }

    private void sendPositionsListToServer(List<Position> positionlist) {

        Call<Void> call = czoperApi.sendPositionList(sharedPreferencesRepository.getUserID(), positionlist);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                sharedPreferencesRepository.setIsPositionSend(true);
                sharedPreferencesRepository.putPositionID(0);

                if (sharedPreferencesRepository.getIsGeoSend()) {
                    assignGeoToPositionOnServer();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                sharedPreferencesRepository.setIsGeoSend(false);
            }
        });
    }

    public void postGeo(Geo geo) {
        appExecutors.diskIO().execute(() -> {
            geoDao.insertGeo(geo);
        });

        long geoIdFromPreferences = sharedPreferencesRepository.getGeoID();

        if (geoIdFromPreferences == 0) {
            sendGeoToServer(geo);
        } else {
            getGeosToSend(geoIdFromPreferences);
        }
    }

    private void sendGeoToServer(Geo geo) {
        Call<Void> call = czoperApi.sendGeo(geo.getUserID(), geo);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                sharedPreferencesRepository.putGeoID(0);
                sharedPreferencesRepository.setIsGeoSend(true);

                if (sharedPreferencesRepository.getIsPositionSend()) {
                    assignGeoToPositionOnServer();
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                sharedPreferencesRepository.putGeoID(geo.getId());
                saveLastAssignedTimeToPreferences();
                sharedPreferencesRepository.setIsPositionSend(false);
            }
        });
    }

    private void getGeosToSend(long geoIdFromPreferences) {
        appExecutors.diskIO().execute(() -> {
            List<Geo> geoList = geoDao.getGeosSinceFailure(geoIdFromPreferences);
            sendGeoListToServer(geoList);
        });
    }

    private void sendGeoListToServer(List<Geo> geolist) {
        Call<Void> call = czoperApi.sendGeoList(sharedPreferencesRepository.getUserID(), geolist);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                sharedPreferencesRepository.putGeoID(0);
                sharedPreferencesRepository.setIsGeoSend(true);

                if (sharedPreferencesRepository.getIsPositionSend()) {
                    assignGeoToPositionOnServer();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                sharedPreferencesRepository.setIsPositionSend(false);
            }
        });
    }


    private void assignGeoToPositionOnServer() {
        sharedPreferencesRepository.setIsPositionSend(false);
        sharedPreferencesRepository.setIsGeoSend(false);
        long lastAssignTimeFromPreferences = sharedPreferencesRepository.getLastAssignedTime();

        if (lastAssignTimeFromPreferences != 0) {
            appExecutors.diskIO().execute(() -> {
                List<PositionGeoJoin> positionGeoJoinList = positionGeoJoinDao.getAssignsSinceFailure(lastAssignTimeFromPreferences);
                List<RemotePositionGeoJoin> remotePositionGeoJoinList = new ArrayList<>();
                for (PositionGeoJoin p : positionGeoJoinList) {
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
        Call<Void> call = czoperApi.assignGeoToPosition(remotePositionGeoJoin);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                sharedPreferencesRepository.setLastAssginedTime(0);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                saveLastAssignedTimeToPreferences();
            }
        });
    }

    private void sendAssignsToServer(List<RemotePositionGeoJoin> remotePositionGeoJoinList) {
        Call<Void> call = czoperApi.assignGeoToPositionList(remotePositionGeoJoinList);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                sharedPreferencesRepository.setLastAssginedTime(0);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    private void saveLastAssignedTimeToPreferences() {
        appExecutors.diskIO().execute(() -> {
            PositionGeoJoin lastAssignment = positionGeoJoinDao.getLastAssignment();
            long lastAssignedTime = lastAssignment.getAssignTime();
            sharedPreferencesRepository.setLastAssginedTime(lastAssignedTime);
        });
    }


    public void assignGeoToPosition(PositionGeoJoin positionGeoJoin) {
        appExecutors.diskIO().execute(() -> positionGeoJoinDao.insert(positionGeoJoin));
    }

    public void getLatestGeo() {
        appExecutors.diskIO().execute(() -> {
            latestGeoFromDb = geoDao.loadLatestGeo(sharedPreferencesRepository.getUserID());
            positionManagerCallback.setLatestGeoFromDb(latestGeoFromDb);
        });
    }

    public void getLatestPosition() {
        appExecutors.diskIO().execute(() -> {
            latestPositionFromDb = positionDao.loadLatestPosition(sharedPreferencesRepository.getUserID());
            positionManagerCallback.setLatestPositionFromDb(latestPositionFromDb);
        });
    }

    public void getOldestGeoForPosition(long positionId) {
        appExecutors.diskIO().execute(() -> {
            latestGeoFromDb = positionGeoJoinDao.getOldestGeoForPosition(positionId);
            positionManagerCallback.setLatestGeoFromDb(latestGeoFromDb);
        });
    }

    public void getLatestGeoForTests() {
        appExecutors.diskIO().execute(() -> {
            Geo latestGeo = geoDao.loadLatestGeo(sharedPreferencesRepository.getUserID());
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

    public LiveData<Resource<TreeMap<String, List<Position>>>> getPositionsFromRange(String userName, long searchFromDay, long searchToDay) {
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


                return Transformations.map(positionDao.getLivePositionsFromRangeAndUser(userName, searchFromDay, searchToDay), input -> {
                    TreeMap<String, List<Position>> positionMap = new TreeMap<>();

                    for (int i = 0; i < days.size(); i++) {
                        List<Position> positionsForDay = new ArrayList<>();
                        for (Position p : input) {

                            if (p.getFirstLocationDate() > days.get(i) && p.getFirstLocationDate() < days.get(i) + DAY_DURATION_INTERVAL) {
                                positionsForDay.add(p);
                            }
                        }
                        Converters.sortPositions(positionsForDay);
                        positionMap.put(Converters.getDayFromMilis(days.get(i)), positionsForDay);

                    }
                    return positionMap;
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
                loginManagerCallback.onLoginFailure();
            }
        });

    }

    private void getInitialPositionData(String login) {

        long timeFrom = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000);
        long timeTo = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000) + 86400000;
        Call<List<Position>> call = czoperApi.getPositionsForDayAndUser(login, timeFrom, timeTo);

        call.enqueue(new Callback<List<Position>>() {
            @Override
            public void onResponse(Call<List<Position>> call, Response<List<Position>> response) {

                if (response.body() != null && response.body().size() > 0) {
                    positionDao.insertAll(response.body());
                    loginManagerCallback.onLoginSuccess();
                } else {
                    loginManagerCallback.onLoginFailure();
                }
            }

            @Override
            public void onFailure(Call<List<Position>> call, Throwable t) {
                loginManagerCallback.onLoginFailure();
            }
        });
    }

    private void saveUserIDToPreferences(String login, List<User> userList) {
        for (User user : userList) {
            if (user.getLogin().equals(login)) {
                sharedPreferencesRepository.setUserID(user.getUserID());
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

    public LiveData<Geo> getLatestGeoForUser(int userID) {
        appExecutors.diskIO().execute(() -> {
            latestGeoForUser = geoDao.loadLatestGeoForUser(userID);
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

            LiveData<List<Position>> positionList;

            @Override
            protected void saveCallResult(@NonNull List<Position> item) {

                int userID = userDao.getUserID(name);
                List<Position> positionsToRemove = new ArrayList();

                for (Position networkPosition : item) {
                    networkPosition.setUserID(userID);

                    if (positionList.getValue() != null) {
                        for (Position dbPosition : positionList.getValue()) {
                            if (networkPosition.getId() == dbPosition.getId()) {
                                if (networkPosition.getLastLocationDate() < dbPosition.getLastLocationDate()) {
                                    positionsToRemove.add(networkPosition);
                                }
                            }
                        }
                    }
                }

                item.removeAll(positionsToRemove);
                positionDao.insertAll(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Position> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Position>> loadFromDb() {


                positionList = positionDao.loadPositionsForDayAndUser(name, rangeFrom, rangeTo);

                if (positionList.getValue() != null) {
                    for (Position p : positionList.getValue()) {
                    }
                }
                return positionList;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Position>>> createCall() {
                return czoperApi.getLivePositionsForDayAndUser(name, rangeFrom, rangeTo);
            }

        }.asLiveData();
    }

    public void setNewLocation(Location location) {
        positionManagerCallback.setNewLocation(location);
    }


    public void getReverseGeocoding(String url) {
        Call<JsonElement> call = czoperApi.getReverseGeocoding(url);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.body() != null) {
                    geocodeAddressCallback.onSuccessGetAddress(response.body().getAsJsonObject());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                geocodeAddressCallback.onFailureGetAddress();
            }
        });
    }

    public int getUserID() {
        return sharedPreferencesRepository.getUserID();
    }

    public void sendLogFile()
     {
        String fileNameToRead = "czoperlog.txt";
        File file = new File(Environment.getExternalStorageDirectory(),fileNameToRead);

        String fileCopyName = "czoperlogCopy.txt";
        File fileCopy = new File(Environment.getExternalStorageDirectory(),fileCopyName);

         try {
             copy(file, fileCopy);
         } catch (IOException e) {
             e.printStackTrace();
         }

         RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        fileCopy
                );

        String fileNameToSend = "czoperlog" + getUserID() + ".txt";
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", fileNameToSend,requestFile);
        Converters.appendLog("sending the log file...");
        Call<Void> call = czoperApi.uploadLogs(multipartBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Converters.readFromLogFile();
                Converters.appendLog("successfuly uploaded file");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Converters.appendLog("did not manage to send the file " + t);

            }
        });
    }

    void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
}
