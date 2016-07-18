package fileComparators;

import java.util.Comparator;

import medical.Patient;

public 	class FileComparator3 implements Comparator<Patient>{

	@Override
	public int compare(Patient o1, Patient o2) {
		//"Ponderation"
		int res = new FileComparator2().compare(o1, o2);
		if(res==0){
			res  = new FileComparator1().compare(o1, o2);
		}
		return res;
	}
	
}