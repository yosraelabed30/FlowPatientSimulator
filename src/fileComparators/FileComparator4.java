package fileComparators;

import java.util.Comparator;

import medical.Patient;

public 	class FileComparator4 implements Comparator<Patient>{
	private int alpha;
	private int beta;
	
	public FileComparator4(int alpha, int beta) {
		super();
		this.alpha = alpha;
		this.beta = beta;
	}

	@Override
	public int compare(Patient o1, Patient o2) {
		int v1 = alpha*o1.getPriority().getOrder()+beta*o1.getDeadLine().toMinutes();
		int v2 = alpha*o2.getPriority().getOrder()+beta*o2.getDeadLine().toMinutes();
		if(v1<v2){
			return -1;
		}
		else if(v1==v2){
			return 0;
		}
		else{
			return 1;
		}
	}
	
}