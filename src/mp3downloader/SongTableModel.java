/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3downloader;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author nhatnk
 */
public class SongTableModel  extends AbstractTableModel{
    private ArrayList<Song> songs;

    public SongTableModel(ArrayList<Song> songs) {
        this.songs = songs;
    }    

    @Override
    public int getRowCount() {
        return this.songs.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Song s = this.songs.get(rowIndex);
        switch (columnIndex) {
            case 0: return s.isSelected();
            case 1: return s.getTitle();
            case 2: return s.getPerformer();
            case 3: return s.isDownloaded()?"downloaded":"";
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {       
        if (columnIndex == 0)
            return Boolean.class;
        return String.class;
    }

    @Override
    public String getColumnName(int column) {
        switch(column){
            case 0: return "";
            case 1: return "Title";
            case 2: return "Artist";
            case 3: return "Downloaded";
        }
        return "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            boolean isSlected = (boolean)aValue;
            this.songs.get(rowIndex).setSelected(isSlected);
        }
    }
    
    public Song searchSong(String id) {
        for (Song song : songs) {
            if (song.getId() == null ? id == null : song.getId().equals(id)) {
                return song;
            }
        }
        return null;
    }
    
    public void refresh(String songId){
        for (int i = 0; i < this.songs.size(); i++) {
            Song s= this.songs.get(i);
            if(s.getId().equals(songId)) {
                s.setDownloaded(true);
                fireTableRowsUpdated(i, i);
                break;
            }
        }
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }
    
    
}
