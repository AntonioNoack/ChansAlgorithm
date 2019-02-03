package basics;

public class Point implements Comparable<Point> {
	
	public double x, y;// Position
	public double arc;// für GrahamsScan
	public int group;
	
	public boolean isStillNeeded;// Optimierung in GrahamsScan
	
	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}

	public void resetForGfx() {
		group = 0;
		arc = 0;
	}

	@Override
	public int compareTo(Point b) {
		double dif = arc - b.arc;
		return dif < 0 ? -1 : dif > 0 ? 1 : 0;
	}
	
}
