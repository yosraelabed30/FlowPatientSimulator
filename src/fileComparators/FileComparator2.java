package fileComparators;

import java.util.Comparator;

import medical.Patient;

public class FileComparator2 implements Comparator<Patient>{

	@Override
	public int compare(Patient o1, Patient o2) {
		int d1 = o1.remainingDaysTillDeadLine();
		int d2 = o2.remainingDaysTillDeadLine();
		if(d1<d2){
			return -1;
		}
		else if(d1==d2){
			return 0;
		}
		else{
			return 1;
		}
	}
	
}