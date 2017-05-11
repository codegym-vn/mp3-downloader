/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3downloader;

import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author nhatnk
 */
public class PlaylistTableModel extends AbstractTableModel{
    private ArrayList<Playlist> playlists;

    public PlaylistTableModel(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }
    

    @Override
    public int getRowCount() {
        return playlists.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Playlist p = playlists.get(rowIndex);
        switch(columnIndex) {
            case 0: return p.getPictureUrl();
            case 1: return p.getTitle();
            case 2: return p.getArtist();
            case 3: return p.getGenre();
            case 4: return p.getTotalListen();
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        switch(column){
            case 0: return "";
            case 1: return "Title";
            case 2: return "Artist";
            case 3: return "Genre";
            case 4: return "Listen";
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return JLabel.class;
        }
        return String.class;
    }
    
    
    public Playlist searchPlaylist(String id) {
        for (Playlist p : playlists) {
            if (p.getId() == null ? id == null : p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
    
    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }
    
    
}
