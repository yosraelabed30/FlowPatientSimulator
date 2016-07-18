package fileComparators;

import java.util.Comparator;

import medical.Patient;

/**
 * Comparator for the files of patients, or any list of patients.
 * Each FileComparator class represents a different way to order the files of patients.
 * @author Joffrey
 *
 */
public class FileComparator1 implements Comparator<Patient>{

	@Override
	public int compare(Patient o1, Patient o2) {
		int prio1 = o1.getPriority().getOrder();
		int prio2 = o2.getPriority().getOrder();
		if(prio1<prio2){
			return -1;
		}
		else if(prio1==prio2){
			return 0;
		}
		else{
			return 1;
		}
	}
	
}