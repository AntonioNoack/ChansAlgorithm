package chan;

import gfx.GfxFlags;
import gfx.DrawState;

import java.util.ArrayList;
import java.util.Arrays;

import run.GfxRequest;
import basics.ArrayIterator;
import basics.Point;

public class ChansAlgorithm {
	
	public static Point[] allPts, pts;
	public static Point[][] groups, convGroups;
	public static ArrayList<Point> currentConv = new ArrayList<Point>();

	public static Point[] computeHull(Point[] points) throws GfxRequest {return computeHull(points, 5);}
	public static Point[] computeHull(Point[] points, int hStar /* geratene Hüllengröße = 5 */) throws GfxRequest {
		
		currentConv.clear();
		
		Point[] pts = ChansAlgorithm.allPts = ChansAlgorithm.pts = points;
		Point[] conv;
		
		convGroups = null;
		GfxRequest.drawingStage = 0;
		
		for(;;){
			
			int n = pts.length;
			int nGroups = (n + hStar - 1) / hStar;// aufrunden
			
			// teile die Punkte in ca. n/hStar kleinere Gruppen für Graham's Scan ein
			Point[][] Sj = groups = new Point[nGroups][];
			for(int i=0,j=0;i<n;){
				// höchstens m Punkte in Sj
				Sj[j++] = Arrays.copyOfRange(pts, i, Math.min(i+hStar, n));
				i += hStar;
			}
			
			GfxRequest.group1 = null;
			GfxRequest.group2 = null;
			GfxRequest.gfx(DrawState.GENERATE_GROUPS);
			
			// berechne für alle Untergruppen die konvexe Hülle
			Point[][] convSj = convGroups = new Point[groups.length][];
			for(int i=0;i<groups.length;i++){
				convSj[i] = GrahamsScan.computeHull(groups[i]);
			}
			
			conv = JarvisMarch.computeHull(convSj, hStar);
			if(conv != null /* erfolgreich */ || hStar >= n /* Fehlerfall */){
				break;
			}
		
			GfxRequest.gfx(DrawState.CHANS_ALGORITHM);
			GfxRequest.drawingStage ++;
			
			// Superexponentielle Suche
			hStar = Math.min(hStar*hStar, n);
			
			// Herausfiltern von nicht mehr benötigten Punkten
			int neededSpace = 0;
			for(Point pt: pts){
				if(pt.isStillNeeded) neededSpace++;
			}
			
			Point[] newPts = new Point[neededSpace];
			for(Point pt: pts){
				if(pt.isStillNeeded){
					newPts[--neededSpace] = pt;
				}
			}
			
			pts = ChansAlgorithm.pts = newPts;
			
		}
		
		GfxRequest.group1 = GfxRequest.group2 = new ArrayIterator<Point>(conv);
		GfxRequest.gfx(DrawState.DONE, GfxFlags.CURRENT_HULL | GfxFlags.CURRENT_HULL_CLOSED);
		
		return conv;
	}
	
}
