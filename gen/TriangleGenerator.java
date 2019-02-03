package gen;

import basics.Point;

public class TriangleGenerator extends Generator {
	
	@Override
	public Point next(int i, int length) {
		double a, b;
		do {
			a = random.nextDouble();
			b = random.nextDouble();
		} while(a > b);
		return new Point(a * .37 + b * .37, b * .5 - a * .5);
	}

}
