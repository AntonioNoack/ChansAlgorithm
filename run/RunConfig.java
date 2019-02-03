package run;

import gfx.DrawState;

public class RunConfig {
	
	public static final int maxExpectedDrawRecordLength = 256;
	
	public int generatorIndex = 0, n = 20, h = -1;
	public int stage = DrawState.GENERATE_GROUPS;
	public int inStageCtr = 0;
	public final long seed;
	
	// Anzahlen der Schritte zum Durchwechseln
	public int[] lengths = new int[maxExpectedDrawRecordLength];
	
	public RunConfig(long seed){
		this.seed = seed;
	}
}
