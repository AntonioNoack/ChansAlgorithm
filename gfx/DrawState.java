package gfx;

public class DrawState {
	
	private static int ctr;
	public static final int
	
	GENERATE_GROUPS = ctr++,
	
	GRAHAM_FIND_MIN = ctr++,
	GRAHAM_SORT_ARCS = ctr++,
	GRAHAM_SCAN = ctr++,
	GRAHAM_DONE = ctr++,
	
	JARVIS_INIT = ctr++,
	JARVIS_BINARY_SEARCH = ctr++,
	JARVIS_MARCH = ctr++,
	
	JARVIS_DONE = ctr++,
	
	CHANS_ALGORITHM = ctr++,
	
	DONE = ctr++
	
	;
	
	public static String getName(int state){
		return state < 0 ? "" : names[state % length];
	}
	
	public static String getShortName(int state){
		return state < 0 ? "" : shortNames[state % length];
	}
	
	private static final String[] names = {
		"Chan: Generate Groups",
		"Graham: Find Minimum",
		"Graham: Sort Angles",
		"Graham's Scan",
		"Graham: Done",
		"Jarvis M.: Init",
		"Jarvis M.: Binary Search",
		"Jarvis March",
		"Jarvis M.: Done",
		"Chan's Algorithm",
		"Done"
	}, shortNames = {
		"GenGrps",
		"GFindMin",
		"GSortAng",
		"G'sScan",
		"G.Done",
		"J.Init",
		"JBinSearch",
		"JMarch",
		"J.Done",
		"Chan'sA.",
		"Done"
	};
	
	public static final int length = names.length;
	
}
