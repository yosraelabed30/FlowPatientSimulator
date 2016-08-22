package medical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import org.jfree.data.time.Day;

import events.Contouring;
import scheduling.Block;
import scheduling.BlockType;
import scheduling.ISchedule;
import scheduling.Schedule;
import scheduling.Week;
import tools.Time;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.randvar.UniformGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import fileComparators.FileComparator1;

public class Doctor implements ISchedule {
	static private int doctorClassId = 0;
	private int id;
	private Schedule schedule;
	private ArrayList<Sphere> spheres;
	private ArrayList<Patient> folders;
	private LinkedList<Patient> filesForContouring;
	private LinkedList<Patient> filesForPlanTreatment;
	private boolean overTime;
	public static RandomVariateGen genDoctorUnif =new UniformGen(new MRG32k3a(),0,1);

	public Doctor(ArrayList<ArrayList<Block>> blocksTab, ArrayList<Sphere> spheres) {
		this.id = doctorClassId++;
		this.spheres = spheres;
		this.folders = new ArrayList<>();
		this.filesForContouring = new LinkedList<>();
		this.filesForPlanTreatment = new LinkedList<>();
		this.schedule = new Schedule(this);
		for (int i = 0; i < 7; i++) {
			for (Block block : blocksTab.get(i)) {
				block.setDay(getSchedule().getDefaultWeek().getDay(i));
			}
			getSchedule().getDefaultWeek().getDay(i).setBlocks(blocksTab.get(i));
		}
		if (genDoctorUnif.nextDouble() > 0.5) {
			this.setOverTime(true);

		} else {
			this.setOverTime(false);
		}

	}

	public boolean canTreat(Patient patient) {
		for (Sphere sphere : spheres) {
			if (sphere.getCancer() == patient.getCancer()) {
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

		boolean need = true;
		if (genDoctorUnif.nextDouble() <= 0.75) {
			need = true;
		} 
		else {
			need = false;
		}
		return need;
	}

	public TreatmentTechnic decidesTechnic(Patient patient) {

		TreatmentTechnic treatmentTechnic = TreatmentTechnic.generateTreatmentTechnic();

		return treatmentTechnic;
	}

	public ArrayList<ScanTechnic> decidesImageryTechnics(Patient patient) {

		int index = (int) ((int) 1 + (genDoctorUnif.nextDouble()* (3 - 1)));
		
		return ScanTechnic.generateScanTechnic(index);
	}

	public int decidesNbTreatments(Patient patient) {
		int nbTreatments = (int) ((int) 8 + (genDoctorUnif.nextDouble() * (40 - 8)));
		return nbTreatments;
	}

	public LinkedList<Patient> getFilesForContouring() {
		return filesForContouring;
	}

	public void setFilesForContouring(LinkedList<Patient> filesForContouring) {
		this.filesForContouring = filesForContouring;
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

	public LinkedList<Patient> getFilesForPlanTreatment() {
		return filesForPlanTreatment;
	}

	public void setFilesForPlanTreatment(LinkedList<Patient> filesForPlanTreatment) {
		this.filesForPlanTreatment = filesForPlanTreatment;
	}

}
