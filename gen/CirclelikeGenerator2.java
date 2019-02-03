package gen;

import basics.Point;

public class CirclelikeGenerator2 extends Generator {
	
	@Override
	public Point next(int i, int length) {
		double radius = 1/(.01 + random.nextDouble()) - 1;
		return new Point(Math.sin(i) * radius, Math.cos(i) * radius);
	}

}
