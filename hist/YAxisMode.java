package hist;

public enum YAxisMode {
	
	CONST("1"),
	N("n"),
	NLOGN("n*log(n)"),
	NLOGH2("n*log²(h)"),
	NLOGH("n*log(h)"),
	N2("n²");
	
	public String name;
	private YAxisMode(String name) {
		this.name = name;
	}
	
}