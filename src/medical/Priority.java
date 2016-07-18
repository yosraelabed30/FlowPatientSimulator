package medical;

public enum Priority {
	P1(1,1),
	P2(2,3),
	P3(3,14),
	P4(4,28); 
	
	private int order;
	private int delay;
	
	Priority(int order, int delay){
		this.setOrder(order);
		this.setDelay(delay);
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
	
}
