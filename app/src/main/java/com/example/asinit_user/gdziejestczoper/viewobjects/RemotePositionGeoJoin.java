package com.example.asinit_user.gdziejestczoper.viewobjects;

//Osobna klasa do wysyłania powiązania z serwerem, żeby uprościć strukturę danych po stronie serwera.
// Dzięki temu tabela mapująca pozycję z geo niepotrzebuje dodatkowej kolumny i możliwe jest zachowanie mapowania ManyToMany
// I tak informacja o ostatnio wysłanym powiązaniu potrzebna jest tylko przy wysyłaniu powiązania.
public class RemotePositionGeoJoin {

    private long positionId;
    private long geoId;

    public RemotePositionGeoJoin() {
    }

    public RemotePositionGeoJoin(long positionId, long geoId) {
        this.positionId = positionId;
        this.geoId = geoId;
    }

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    public long getGeoId() {
        return geoId;
    }

    public void setGeoId(long geoId) {
        this.geoId = geoId;
    }

    @Override
    public String toString() {
        return "RemotePositionGeoJoin{" +
                "positionId=" + positionId +
                ", geoId=" + geoId +
                '}';
    }
}
