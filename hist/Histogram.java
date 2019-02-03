package hist;

import run.GfxRequest;
import run.RunConfig;
import chan.ChansAlgorithm;
import basics.Point;
import gen.Generator;
import gfx.DrawState;

// immer von 10 bis 5000?
public class Histogram {

	public static int index = 3;
	public static final int timerIndex = DrawState.length;
	public static boolean needsUpdate = false;
	public static int[] n;
	public static float[] h;// damit es mit runTimes kompatibel ist
	public static float[][] runTimes = new float[DrawState.length + 1][];
	private static RunConfig config = new RunConfig(0x46165313L);
	private static RunConfig stolen = GfxRequest.config;
	
	static class State {
		boolean isRunning;
	}
	
	private static State state = new State();
	
	public static void calculate(int count, XAxisMode mode){
		
		synchronized (GfxRequest.config) {
			
			if(state.isRunning){
				return;
			}
			
			state.isRunning = true;
			
		}
		
		needsUpdate = false;
		
		Point[] allPts = ChansAlgorithm.allPts, pts = ChansAlgorithm.pts;
		Point[][] groups = ChansAlgorithm.groups;
		int drawMode = GfxRequest.forcedDrawMode;
		
		GfxRequest.writing = true;
		GfxRequest.config = config;
		
		Generator generator = GfxRequest.generators[GfxRequest.lastGeneratorIndex];
		
		if(n == null || n.length != count){
			n = new int[count];
			h = new float[count];
		}
		
		if(runTimes[0] == null || runTimes[0].length < count){
			for(int i=0;i<runTimes.length;i++){
				runTimes[i] = new float[count];
			}
		}
		
		int[] ctr = config.lengths;
		
		try {
			for(int i=0;i<count;i++){
				
				GfxRequest.timer = 0;
				
				for(int j=0;j<ctr.length;j++){
					ctr[j] = 0;
				}
				
				int thisN = n[i] = mode.calculateN(i);
				Point[] points = new Point[thisN];
				
				for(int j=0;j<=DrawState.length;j++){
					runTimes[j][i] = 0;
				}
				
				int times = Math.max(1, 2500/(thisN+1));
				float fTimes = 1f/times;
				
				h[i] = 0;
				
				for(int k=0;k<times;k++){
					for(int j=0;j<thisN;j++){
						points[j] = generator.next(j, thisN);
					}
						
					Point[] conv = ChansAlgorithm.computeHull(points);
					h[i] += fTimes * conv.length;
				}
				
				for(int j=0;j<ctr.length;j++){
					runTimes[j % DrawState.length][i] += ctr[j] * fTimes;
				}
				
				runTimes[timerIndex][i] = GfxRequest.timer * fTimes;
				
			}
			
		} catch (GfxRequest e) {
			e.printStackTrace();
		}
		
		
		GfxRequest.writing = false;
		GfxRequest.config = stolen;
		GfxRequest.forcedDrawMode = drawMode;
		ChansAlgorithm.allPts = allPts;
		ChansAlgorithm.pts = pts;
		ChansAlgorithm.groups = groups;
		
		state.isRunning = false;
		
		
	}
	
}
