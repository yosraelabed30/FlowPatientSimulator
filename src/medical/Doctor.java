package medical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import scheduling.Block;
import scheduling.ISchedule;
import scheduling.Schedule;
import scheduling.Week;
import fileComparators.FileComparator1;

public class Doctor  implements ISchedule{
	static private int doctorClassId=0;
	private int id;
	private ArrayList<Sphere> spheres;
	private ArrayList<Patient> folders;
	private LinkedList<Patient> filesForContouring; //TODO during the contouring block
	private LinkedList<Patient> filesForDosi;
	private Schedule schedule;
	private boolean overTime;
	private Center center;
	


	public Doctor(ArrayList<ArrayList<Block>> blocksTab, ArrayList<Sphere> spheres, Center center ) {

		this.setCenter(center);
		this.id = doctorClassId++;
		this.spheres=spheres;
		this.folders = new ArrayList<>();
		this.filesForContouring = new LinkedList<>();
		this.filesForDosi = new LinkedList<>();
		this.schedule = new Schedule(this);
		for (int i = 0; i < 7; i++) {
			for (Block block : blocksTab.get(i)) {
				block.setDay(getSchedule().getDefaultWeek().getDay(i));
			}
			getSchedule().getDefaultWeek().getDay(i).setBlocks(blocksTab.get(i));
		}
		if (Math.random() > 0.5) {
			this.setOverTime(true);

		}
		else {
			this.setOverTime(false);
		}

	}

	public boolean canTreat(Patient patient) {
		for (Sphere sphere : spheres) {
			if (sphere.getCancer()==patient.getCancer()){
				return true;
			}
			
		}
		return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Patient> getFolders() {
		return folders;
	}

	public void setFolders(ArrayList<Patient> folders) {
		this.folders = folders;
	}


	public boolean isRadiotherapyNeeded(Patient patient) {
		// TODO 
		return true;
	}

	public TreatmentTechnic decidesTechnic(Patient patient) {
		// TODO change that to sth realistic
		int a = (int)Math.random()*2;
		TreatmentTechnic t = null;
		if(a==0){
			t = TreatmentTechnic.technic1;
		}
		else{
			t = TreatmentTechnic.technic2;
		}
		return t;
	}

	public ArrayList<ScanTechnic> decidesImageryTechnics(Patient patient) {
		// TODO change it not to return all the technics
		return new ArrayList<ScanTechnic>(Arrays.asList(ScanTechnic.values()));
	}

	public int decidesNbTreatments(Patient patient) {
		// TODO change it to correspond to reality
		return 25;
	}

	public LinkedList<Patient> getFilesForContouring() {
		return filesForContouring;
	}

	public void setFilesForContouring(LinkedList<Patient> filesForContouring) {
		this.filesForContouring = filesForContouring;
	}
	
	public void processPatientFilesForContouring(){
		Collections.sort(this.filesForContouring, new FileComparator1());
	}

	public LinkedList<Patient> getFilesForDosi() {
		return filesForDosi;
	}

	public void setFilesForDosi(LinkedList<Patient> filesForDosi) {
		this.filesForDosi = filesForDosi;
	}

	@Override
	public Week addWeek(int weekId) {
		return getSchedule().addWeek(weekId);
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public boolean isOverTime() {
		return overTime;
	}

	public void setOverTime(boolean overTime) {
		this.overTime = overTime;
	}

	public ArrayList<Sphere> getSpheres() {
		return spheres;
	}

	public void setSpheres(ArrayList<Sphere> spheres) {
		this.spheres = spheres;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

}
