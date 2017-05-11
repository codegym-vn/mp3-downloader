/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3downloader;

/**
 *
 * @author nhatnk
 */
public class Playlist {
    String id;
    String title;
    String artist;
    String genre;
    String pictureUrl;
    long totalListen;
    boolean selected;   

    public Playlist(String id, String title, String artist, String genre, String pictureUrl, long totalListen) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.pictureUrl = pictureUrl;
        this.totalListen = totalListen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getTotalListen() {
        return totalListen;
    }

    public void setTotalListen(long totalListen) {
        this.totalListen = totalListen;
    }

    
}
