package gen;

import java.util.Random;

import basics.Point;

public abstract class Generator {
	
	public Random random = new Random();
	
	public Point[] generate(int length){
		Point[] points = new Point[length];
		for(int i=0;i<length;i++){
			points[i] = next(i, length);
		}
		return points;
	}
	
	public abstract Point next(int i, int length);
	
	public void reset(long seed){
		random.setSeed(seed);
	}
	
}
