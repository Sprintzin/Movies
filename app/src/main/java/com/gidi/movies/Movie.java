package com.gidi.movies;


public class Movie {

    private long id;
    private String title;
    private String plot;
    private String year;
    private String type;
    private String imdbID;
    private String poster;
    private String actors;
    private String imdbRating;
    private String director;
    private boolean seen;


    public Movie(String title, String type, String year, String imdbID, String poster) {
        this.title = title;
        this.year = year;
        this.type = type;
        this.imdbID = imdbID;
        this.poster = poster;

    }


    public Movie(long id, String title, String poster, String year, String type, String plot, String director, String actors, String imdbID, String imdbRating, boolean seen) {
        this.id = id;
        this.title = title;
        this.plot = plot;
        this.year = year;
        this.type = type;
        this.director = director;
        this.imdbID = imdbID;
        this.poster = poster;
        this.actors = actors;
        this.imdbRating = imdbRating;
        this.seen = seen;
    }


    // =====================================toString===================================


    @Override
    public String toString() {

        return title;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }


    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}