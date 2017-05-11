/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author nhatnk
 */
public class SongSubject {

    private String name;
    private String link;
    private String path;
    private String downloaded;
    private ArrayList<Song> songs;
    private Notification notification;
    private boolean loaded;
    private boolean crawled;

    public SongSubject(String name, String path) {
        this.name = name;
        this.path = path;
        songs = new ArrayList<>();
    }

    public void load() {
        if (!loaded) {
            try {
                URL url = new URL(path);
                URLConnection conn = url.openConnection();
                conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                is.close();
                String content = sb.toString();
                this.link = getLink(content);
                this.name = getName(content);
            } catch (MalformedURLException ex) {
                Logger.getLogger(SongSubject.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SongSubject.class.getName()).log(Level.SEVERE, null, ex);
            }
            loaded = true;
        }
    }

    private String getLink(String content) {
        Pattern pattern = Pattern.compile("xmlURL: '(.+)'", Pattern.UNIX_LINES);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String title = matcher.group(1);
            return title;
        }
        return null;
    }

    private String getName(String content) {
        Pattern pattern = Pattern.compile("<span class=\"fn-title\">(.+)</span>", Pattern.UNIX_LINES);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String title = matcher.group(1);
            return title;
        }
        return null;
    }

    public void crawl() {
        if (!crawled) {
            try {
                URL url = new URL(this.link);
                URLConnection conn = url.openConnection();
                conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                is.close();
                String content = sb.toString();
                readSongs(content);

            } catch (MalformedURLException ex) {
                Logger.getLogger(SongSubject.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SongSubject.class.getName()).log(Level.SEVERE, null, ex);
            }
            crawled = true;
        }
    }

    private void readSongs(String content) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(content));
            Document doc = db.parse(inputSource);
            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                String id = ((Element) item.getElementsByTagName("id").item(0)).getTextContent();
                String title = ((Element) item.getElementsByTagName("title").item(0)).getTextContent();
                String performer = ((Element) item.getElementsByTagName("performer").item(0)).getTextContent();
                String source = ((Element) item.getElementsByTagName("source").item(0)).getTextContent();
                String thumbnail = ((Element) item.getElementsByTagName("thumbnail").item(0)).getTextContent();
                String type = item.getAttribute("type");
                Song s = new Song(id, title, performer, source, thumbnail, type);
                this.songs.add(s);
            }

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SongSubject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SongSubject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SongSubject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void downloadAll(String path) {
        for (int i = 0; i < this.songs.size(); i++) {
            download(this.songs.get(i), path);
        }
        if (notification != null) {
            notification.notify("Album downloaded: " + this.name);
        }
    }

    private void download(Song song, String path) {
        try {
            if (notification != null) {
                notification.notify("Downloading: " + song.getTitle());
            }
            URL url = new URL(song.getSource());
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(path + File.separator + song.getId()+ "." + song.getType());
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] buf = new byte[4000];
            int lenght = 0;
            while ((lenght = bis.read(buf)) != -1) {
                bos.write(buf, 0, lenght);
            }
            bos.close();
            is.close();
            if (notification != null) {
                notification.notify("Downloaded: " + song.getTitle());
                notification.songDownloaded(song.getId());
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(SongSubject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SongSubject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
