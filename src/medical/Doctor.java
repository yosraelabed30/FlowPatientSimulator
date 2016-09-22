package medical;

import hep.aida.ref.Converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
import umontreal.iro.lecuyer.util.TextDataReader;
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
		
		for (int i = 0; i < blocksTab.size(); i++) { //for each day in the week
			//sets the day for each block
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

	/**
	 * The center(s?) must already be created, so that a doctor can be assigned to a sphere
	 * @return
	 */
	public static ArrayList<Doctor> doctorsFileReader(Center center){
		String[] strings = null;
		Path path = FileSystems.getDefault().getPath("ScheduleDoctors.txt");
		Charset charset = Charset.forName("US-ASCII");
		ArrayList<ArrayList<Block>> blocksTab = null;
		ArrayList<Doctor> doctors = new ArrayList<Doctor>();
		ArrayList<Sphere> spheres = new ArrayList<Sphere>();
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			strings = TextDataReader.readStringData(reader);
			String regex="\t"; //regex for the tab character
			for (String string : strings) {
				if(!string.isEmpty()){
					String[] doctorInfo = string.split(regex);
					if(doctorInfo[0].equalsIgnoreCase("Doctor")){
						// we encounter a new doctor
						blocksTab = new ArrayList<>();
						spheres = new ArrayList<Sphere>();
					}
					else if(doctorInfo[0].equalsIgnoreCase("Speciality")){
						for (String speciality : doctorInfo) {
							if(!speciality.equalsIgnoreCase("Speciality")){
								spheres.add(center.getSphere(speciality));
							}
						}
					}
					else if(doctorInfo[0].equalsIgnoreCase("Day")){
						blocksTab.add(new ArrayList<Block>());
					}
					else if(doctorInfo.length==4 && !doctorInfo[0].equalsIgnoreCase("Speciality")){
						Block b = new Block(Integer.parseInt(doctorInfo[0]), Integer.parseInt(doctorInfo[2]), Integer.parseInt(doctorInfo[3]), BlockType.get(doctorInfo[1]));
						blocksTab.get(blocksTab.size()-1).add(b);
					}
				}
				else{
					doctors.add(new Doctor(blocksTab, spheres));
				}
			}
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		return doctors;
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
	
	public int decidesNbTreatments2(Patient patient){
		int nbTreatments=-1;
		double rnd = genDoctorUnif.nextDouble();
		for (int[] tab : patient.getCancer().getNbTreatments()) {
			if(rnd<tab[0]){
				nbTreatments=tab[1];
				break;
			}
		}
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
