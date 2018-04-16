package com.example.asinit_user.gdziejestczoper.viewobjects;


public class MapGeo {

    private User user;
    private Geo geo;

    public MapGeo() {
    }

    public MapGeo(User user, Geo geo) {
        this.user = user;
        this.geo = geo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    @Override
    public String toString() {
        return "MapGeo{" +
                "user=" + user +
                ", geo=" + geo +
                '}';
    }
}
