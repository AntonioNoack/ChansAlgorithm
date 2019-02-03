package gfx;

import hist.Histogram;
import hist.XAxisMode;
import hist.YAxisMode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import run.GfxRequest;
import chan.ChansAlgorithm;
import basics.Maths;
import basics.Point;

public abstract class GraphFrame extends JLabel {
	
	// die Positionen und Breiten der Knöpfe
	float smallButtonDX = .15f, smallButtonDY = .15f;
	float sp = .02f, dy0 = .20f, dy1 = .12f, dy2 = .15f, dy3 = .15f, dy4 = .15f;
	float y0 = sp, y1 = y0 + dy0, y2 = y1 + dy1 + sp, y3 = y2 + dy2, y4 = y3 + dy3;
	float dyH = .15f;
	
	int ptSize = 8, ptSizef2 = ptSize/2;
	int bigPtSize = ptSize * 3/2, bigPtSizef2 = bigPtSize/2;
	
	float lineStrength = ptSize / 4f;
	
	
	
	private boolean showHistogram = false;
	
	private XAxisMode xMode = XAxisMode.LINEAR;
	private YAxisMode yMode = YAxisMode.CONST;
	
	int[] histIndices = {DrawState.GRAHAM_SCAN, DrawState.JARVIS_BINARY_SEARCH, DrawState.CHANS_ALGORITHM};
	
	private static final long serialVersionUID = 1L;

	public abstract void nextStep();
	public abstract void nextStage();
	public abstract void nextSeed();
	
	public abstract void prevStep();
	public abstract void prevStage();
	public abstract void prevSeed();
	
	public abstract void changedGenerator();
	
	public GraphFrame(){
		// Mouselistener für die Click-Events
		addMouseListener(new MouseListener() {
			
			@Override public void mouseReleased(MouseEvent arg0) {}
			@Override public void mousePressed(MouseEvent arg0) {}
			@Override public void mouseExited(MouseEvent arg0) {}
			@Override public void mouseEntered(MouseEvent arg0) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				int w = getWidth();
				float x0 = w - menueWidth*(1f + sp);
				float fx = 1f * (x-x0)/menueWidth, fx2 = (x-sp)/menueWidth;
				float fy = 1f * y / menueWidth;
				if(fy < smallButtonDX){
					int buttonIndex = (int) (fx2 / smallButtonDX);
					switch(buttonIndex){
					case 0:
						showHistogram = !showHistogram;
						Histogram.needsUpdate = true;
						changedGenerator();
						break;
					case 1:
						if(showHistogram){
							histMenuEntry = (histMenuEntry + (e.getButton() == MouseEvent.BUTTON1 ? 1 : 3)) % 4;
							repaint();
						} else {
							showMenu = !showMenu;
							repaint();
						};break;
					case 2:
						if(!showHistogram){
							if(black.getBlue() < 40){
								darkThemed();
							} else {
								lightThemed();
							}
						}
					}
				}

				// menu to 1/3 on the right :)
				// buttons for all display modes, buttons for switching :)
				if(fx > 0){
					if(showHistogram){
						int index;
						switch(histMenuEntry){
						case 0:// was dargestellt werden soll
							index = (int)((fy-sp)/dyH) * 2 + (fx > .5f ? 1 : 0);
							if(index > -1 && index < 2 + histIndices.length){
								if(index == 0) Histogram.index = Histogram.timerIndex;
								else if(index == 1) Histogram.index = -1;
								else Histogram.index = histIndices[index-2];
								repaint();
							};break;
						case 1:// x-Achse
							if(fy > sp && fy <= sp + dyH){
								if((xMode == XAxisMode.LINEAR) != fx < .5f){
									xMode = fx < .5f ? XAxisMode.LINEAR : XAxisMode.LOGARITHMIC;
									Histogram.needsUpdate = true;
									repaint();
								}
							};break;
						case 2:// y-Achse
							index = (int)((fy-sp)/dyH) * 2 + (fx > .5f ? 1 : 0);
							if(index > -1 && index < YAxisMode.values().length){
								yMode = YAxisMode.values()[index];
								repaint();
							};break;
						}
					} else {
						if(fy >= y0 && fy < y0 + dy0){
							if(fx < .5f){
								if(fx < .25f){
									prevStage();
								} else {
									prevStep();
								}
							} else {
								if(fx < .75f){
									nextStep();
								} else {
									nextStage();
								}
							}
						} else if(fy >= y1 && fy < y1 + dy1){
							if(fx < .5f){
								prevSeed();
							} else {
								nextSeed();
							}
						} else if(fy >= y2 && fy < y2 + dy2){
							if(fx < .333f){
								changeToGenerator(0);
							} else if(fx < .667f){
								changeToGenerator(1);
							} else {
								changeToGenerator(2);
							}
						} else if(fy >= y3 && fy < y3 + dy3){
							if(fx < .333f){
								changeToGenerator(3);
							} else if(fx < .667f){
								changeToGenerator(4);
							} else {
								changeToGenerator(5);
							}
						} else if(fy >= y4 && fy < y4 + dy4){
							int newN = frageN();
							if(newN > 0 && newN <= 100000){// alles andere ist unvernünftig...
								GfxRequest.config.n = newN;
								changedGenerator();
							}
						}
					}
				}
			}
		});
		
		// MouseMotion für die Hovering-Changing-Events,
		// auch wenn ich sie nur direkt und ineffizient abfange
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override public void mouseMoved(MouseEvent e) {
				mx = e.getX();
				my = e.getY();
				repaint();
			}
			
			@Override public void mouseDragged(MouseEvent arg0) {}
		});
	}
	
	// Generator wechseln
	private void changeToGenerator(int i){
		if(GfxRequest.config.generatorIndex != i){
			GfxRequest.config.generatorIndex = i;
			changedGenerator();
		}
	}
	
	private int frageN(){
		try {
			String s = (String) JOptionPane.showInputDialog(null, null, "Definiere n!", JOptionPane.PLAIN_MESSAGE/*, IconFactory.icon*/, null, null, null);
			if(s==null) return -1;
			return Integer.parseInt(s);
		} catch(NumberFormatException e){}
		return -1;
	}
	
	private int mx, my;
	
	private int menueWidth;
	
	private boolean showMenu;
	
	public void calculateBounds(Point[] points){
		
		double 
			minimumX = Double.POSITIVE_INFINITY,
			minimumY = Double.POSITIVE_INFINITY,
			maximumX = - minimumX,
			maximumY = - minimumY;
		
		for(Point p : points){
			if(p.x < minimumX) minimumX = p.x;
			if(p.y < minimumY) minimumY = p.y;
			if(p.x > maximumX) maximumX = p.x;
			if(p.y > maximumY) maximumY = p.y;
		}
		
		midX = (maximumX + minimumX)/2;
		midY = (maximumY + minimumY)/2;
		
		double padding = ptSize * 3;
		
		scale = Math.min((width - 2*padding) / (maximumX - minimumX + .01), (height - 2*padding) / (maximumY - minimumY + .01));
		
	}
	
	private double width, height, scale, midX, midY;
	
	public double screenX(double pointX){
		return width/2 + (pointX - midX) * scale;
	}
	
	public double screenY(double pointY){
		return height/2 - (pointY - midY) * scale;
	}

	private Polygon polygon = new Polygon();
	private FontMetrics menueFontMetrics, menueSmallFontMetrics;
	
	private Color
	
			ppColor = c(0x0063c6),
			pColor = c(0x4ba5ff),
			nnColor = c(0x8ec600),
			nColor = c(0xc4ff2d),
			
			white,
			whiteFog,
			
			clockwiseTestColor,
			
			lightGray, darkGray,
			
			black, tBlack, ttBlack;// transparent (transparent) black
	
	private float hsvValue;
	
	private Stroke stroke = new BasicStroke(lineStrength);
	private Stroke stroke2 = new BasicStroke(2f);

	public void lightThemed(){
		
		white = Color.WHITE;
		whiteFog = c(-1, 120);
		lightGray = Color.LIGHT_GRAY;
		darkGray = Color.DARK_GRAY;
		clockwiseTestColor = Color.BLUE;
		black = Color.BLACK;
		tBlack = c(0, 150);
		ttBlack = new Color(0,0,0,75);
		
		hsvValue = .5f;
		
		repaint();
		
	}
	
	public void darkThemed(){
		
		white = Color.BLACK;
		whiteFog = c(0, 120);
		lightGray = Color.DARK_GRAY;
		darkGray = Color.LIGHT_GRAY;
		clockwiseTestColor = Color.GREEN;
		black = Color.WHITE;
		tBlack = c(-1, 150);
		ttBlack = c(-1, 75);
		
		hsvValue = 1f;
		
		repaint();
		
	}
	
	private void calcHistdata(){
		Histogram.calculate(100, xMode);
		repaint();
	}
	
	private double histX(int n){
		switch(xMode){
		case LINEAR:
			return n;
		case LOGARITHMIC:
			return n < 2 ? 0 : Math.log(n);
		default:
			return n;
		}
	}
	
	private double histY(double t, double n, double h){
		if(n < 1) return 0;
		switch(yMode){
		case CONST:
			return t;
		case N:
			return t/n;
		case NLOGN:
			return t/(n*Math.log(n));
		case NLOGH2:
			h=Math.log(h);
			return t/(n*h*h);
		case NLOGH:
			return t/(n*Math.log(h));
		case N2:
			return t/(n*n);
		default:
			return t/n;
		}
	}
	
	private int histMenuEntry;
	
	// die Haupt-Mal-Funktion
	public void paint(Graphics g) {
		
		int iw = getWidth();
		int ih = getHeight();
		
		Graphics2D g2D = g instanceof Graphics2D ? (Graphics2D) g : null;
		if(g2D != null) g2D.setStroke(stroke);
		
		width = iw;
		height = ih;
		
		menueWidth = (int) Math.min(400, width/3);

		Font menueFont = getFont().deriveFont(menueWidth * .1f);
		Font menueSmallFont = getFont().deriveFont(menueWidth * .07f);
		Font infoFont = getFont().deriveFont(menueWidth * .05f);
		menueFontMetrics = g.getFontMetrics(menueFont);
		menueSmallFontMetrics = g.getFontMetrics(menueSmallFont);
		
		g.setColor(white);
		g.fillRect(0, 0, iw, ih);
		
		g.setColor(darkGray);
		
		if(showHistogram){
			
			float[] runTimes = Histogram.index < 0 ? Histogram.h : Histogram.runTimes[Histogram.index], h = Histogram.h;
			int[] n = Histogram.n;
			
			if(!Histogram.needsUpdate && runTimes != null && n != null && h != null && runTimes.length >= n.length && n.length == h.length && n.length > 1){
				
				int l = n.length;
				double minX = histX(n[0]-1), maxX = histX(n[l-1]);		
				double maxY = 0;
				for(int i=0;i<l;i++){
					double theY = histY(runTimes[i], n[i], h[i]);
					if(theY > maxY && Double.isFinite(theY)) maxY = theY;
				}
				
				if(maxY > 0){
					
					int y0 = (int) (height);
					double scaleY = height * .85 / maxY;
					
					int x0 = 0;
					double scaleX = width / (maxX - minX);
					
					int lx = 0;
					
					for(int i=0;i<l;i++){
						
						int tx = (int) (scaleX * (histX(n[i]) - minX));
						if(tx > lx){

							int y = (int) (scaleY * histY(runTimes[i], n[i], h[i]));
							if(Double.isFinite(y)){
								g.fillRect(x0 + lx, y0 - y, tx-lx, y + 1);
							}
							lx = tx;
							
						}
					}
					
					g.setFont(menueSmallFont);
					float y = (float) height - menueFont.getSize();
					Button.drawText(g, menueSmallFontMetrics, maxY < 1 ? String.format("%.2f", maxY) : maxY < 10 ? String.format("%.1f", maxY) : (int)(maxY+.5)+"", lightGray, .05f * (float) width, (float) height * .15f, 1f, 1f, 0, 1f);
					Button.drawText(g, menueSmallFontMetrics, n[0]+"", lightGray, .05f * (float) width, y, 1f, 1f, 0, 1f);
					Button.drawText(g, menueSmallFontMetrics, n[l/2]+"", lightGray, .5f * (float) width, y, 1f, 1f, 0, 1f);
					Button.drawText(g, menueSmallFontMetrics, n[l-1]+"", lightGray, .95f * (float) width, y, 1f, 1f, 0, 1f);
					
				}
				
				
			} else calcHistdata();
			
		} else if(ChansAlgorithm.allPts != null){
			
			g.setFont(infoFont);
			g.drawString(DrawState.getName(GfxRequest.config.stage), 10, ih - 10);
			
			if(g2D != null){
				g2D.setRenderingHints(new RenderingHints(
			    		RenderingHints.KEY_TEXT_ANTIALIASING,
			    		RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
			    g2D.setRenderingHints(new RenderingHints(
			    		RenderingHints.KEY_ANTIALIASING,
			    		RenderingHints.VALUE_ANTIALIAS_ON));
			}
			
			calculateBounds(ChansAlgorithm.allPts);
			
			Point special = flag(GfxRequest.forcedDrawMode, GfxFlags.SPECIAL_POINT) ? GfxRequest.special : null;
			
			// ctx.clearRect(0,0,canvas.width,canvas.height);
			
			if(ChansAlgorithm.allPts != ChansAlgorithm.pts){
				g.setColor(darkGray);
				for(Point pt : ChansAlgorithm.allPts){
					g.fillRect((int) screenX(pt.x) - 2, (int) screenY(pt.y) - 2, 4, 4);
				}
			}
			
			Point[][] groups = ChansAlgorithm.groups;
			if(groups != null){
				for(int i=0;i<groups.length;i++){
					
					Point[] group = groups[i];
					float hue = (1f * i / ChansAlgorithm.groups.length);
					g.setColor(fromHSV(hue, 1f, hsvValue));// "hsl("+hue+", 100%, 50%)";
					
					for(Point pt : group){
						if(pt == special){
							g.fillRect((int) screenX(pt.x) - bigPtSizef2, (int) screenY(pt.y) - bigPtSizef2, bigPtSize, bigPtSize);
						} else {
							g.fillRect((int) screenX(pt.x) - ptSizef2, (int) screenY(pt.y) - ptSizef2, ptSize, ptSize);
						}
					}
					
				}
			}
			
			Iterable<Point> group1 = GfxRequest.group1, group2 = GfxRequest.group2;
			if(flag(GfxRequest.forcedDrawMode, GfxFlags.ARCS) && group1 != null){
				g.setColor(black);
				for(Point pt : group1){
					g.drawString(Math.round(pt.arc * 180 / Math.PI)+"°", (int) screenX(pt.x) + ptSizef2, (int) screenY(pt.y) - ptSizef2);
				}
			}
			
			
			// groups should be drawn
			if(group1 != null){
				
				g.setColor(group2 != null ? tBlack : black);
				
				linePolygon(g, group1.iterator(), flag(GfxRequest.forcedDrawMode, GfxFlags.CURRENT_HULL_CLOSED));
				
				if(group2 != null){
					polygon.reset();
					Iterator<Point> g2 = group2.iterator();
					if(g2.hasNext()){
						while(g2.hasNext()){
							Point pt = g2.next();
							int cx = (int) screenX(pt.x), cy = (int) screenY(pt.y);
							polygon.addPoint(cx, cy);
						}
					}
					if(polygon.npoints > 2){
						g.fillPolygon(polygon);
					} else {
						linePolygon(g, group2.iterator(), false);
					}
				}
			}
			
			Point[][] convGroups = ChansAlgorithm.convGroups;
			if(convGroups != null){
				for(int i=0;i<convGroups.length;i++){
					Point[] group = convGroups[i];
					if(group != null){
						
						float hue = (1f * i / ChansAlgorithm.groups.length);
						g.setColor(fromHSV(hue, 1f, hsvValue));// "hsl("+hue+", 100%, 50%)";
						
						// todo draw all groups :)
						polygon.reset();
						for(Point pt : group){
							polygon.addPoint((int) screenX(pt.x), (int) screenY(pt.y));
						}
						g.drawPolygon(polygon);
						
					}
				}
			}
			
			if(flag(GfxRequest.forcedDrawMode, GfxFlags.CLOCKWISE_TEST)){
				Point a = GfxRequest.a, b = GfxRequest.b, c = GfxRequest.c;
				if(a != null && b != null && c != null){
					
					g.setColor(whiteFog);
					g.fillRect(0, 0, iw, ih);
					
					boolean isLeft = Maths.istClinksVonAB(a, b, c);
					int
					ax = (int) screenX(a.x),
					ay = (int) screenY(a.y),
					bx = (int) screenX(b.x),
					by = (int) screenY(b.y),
					cx = (int) screenX(c.x),
					cy = (int) screenY(c.y),
					size = (int) Math.sqrt(Math.min(Maths.sq(bx-ax, by-ay), Maths.sq(cx-ax, cy-ay)));
					double angle1 = Math.atan2(c.y-a.y, c.x-a.x);
					double angle2 = Math.atan2(b.y-a.y, b.x-a.x);
					if(!isLeft) size /= 2;
					g.setColor(isLeft ? tBlack : ttBlack);
					polygon.reset();
					drawArc(polygon, ax, ay, Math.min(size, (int)(menueWidth * .3)), angle2, angle1-angle2);
					g.fillPolygon(polygon);
					g.setColor(black);
					double len = Math.sqrt(Maths.sq(ax-bx, ay-by));
					int mulLength = (int)(Math.min(len * .5, menueWidth * .1) * 65536 / len);
					int dx = (mulLength*(bx-ax)) >> 16, dy = (mulLength*(by-ay)) >> 16;
					g.drawLine(ax-dx, ay-dy, bx, by);
					g.drawLine(ax-dy, ay+dx, ax+dy, ay-dx);
					g.setColor(clockwiseTestColor);
					g.drawLine(ax, ay, cx, cy);
				}
			}
		}
		
		if(g2D != null) g2D.setStroke(stroke2);
		
		g.setFont(menueFont);
		float x0 = iw - menueWidth * (1f + sp);
		float dx = menueWidth;
		
		Button.draw(g, menueFontMetrics, "H", pColor, ppColor,
				sp, sp,
				smallButtonDX, smallButtonDY,
				0, dx, mx, my, showHistogram);
		
		if(showHistogram){
			
			Button.draw(g, menueFontMetrics, histMenuEntry == 0 ? "/" : histMenuEntry == 1 ? "X" : histMenuEntry == 2 ? "Y" : "", pColor,
				sp + smallButtonDX, sp,
				smallButtonDX, smallButtonDY,
				0, dx, mx, my);
			
			// zeige die Optionen: Einstellungsmöglichkeiten für die x-Achse, y-Achse, Auswahl
			int i;
			switch(histMenuEntry){
				case 0:// Auswahl, was gemalt werden soll
					i = 2;
					g.setFont(menueSmallFont);
					Button.draw(g, menueSmallFontMetrics, "Gesamt", pColor, ppColor, .0f, y0, .5f, dyH, x0, dx, mx, my, Histogram.index == Histogram.timerIndex);
					Button.draw(g, menueSmallFontMetrics, "h", pColor, ppColor, .5f, y0, .5f, dyH, x0, dx, mx, my, Histogram.index == -1);
					for(int state: histIndices){
						Button.draw(g, menueSmallFontMetrics, DrawState.getShortName(state), pColor, ppColor, (i%2) == 0 ? 0f : .5f, y0 + (i/2) * dyH, .5f, dyH, x0, dx, mx, my, Histogram.index == state);
						i++;
					};break;
				case 1:// x-Achse
					g.setFont(menueSmallFont);
					Button.draw(g, menueSmallFontMetrics, "lin", pColor, ppColor, .0f, y0, .5f, dyH, x0, dx, mx, my, xMode == XAxisMode.LINEAR);
					Button.draw(g, menueSmallFontMetrics, "exp", pColor, ppColor, .5f, y0, .5f, dyH, x0, dx, mx, my, xMode == XAxisMode.LOGARITHMIC);
					break;
				case 2:// y-Achse
					i = 0;
					g.setFont(menueSmallFont);
					for(YAxisMode mode : YAxisMode.values()){
						Button.draw(g, menueSmallFontMetrics, mode.name, pColor, ppColor, (i%2) == 0 ? 0f : .5f, y0 + (i/2) * dyH, .5f, dyH, x0, dx, mx, my, mode == yMode);
						i++;
					};break;
			}
			
		} else {
			
			Button.draw(g, menueFontMetrics, "M", pColor, ppColor,
					sp + smallButtonDX, sp,
					smallButtonDX, smallButtonDY,
					0, dx, mx, my, showMenu);
			
			Button.draw(g, menueFontMetrics, black.getBlue() < 40 ? "D" : "L", nnColor,
					sp + smallButtonDX * 2, sp,
					smallButtonDY, smallButtonDY,
					0, dx, mx, my);
			
			if(showMenu){
				
				Button.draw(g, menueFontMetrics, "<<", ppColor, .00f, y0, .25f, dy0, x0, dx, mx, my);
				Button.draw(g, menueFontMetrics, "<",   pColor, .25f, y0, .25f, dy0, x0, dx, mx, my);
				Button.draw(g, menueFontMetrics, ">",   nColor, .50f, y0, .25f, dy0, x0, dx, mx, my);
				Button.draw(g, menueFontMetrics, ">>", nnColor, .75f, y0, .25f, dy0, x0, dx, mx, my);
				
				Button.draw(g, menueFontMetrics, "<<<", ppColor, .00f, y1, .50f, dy1, x0, dx, mx, my);
				Button.draw(g, menueFontMetrics, ">>>", nnColor, .50f, y1, .50f, dy1, x0, dx, mx, my);
				
				g.setFont(menueSmallFont);
				Button.draw(g, menueSmallFontMetrics, "normal",  nnColor, nColor, .000f, y2, .334f, dy2, x0, dx, mx, my, GfxRequest.config.generatorIndex == 0);
				Button.draw(g, menueSmallFontMetrics, "uniform", nnColor, nColor, .333f, y2, .333f, dy2, x0, dx, mx, my, GfxRequest.config.generatorIndex == 1);
				Button.draw(g, menueSmallFontMetrics, "trian.", nnColor, nColor, .666f, y2, .333f, dy2, x0, dx, mx, my, GfxRequest.config.generatorIndex == 2);
				Button.draw(g, menueSmallFontMetrics, "cir0%", nnColor, nColor, .000f, y3, .334f, dy3, x0, dx, mx, my, GfxRequest.config.generatorIndex == 3);
				Button.draw(g, menueSmallFontMetrics, "cir99%",  nnColor, nColor, .333f, y3, .333f, dy3, x0, dx, mx, my, GfxRequest.config.generatorIndex == 4);
				Button.draw(g, menueSmallFontMetrics, "cir100%",  nnColor, nColor, .667f, y3, .333f, dy3, x0, dx, mx, my, GfxRequest.config.generatorIndex == 5);
				g.setFont(menueFont);
				
				Button.draw(g, menueFontMetrics, "n: "+GfxRequest.config.n+", h: "+GfxRequest.config.h, pColor, 0f, y4, 1f, dy4, x0, dx, mx, my);
				
			}
		}
	}
	
	private void linePolygon(Graphics g, Iterator<Point> g1, boolean close){
		if(g1.hasNext()){
			Point pt = g1.next();
			if(g1.hasNext()){
				int lx = (int) screenX(pt.x), fx = lx, ly = (int) screenY(pt.y), fy = ly;
				while(g1.hasNext()){
					pt = g1.next();
					int cx = (int) screenX(pt.x), cy = (int) screenY(pt.y);
					g.drawLine(lx, ly, cx, cy);
					lx = cx;
					ly = cy;
				}
				if(close){
					g.drawLine(lx, ly, fx, fy);
				}
			}
		}
	}
	
	// ist der Flag gesetzt?
	private static boolean flag(int a, int flag){
		return (a & flag) == flag;
	}
	
	// weil die Standardfunktion nur Grad in ganzen Zahlen annimmt, was zu wenig für unseren Bedarf ist: rechnet einfach das gleiche mit Fließkommazahlen
	private static void drawArc(Polygon polygon, int x0, int y0, int size, double angle, double deltaAngle){
		
		if(deltaAngle > Math.PI){
			deltaAngle -= 2*Math.PI;
		} else if(deltaAngle < -Math.PI){
			deltaAngle += 2*Math.PI;
		}
		
		int steps = Math.max(3, (int) Math.abs(deltaAngle * size));
		double fstep = deltaAngle / (steps-1);
		polygon.addPoint(x0, y0);
		
		for(int i=0;i<steps;i++){
			polygon.addPoint(x0 + (int)(size * Math.cos(angle + fstep * i)), y0 - (int)(size * Math.sin(angle + fstep * i)));
		}
		
	}
	
	// https://stackoverflow.com/questions/7896280/converting-from-hsv-hsb-in-java-to-rgb-without-using-java-awt-color-disallowe
	public static Color fromHSV(float hue, float saturation, float value) {
	
	    int h = (int)(hue * 6);
	    float f = hue * 6 - h;
	    float p = value * (1 - saturation);
	    float q = value * (1 - f * saturation);
	    float t = value * (1 - (1 - f) * saturation);

	    switch (h % 6) {
		    case 0: return new Color(value, t, p);
		    case 1: return new Color(q, value, p);
		    case 2: return new Color(p, value, t);
		    case 3: return new Color(p, q, value);
		    case 4: return new Color(t, p, value);
		    default: return new Color(value, p, q);
	    }
	}
	
	// Farbe from Hex
	public static Color c(int hex){
		return new Color((hex >> 16) & 255 , (hex >> 8) & 255, hex & 255);
	}
	
	// Farbe from Hex und alpha
	public static Color c(int hex, int alpha){
		return new Color((hex >> 16) & 255 , (hex >> 8) & 255, hex & 255, alpha);
	}
}
