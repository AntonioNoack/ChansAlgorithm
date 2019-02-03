package gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class IconFactory {// just for fun, because we can ;)
	
	public static Icon icon;
	
	public static void setIcon(JFrame j){
		try {
			
			int w = 128, h = w;
			BufferedImage img = new BufferedImage(w, h, 2);
			Graphics2D g2 = (Graphics2D) img.getGraphics();
			
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, w, h);
			g2.setColor(Color.RED);
			int x = w*3/7;
			g2.fillRect(w/6, h/6, x, x);
			g2.setColor(Color.GREEN);
			g2.fillRect(w/2, h/2, x, x);
			
			g2.dispose();
			j.setIconImage(img);
			
			icon = new ImageIcon(img);
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
