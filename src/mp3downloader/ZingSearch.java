/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import static mp3downloader.Category.BASE_URL;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author nhatnk
 */
public class ZingSearch {

    static String privateKey = "5a9bb3f204830f2b42ebfa1532654d79";
    static String publicKey = "a8c840ab0e9d20feffb4a9f7f9284c9171a1810b7a";
    static String searchUrl = "http://api.mp3.zing.vn/api/search?";
    static String detailUrl = "http://api.mp3.zing.vn/api/detail?";

    public static ArrayList<Song> searchSong(String term) {
        try {
            String content = getSearchResult(term, 0);
            
            return readSongsFromContent(content);
        } catch (Exception ex) {
            Logger.getLogger(ZingSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<Song>();
    }

    private static ArrayList<Song> readSongsFromContent(String content) throws ParseException {
        ArrayList<Song> songs = new ArrayList<Song>();
        JSONParser parser = new JSONParser();
        
        JSONObject obj = (JSONObject)parser.parse(content);
        JSONArray items = (JSONArray)obj.get("Data");
        
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = (JSONObject)items.get(i);
            String id = (String)item.get("ID");
            String title = (String) item.get("Title");
            String performer = (String) item.get("Artist");
            String thumbnail = (String) item.get("ArtistAvatar");
            String source = "";
            if (item.get("LinkDownload320") != null) {
                source = (String) item.get("LinkDownload320");
            } else if (item.get("LinkDownload128") != null){
                source = (String) item.get("LinkDownload128");
            }           
            String type = "mp3";
            Song song = new Song(id, title, performer, source, thumbnail, type);
            songs.add(song);
        }
        return songs;
    }

    private static String getSearchResult(String term, int searchType) throws UnsupportedEncodingException, MalformedURLException, IOException {
        String data = "{\"kw\": \"" + term + "\", \"t\": "+searchType+", \"rc\": 50}";
        String jsonData = URLEncoder.encode(Base64.getEncoder().encodeToString(data.getBytes("UTF-8")), "UTF-8");
        String signature = hash_hmac(jsonData, privateKey);
        String urlString = searchUrl + "publicKey=" + publicKey + "&signature=" + signature + "&jsondata=" + jsonData;
        URL url = new URL(urlString);
        URLConnection urlConn = url.openConnection();
        urlConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
        InputStream is = urlConn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        is.close();
        String content = sb.toString();
        return content;
    }

    private static String getDetailResult(String id, String searchType) throws UnsupportedEncodingException, MalformedURLException, IOException {
        String data = "{\"id\": \"" + id + "\", \"t\": \""+searchType+"\"}";
        String jsonData = URLEncoder.encode(Base64.getEncoder().encodeToString(data.getBytes("UTF-8")), "UTF-8");
        String signature = hash_hmac(jsonData, privateKey);
        String urlString = detailUrl + "publicKey=" + publicKey + "&signature=" + signature + "&jsondata=" + jsonData;
        URL url = new URL(urlString);
        URLConnection urlConn = url.openConnection();
        urlConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
        InputStream is = urlConn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        is.close();
        String content = sb.toString();
        return content;
    }
    
    public static ArrayList<Playlist> searchPlaylist(String term) {
        ArrayList<Playlist> playlists = new ArrayList<>();
        try {
            String content = getSearchResult(term, 8);
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject)parser.parse(content);
            JSONArray items = (JSONArray)obj.get("Data");
            
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = (JSONObject)items.get(i);                
                String id = (String)item.get("ID");
                String title = (String) item.get("Title");
                String artist = (String) item.get("Artist");
                String genre = (String) item.get("Genre");
                String pictureUrl = (String) item.get("PictureURL");
                long totalListen = 0;
                if (item.get("TotalListen") != null) {
                    totalListen = (long) item.get("TotalListen");
                }
                Playlist p = new Playlist(id, title, artist, genre, pictureUrl, totalListen);
                playlists.add(p);
            }
        } catch (Exception ex) {
            Logger.getLogger(ZingSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return playlists;
    }

    public static ArrayList<Song> getPlaylistDetail(String id) {
        try {
            String content = getDetailResult(id, "playlist");
            return readSongsFromContent(content);
        } catch (Exception ex) {
            Logger.getLogger(ZingSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<Song>();
    }
    
    private static String hash_hmac(String data, String key) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacMD5");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());

            // Convert raw bytes to Hex
            byte[] hexBytes = new Hex().encode(rawHmac);

            //  Covert array of Hex bytes to a String
            return new String(hexBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}