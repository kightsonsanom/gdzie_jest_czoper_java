package pl.tolichwer.gdziejestczoper.viewobjects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = User.TABLE_NAME)
public class User {

    public static final String TABLE_NAME = "user";

    @PrimaryKey
    @NonNull
    private int userID;

    private String name;
    private String login;
    private String password;

    public User() {
    }

    @Ignore
    public User(@NonNull int userID, String name, String login, String password) {
        this.userID = userID;
        this.name = name;
        this.login = login;
        this.password = password;
    }

    @NonNull
    public int getUserID() {
        return userID;
    }

    public void setUserID(@NonNull int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
