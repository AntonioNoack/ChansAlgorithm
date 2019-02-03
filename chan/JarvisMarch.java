package chan;

import gfx.GfxFlags;
import gfx.DrawState;

import java.util.ArrayList;

import run.GfxRequest;
import basics.ArrayStack;
import basics.Maths;
import basics.Point;

public class JarvisMarch {
	
	private static int circleAccess(int length, int index){
		return (index+length) % length;
	}
	
	private static int jarvisBinarySearch(ArrayList<Point> pts, Point pi, Point displayBeingSpecial /* currently the best one */) throws GfxRequest {
		
		int length = pts.size();
		int max = length-1, min = -max;
		
		GfxRequest.group1 = pts;
		
		int besti = 0, altMax, altMin;
		Point best = pts.get(0), maybe;
		while(max-min > 0){
			
			// if want to left in array = right on screen
			maybe = pts.get(circleAccess(length, besti-1));
			if(besti > min) GfxRequest.gfx(DrawState.JARVIS_BINARY_SEARCH, GfxFlags.CURRENT_HULL, pi, best, maybe, displayBeingSpecial);
			if(besti > min && Maths.istClinksVonAB(pi, maybe, best)){
				
				altMax = max;
				
				max = besti-1;
				besti = (min+max)>>1;
				best = pts.get(circleAccess(length, besti));
				
				GfxRequest.gfx(DrawState.JARVIS_BINARY_SEARCH, GfxFlags.CURRENT_HULL, pi, best, maybe, displayBeingSpecial);
				if(Maths.istClinksVonAB(pi, maybe, best)){
					
					// Ausnahme: maybe > best
					// Zustand wiederherstellen :)
					besti = max;
					best = maybe;
					max = altMax;
				}
				
			} else {

				maybe = pts.get(circleAccess(length, besti+1));
				if(besti < max) GfxRequest.gfx(DrawState.JARVIS_BINARY_SEARCH, GfxFlags.CURRENT_HULL, pi, best, maybe, displayBeingSpecial);
				if(besti < max && Maths.istClinksVonAB(pi, maybe, best)){
					
					altMin = min;
					
					min = besti+1;
					besti = (min+max)>>1;
					best = pts.get(circleAccess(length, besti));
					
					GfxRequest.gfx(DrawState.JARVIS_BINARY_SEARCH, GfxFlags.CURRENT_HULL, pi, best, maybe, displayBeingSpecial);
					if(Maths.istClinksVonAB(pi, maybe, best)){
						
						// Ausnahme: maybe > best
						// Zustand wiederherstellen :)
						besti = min;
						best = maybe;
						min = altMin;
					}
					
				} else {
					return circleAccess(length, besti);
				}
			}
		}
		
		return circleAccess(length, besti);
	}

	public static Point[] computeHull(Point[][] hulls, int hStar) throws GfxRequest {
		
		// nur eine Teilhülle
		if(hulls.length == 1) return hulls[0];
		
		GfxRequest.group1 = GfxRequest.group2 = null;
		
		ArrayList<ArrayList<Point>> S2 = new ArrayList<ArrayList<Point>>();
		
		// suche den tiefsten, linkesten Punkt
		Point minPt = hulls[0][0];
		for(int i=0;i<hulls.length;i++){
			Point pt = hulls[i][0];
			if(pt.y < minPt.y || (pt.y == minPt.y && pt.x < minPt.x)){
				minPt = pt;
			}
		}
		
		GfxRequest.gfx(DrawState.JARVIS_INIT, 0, minPt);
		
		int k = 0;
		for(Point[] group: hulls){
			
			if(group.length > 0 && (group.length > 1 || group[0] != minPt)){
				
				ArrayList<Point> g2 = new ArrayList<>(group.length);
				S2.add(g2);
				
				for(Point pt: group){
					if(pt != minPt){
						g2.add(pt);
						pt.arc = Math.atan2(pt.x-minPt.x, pt.y-minPt.y);
						pt.group = k;
					}
				}
				
				Point first = g2.get(0);
				if(group.length > 1 && first.x < minPt.x){
					// bewege den Eintrag ans Ende
					g2.remove(0);
					g2.add(first);
				}
				
				k++;
			}
		}
		
		ArrayStack<Point> conv = new ArrayStack<Point>(hStar);
		conv.push(minPt);
		
		GfxRequest.group2 = conv;
		
		do {
			
			Point pt1 = conv.peek(0);
			Point nextCandidate = conv.size() == 1 ? null : minPt;
			
			ArrayList<Point> bpts = null;
			int bptsi = 0;
			
			// finde den besten Kandidaten in den Unterhüllen
			for(ArrayList<Point> pts : S2){
				if(pts.size() > 0){
					int pti = jarvisBinarySearch(pts, pt1, nextCandidate == null || nextCandidate == minPt ? pt1 : nextCandidate);
					Point pt = pts.get(pti);
					
					if(nextCandidate != null) GfxRequest.gfx(DrawState.JARVIS_BINARY_SEARCH, GfxFlags.CURRENT_HULL, pt1, pt, nextCandidate, pt1);
					if(nextCandidate == null || Maths.istClinksVonAB(pt1, pt, nextCandidate)){
						nextCandidate = pt;
						bpts = pts;
						bptsi = pti;
					}
				}
			}
			
			// ggf Abbruch
			if(nextCandidate == null || nextCandidate == minPt){
				GfxRequest.group1 = conv;
				GfxRequest.group2 = null;
				GfxRequest.gfx(DrawState.JARVIS_DONE, GfxFlags.CURRENT_HULL);
				return conv.toArray(new Point[conv.size()]);
			}
			
			// Hinzufügen des Punktes der Hülle
			bpts.remove(bptsi);
			conv.push(nextCandidate);

			GfxRequest.gfx(DrawState.JARVIS_MARCH, GfxFlags.CURRENT_HULL, nextCandidate);
			
		} while(conv.size() <= hStar);// Abbruch wenn |conv| > h*
		
		GfxRequest.group1 = conv;
		GfxRequest.group2 = null;
		GfxRequest.gfx(DrawState.JARVIS_DONE, GfxFlags.CURRENT_HULL | GfxFlags.CURRENT_HULL_CLOSED);
		
		return null;
		
	}
	
}

// # ganz schön viele Schleifen für n * log(h)
