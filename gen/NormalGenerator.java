package gen;

import basics.Point;

public class NormalGenerator extends Generator {

	@Override
	public Point next(int i, int length) {
		return new Point(random.nextGaussian(), random.nextGaussian());
	}
	
}
