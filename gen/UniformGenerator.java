package gen;

import basics.Point;

public class UniformGenerator extends Generator {

	@Override
	public Point next(int i, int length) {
		return new Point(random.nextDouble(), random.nextDouble());
	}
	
}
