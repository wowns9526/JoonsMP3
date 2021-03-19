package com.example.joonsmp3;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class MusicData {

    private String id;
    private String artist;
    private String title;
    private String albumArt;
    private String duration;
    private int liked;

    public MusicData(String id, String artist, String title, String albumArt, String duration, int liked) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.albumArt = albumArt;
        this.duration = duration;
        this.liked = liked;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public String getDuration() {
        return duration;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MusicData{" +
                "id='" + id + '\'' +
                ", artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", albumArt='" + albumArt + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicData musicData = (MusicData) o;
        return Objects.equals(id, musicData.id);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id, artist, title, albumArt, duration);
    }

}
