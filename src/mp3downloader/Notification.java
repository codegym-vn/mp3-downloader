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
public interface Notification {
    void notify(String message);
    void songDownloaded(String id);
}
