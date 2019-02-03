package chan;

import gfx.GfxFlags;
import gfx.DrawState;

import java.util.ArrayList;
import java.util.Collections;

import run.GfxRequest;
import basics.ArrayIterator;
import basics.ArrayStack;
import basics.Maths;
import basics.Point;

public class GrahamsScan {
	
	public static Point[] computeHull(Point[] pts) throws GfxRequest {
		
		if(pts.length < 4){
			for(Point pt : pts){
				pt.isStillNeeded = true;
			};return pts;
		}
		
		GfxRequest.group2 = null;
		
		ArrayStack<Point> conv = new ArrayStack<Point>(pts.length);
		
		// suche den Punkt mit der kleinsten y-Koordinate
		// bei mehreren den mit der kleinsten x-Koordinate
		double minY = pts[0].y;
		Point minPt = pts[0];
		int minIndex = 0;
		for(int i=1;i<pts.length;i++){
			Point pt = pts[i];
			if(pt.y < minY){
				minPt = pt;
				minY = pt.y;
				minIndex = i;
			} else if(pt.y == minY && pt.x < minPt.x){
				minPt = pt;
				minIndex = i;
			}
		}
		
		GfxRequest.group1 = new ArrayIterator<Point>(pts);
		GfxRequest.gfx(DrawState.GRAHAM_FIND_MIN, 0, minPt);
		
		// entferne minPt
		ArrayList<Point> andere = new ArrayList<Point>(pts.length-1);
		for(int i=0;i<pts.length;i++){
			if(i != minIndex){
				andere.add(pts[i]);
			}
		}
		
		GfxRequest.group1 = andere;
		
		// berechne den Winkel zu minPt und x-Achse
		for(Point pt : andere){
			pt.arc = Math.atan2(pt.y-minPt.y, pt.x-minPt.x);
			pt.isStillNeeded = false;
		}
		
		GfxRequest.gfx(DrawState.GRAHAM_SORT_ARCS, GfxFlags.ARCS | GfxFlags.CURRENT_HULL, minPt);

		// sortiere die Punkte nach Winkel zwischen minPt und x-Achse
		Collections.sort(andere);
		
		// entferne bei kollinearen Punkten den mittleren
		for(int i=1,l=andere.size();i<l;i++){
			Point a = andere.get(i-1);
			Point b = andere.get(i);
			if(a.arc == b.arc){
				// der Punkt näher an p0 wird verworfen
				if(Maths.sq(a.x-minPt.x, a.y-minPt.y) > Maths.sq(b.x-minPt.x, b.y-minPt.y)){
					// behalte a
					andere.remove(i);
				} else {
					// behalte b
					andere.remove(i-1);
				};i--;
			}
		}
		
		// der Stack von Punkten der Hülle
		conv.push(minPt);
		conv.push(andere.get(0));
		
		GfxRequest.group2 = conv;
		
		for(int i=1,l=andere.size();i<l;){
			int stackLength = conv.size();
			Point pt1 = conv.peek(0);
			Point pt2 = conv.peek(1);
			Point compare = andere.get(i);
			
			GfxRequest.gfx(DrawState.GRAHAM_SCAN, GfxFlags.ARCS, pt2, pt1, compare, minPt);
			if(stackLength == 2 || Maths.istClinksVonAB(pt2, pt1, compare)){
				conv.push(compare);
				i++;
			} else {
				conv.pop();
			}
		}
		
		Point[] ret = conv.toArray(new Point[conv.size()]);
		for(Point pt : ret){
			pt.isStillNeeded = true;
		}
		
		GfxRequest.gfx(DrawState.GRAHAM_DONE, GfxFlags.CURRENT_HULL);
		GfxRequest.group1 = null;
		GfxRequest.group2 = null;
		
		return ret;
		
	}
	
}
