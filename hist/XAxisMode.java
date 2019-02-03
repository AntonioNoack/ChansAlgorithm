package hist;

public enum XAxisMode {
		
	LINEAR("n"){
		@Override public int calculateN(int n) {
			return (n + 2) * 10;
		}
	},
	
	LOGARITHMIC("log(n)"){
		@Override public int calculateN(int n) {
			return (int) (Math.pow(1.1, n) + 3 * n + 10);
		}
	};
	
	String name;
	private XAxisMode(String name) {
		this.name = name;
	}
	
	public abstract int calculateN(int n);
	
}