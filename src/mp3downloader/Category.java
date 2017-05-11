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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author nhatnk
 */
public class Category {

    private int id;
    private String name;
    private boolean loaded;
    private ArrayList<SongSubject> subjects;
    public static String AJAX_URL = "http://radio.zing.vn/ajax/radio-list?id=";
    public static String BASE_URL = "http://radio.zing.vn";

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
        this.loaded = false;
        this.subjects = new ArrayList<>();
    }

    public void load() {
        try {
            URL url = new URL(AJAX_URL + this.id);
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = reader.readLine()) != null) {            
                sb.append(line);
                sb.append("\n");
            }
            is.close();
            String content = sb.toString();
            JSONParser parser = new JSONParser();
            
            JSONObject obj = (JSONObject)parser.parse(content);
            JSONArray items = (JSONArray)obj.get("items");
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = (JSONObject)items.get(i);
                String group = (String)item.get("group");
                JSONArray subjects = (JSONArray) item.get("item");
                for (int j = 0; j < subjects.size(); j++) {
                    JSONObject subject = (JSONObject)subjects.get(j);
                    String title = (String)subject.get("title");
                    String path = BASE_URL + (String) subject.get("link");
                    SongSubject ss = new SongSubject(title, path);
                    this.subjects.add(ss);
                }                
            }
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(SongSubject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SongSubject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Category.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public ArrayList<SongSubject> getSubjects() {
        return subjects;
    }    
}
