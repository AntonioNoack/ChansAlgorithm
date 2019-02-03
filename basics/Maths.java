package basics;

public class Maths {
	
	public static double mix(double a, double b, double f){
		return a*(1-f)+f*b;
	}
	
	public static double sq(double x, double y){
		return x*x+y*y;
	}

	public static boolean istClinksVonAB(Point a, Point b, Point c) {
		return (b.x-a.x)*(c.y-a.y) > (c.x-a.x)*(b.y-a.y);
	}
}
