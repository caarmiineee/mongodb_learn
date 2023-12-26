package com.example.mongolearn;

import java.util.List;

public class Movie {
    String plot;
    List<String> genres;
    String title;
    Integer releaseYear;

    public String getPlot() {
        return plot;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getTitle() {
        return title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "plot='" + plot + '\'' +
                ", genres=" + genres +
                ", title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                '}';
    }
}