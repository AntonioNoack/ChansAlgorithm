package gen;

import basics.Maths;
import basics.Point;

public class CirclelikeGenerator extends Generator {
	
	private double circleness;
	
	public CirclelikeGenerator(double circleness) {
		this.circleness = circleness;
	}

	@Override
	public Point next(int i, int length) {
		double radius = Maths.mix(random.nextDouble(), 1., circleness);
		return new Point(Math.sin(i) * radius, Math.cos(i) * radius);
	}

}
