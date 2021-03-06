package medical;

import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.randvar.UniformGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;

public enum Priority {
	P1(1,1),
	P2(2,3),
	P3(3,14),
	P4(4,28); 
	
	private int order;
	private int delay;
	public static RandomVariateGen genPriorityUnif =new UniformGen(new MRG32k3a(),0,1);
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
	
	public static Priority getPriority(int order, int delay){
		for (Priority priority : Priority.values()) {
			if (priority.getOrder()== order && priority.getDelay() == delay){
				return priority;
			}
		}
		return null;
	}
	
	public static Priority generatePriority(){
		int order ;
		int delay ;
		double i = genPriorityUnif.nextDouble();
		double j = genPriorityUnif.nextDouble();
		double z = genPriorityUnif.nextDouble();
		
		if (i<= 0.8) {
			order = 3;
			delay = 14;
		} 
		else if(j<=0.7) {
			order =2;
			delay = 3;
		}
		else if (z<=0.8){
			order =4;
			delay = 28;
		}
		else {
			order =1;
			delay = 1;
		}
		Priority priority = getPriority(order, delay);
		return priority;
	}
	
}
