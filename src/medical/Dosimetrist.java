package medical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import fileComparators.FileComparator1;

public class Dosimetrist extends Resource{
	static private int dosimetristClassId=0;
	private int id;
	private ArrayList<Patient> patients;
	private LinkedList<Patient> filesForDosi;
	
	public Dosimetrist(Center center, int id, ArrayList<Patient> patients) {
		super(center);
		this.id = id;
		this.patients = patients;
		this.filesForDosi = new LinkedList<Patient>();
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

	public ArrayList<Patient> getPatients() {
		return patients;
	}

	public void setPatients(ArrayList<Patient> patients) {
		this.patients = patients;
	}

	public LinkedList<Patient> getFilesForDosi() {
		return filesForDosi;
	}

	public void setFilesForDosi(LinkedList<Patient> filesForDosi) {
		this.filesForDosi = filesForDosi;
	}
	
}
