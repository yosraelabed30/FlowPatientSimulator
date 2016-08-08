package medical;


import java.util.Collections;
import java.util.LinkedList;

import fileComparators.FileComparator1;

public class Dosimetrist extends Resource{
	static private int dosimetristClassId=0;
	private int id;

	private static LinkedList<Patient> filesForDosi;
	private static LinkedList<Patient> filesForVerif;
	
	public Dosimetrist(Center center, int id) {
		super(center);
		this.id = id;
		
		Dosimetrist.filesForDosi = new LinkedList<Patient>();
		Dosimetrist.filesForVerif=new LinkedList<Patient>();
	}

	public void processFilesForDosi(){
		Collections.sort(filesForDosi, new FileComparator1());
		
	}
	
	public static int getDosimetristClassId() {
		return dosimetristClassId;
	}

	public static void setDosimetristClassId(int dosimetristClassId) {
		Dosimetrist.dosimetristClassId = dosimetristClassId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static LinkedList<Patient> getFilesForDosi() {
		return filesForDosi;
	}

	public void setFilesForDosi(LinkedList<Patient> filesForDosi) {
		Dosimetrist.filesForDosi = filesForDosi;
	}

	public static LinkedList<Patient> getFilesForVerif() {
		return filesForVerif;
	}

	public static void setFilesForVerif(LinkedList<Patient> filesForVerif) {
		Dosimetrist.filesForVerif = filesForVerif;
	}
	
}
