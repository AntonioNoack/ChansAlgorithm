package run;

import basics.Point;
import gen.CirclelikeGenerator;
import gen.Generator;
import gen.NormalGenerator;
import gen.TriangleGenerator;
import gen.UniformGenerator;
import gfx.GfxFlags;
import gfx.DrawState;

// die Klasse, die sich um Zeichenanfragen kümmert: wird vom Algorithmus mit Zusatzinformationen zum Malen aufgerufen,
// wenn etwas interessantes passiert ist
public class GfxRequest extends Exception {

	private static final long serialVersionUID = 1L;
	
	public static int lastGeneratorIndex = 0, lastN = 20;
	public static Generator[] generators = {
		new NormalGenerator(),
		new UniformGenerator(),
		new TriangleGenerator(),
		new CirclelikeGenerator(0f),
		new CirclelikeGenerator(.9f),
		new CirclelikeGenerator(1f),
	};
	
	public static RunConfig config;
	public static int forcedDrawMode;
	public static int lastState = -1;
	public static int inStageCtr = 0;
	public static int timer = 0;
	
	public static boolean writing = false;
	public static int drawingStage = 0;

	public static Iterable<Point> group1, group2;
	public static Point a, b, c, special;
	
	public static int[] ctr = new int[RunConfig.maxExpectedDrawRecordLength];
	
	public static void reset(){
		
		group1 = group2 = null;
		
		forcedDrawMode = 0;
		lastState = -1;
		inStageCtr = 0;
		timer = 0;
		a = b = c = special = null;
		
		drawingStage = 0;
		
		for(int i=0;i<ctr.length;i++){
			ctr[i] = 0;
		}
		
	}

	// called on every draw possibility, forcing the drawing style
	
	public static void gfx(int state) throws GfxRequest {gfx(state, 0);}
	
	public static void gfx(int state, int flags, Point a) throws GfxRequest {
		GfxRequest.special = a;
		gfx(state, GfxFlags.SPECIAL_POINT | flags);
	}
	
	public static void gfx(int state, int flags, Point a, Point b, Point c) throws GfxRequest {
		GfxRequest.a = a;
		GfxRequest.b = b;
		GfxRequest.c = c;
		gfx(state, GfxFlags.CLOCKWISE_TEST | flags);
	}
	
	public static void gfx(int state, int flags, Point a, Point b, Point c, Point special) throws GfxRequest {
		GfxRequest.special = a;
		GfxRequest.a = a;
		GfxRequest.b = b;
		GfxRequest.c = c;
		gfx(state, GfxFlags.CLOCKWISE_TEST | GfxFlags.SPECIAL_POINT | flags);
	}
	
	public static void gfx(int state, int forcedDrawMode) throws GfxRequest {

		timer++;
		
		state += drawingStage * DrawState.length;
		
		if(writing){
			
			config.lengths[state]++;
			
		} else {
			
			ctr[state]++;
			lastState = state;
			
			if(state == config.stage && ctr[state] >= config.inStageCtr+1){
				
				GfxRequest.forcedDrawMode = forcedDrawMode;
				throw new GfxRequest();
				
			}
		}
		
	}

}
