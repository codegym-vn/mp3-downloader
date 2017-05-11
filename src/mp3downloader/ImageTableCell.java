/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3downloader;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author nhatnk
 */
public class ImageTableCell implements TableCellRenderer{

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {        
        try {
            String url = (String)value;            
            return new JLabel(new ImageIcon(new URL(url)));
        } catch (Exception ex) {
            Logger.getLogger(ImageTableCell.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return new JLabel();
    }
    
}
