package gfx;

public class GfxFlags {
	
	private static int ctr = 0;
	public static final int
		CLOCKWISE_TEST = 1 << ctr++,
		ARCS = 1 << ctr++,
		SPECIAL_POINT = 1 << ctr++,
		CURRENT_HULL = 1 << ctr++,
		CURRENT_HULL_CLOSED = 1 << ctr++;
	
}
