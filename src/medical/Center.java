package medical;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import tools.Time;
import umontreal.iro.lecuyer.simevents.LinkedListStat;
import umontreal.iro.lecuyer.simevents.Sim;
import umontreal.iro.lecuyer.util.TextDataReader;
/**
 * Radiotherapy center
 * @author Joffrey
 *
 */
public class Center {
	private String name;
	private int centerId;
	/**
	 * Is true when the center is opened and can welcome patients, is false otherwise.
	 */
	boolean welcome;
	/**
	 * Time in minutes when the center opens everyday
	 */
	private int openingTime = 8*60;
	/**
	 * Time in minutes when the center closes everyday
	 */
	private int closingTime = 18*60;
	/**
	 * Doctors in the center, can be also accessed via there respective spheres.
	 */
	ArrayList<Doctor> doctors;
	/**
	 * Patients assigned to this center
	 */
	LinkedListStat<Patient> patients;
	/**
	 * Patients assigned to this center that are done with the radiotherapy process
	 */
	LinkedList<Patient> patientsOut;
	/**
	 * Scan machines of the center
	 */
	ArrayList<Scan> scans;
	/**
	 * Treatment machines of the center
	 */
	ArrayList<TreatmentMachine> treatmentMachines;
	/**
	 * Dosimetrists working in the center
	 */
	private ArrayList<Dosimetrist> dosimetrists;
	/**
	 * Model the tasks made by all the administrative agents working in the center
	 */
	private AdminAgent adminAgent;
	/**
	 * Model the tasks made by all the technologists working in the center
	 * TODO change to a list of multiple technologists
	 */
	private Technologist technologist;
	/**
	 * Spheres of the center, one for each cancer type
	 */
	private ArrayList <Sphere> spheres;
	
	public Center(){
		super();
		this.name="NoName";
		this.welcome = false;
		this.doctors = new ArrayList<>();
		this.patients = new LinkedListStat<>();
		this.patientsOut = new LinkedList<>();
		this.scans = new ArrayList<>();
		this.treatmentMachines = new ArrayList<>();
		this.spheres = new ArrayList<>();
		this.dosimetrists = new ArrayList<>();
		patients.setStatCollecting(true);
		patients.statSize().setName("Size of queue of patients in the center");
		patients.statSojourn().setName("Radiotherapy waiting time");
	}

	public Center(String name, int openingTime, int closingTime, ArrayList<Sphere> spheres){
		super();
		this.name=name;
		this.welcome = false;
		this.doctors = new ArrayList<>();
		this.patients = new LinkedListStat<>();
		this.patientsOut = new LinkedList<>();
		this.scans = new ArrayList<>();
		this.treatmentMachines = new ArrayList<>();
		this.spheres = spheres;
		this.dosimetrists = new ArrayList<>();
		patients.setStatCollecting(true);
		patients.statSize().setName("Size of queue of patients in the center");
		patients.statSojourn().setName("Radiotherapy waiting time");
		this.openingTime=openingTime;
		this.closingTime=closingTime;
	}
	
	public static Center centerFileReader(){
		String[] strings = null;
		Path path = FileSystems.getDefault().getPath("Center.txt");
		Charset charset = Charset.forName("US-ASCII");
		Center center =new Center();
		int lineCt = 0;
		
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			strings = TextDataReader.readStringData(reader);
			for (String string : strings) {
				if(lineCt==0){
					center.setName(string);
				}
				else if(lineCt==1){
					String regex="\t"; //regex for the tab character
					String[] cancerInfo = string.split(regex);
					center.setOpeningTime(Integer.parseInt(cancerInfo[0]));
					center.setClosingTime(Integer.parseInt(cancerInfo[1]));
				}
				else if(lineCt>=3){
					Cancer cancer = Cancer.get(string);
					Sphere sphere = new Sphere(center, cancer, null, new ArrayList<Doctor>(), new ArrayList<Patient>());
					ChefSphere chef = new ChefSphere(sphere);
					sphere.setChefSphere(chef);
					center.getSpheres().add(sphere);
				}
				lineCt++;
			}
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		return center;
	}
	
	/*
	 * Getters and setters
	 */

	public ArrayList<Doctor> getDoctors() {
		return doctors;
	}

	public void setDoctors(ArrayList<Doctor> doctors) {
		this.doctors = doctors;
	}

	public LinkedListStat<Patient> getPatients() {
		return patients;
	}

	public void setPatients(LinkedListStat<Patient> patients) {
		this.patients = patients;
	}

	public boolean isWelcome() {
		return welcome;
	}

	public void setWelcome(boolean welcome) {
		this.welcome = welcome;
	}

	public ArrayList<Scan> getCtscans() {
		return scans;
	}

	public void setCtscans(ArrayList<Scan> ctscans) {
		this.scans = ctscans;
	}
	
	public AdminAgent getAdminAgent() {
		return adminAgent;
	}

	public void setAdminAgent(AdminAgent adminAgent) {
		this.adminAgent = adminAgent;
	}

	/**
	 * Make all the people in the center with a schedule (ie implementing ISchedule) do their next task in their agenda
	 */
	public void doScheduleToday() {
		for (Patient patient : patients) {
			patient.getSchedule().doNextTask();
		}
		for(Doctor doctor : doctors){
			doctor.getSchedule().doNextTask();
		}
		for(Scan scan : scans){
			scan.getSchedule().doNextTask();
		}
		for(TreatmentMachine treatmentMachine : treatmentMachines){
			treatmentMachine.getSchedule().doNextTask();
		}
		for(Dosimetrist dosimetrist : dosimetrists){
			dosimetrist.getSchedule().doNextTask();
		}
	}
	
	/**
	 * adds the new coming week to all the people in the center with a schedule (ie implementing ISchedule)
	 */
	public void addWeek(){
		int time = (int) Sim.time();
		int weekId = Time.weekCorrespondingToTime(time);
		for (Patient patient : patients) {
			patient.addWeek(weekId);
		}
		for(Doctor doctor : doctors){
			doctor.addWeek(weekId);
		}
		for(Scan scan : scans){
			scan.addWeek(weekId);
		}
		for (TreatmentMachine treatmentMachine : treatmentMachines) {
			treatmentMachine.addWeek(weekId);
		}
		for(Dosimetrist dosimetrist : dosimetrists){
			dosimetrist.addWeek(weekId);
		}
	}


	public int getCenterId() {
		return centerId;
	}

	public void setCenterId(int id) {
		this.centerId = id;
	}

	public Technologist getTechnologist() {
		return technologist;
	}

	public void setTechnologist(Technologist technologist) {
		this.technologist = technologist;
	}

	public LinkedList<Patient> getPatientsOut() {
		return patientsOut;
	}

	public void setPatientsOut(LinkedList<Patient> patientsOut) {
		this.patientsOut = patientsOut;
	}
	
	/**
	 * Remove from patients and add to patients out
	 */
	public void toPatientsOut(Patient patient){
		patient.setOut(true);
		patients.remove(patient);
		patientsOut.add(patient);
		patients.statSize().update();
		int obs = Time.now() - patient.getReferredDate().toMinutes();
		patients.statSojourn().add(obs);
	}

	public ArrayList<TreatmentMachine> getTreatmentMachines() {
		return treatmentMachines;
	}
	

	public void setTreatmentMachines(ArrayList<TreatmentMachine> treatmentMachines) {
		this.treatmentMachines = treatmentMachines;
	}
	public ArrayList<Scan> getScans() {
		return scans;
	}



	public void setScans(ArrayList<Scan> scans) {
		this.scans = scans;
	}



	public ArrayList <Sphere> getSpheres() {
		return spheres;
	}



	public void setSpheres(ArrayList <Sphere> spheres) {
		this.spheres = spheres;
	}



	public ArrayList<Dosimetrist> getDosimetrists() {
		return dosimetrists;
	}



	public void setDosimetrists(ArrayList<Dosimetrist> dosimetrists) {
		this.dosimetrists = dosimetrists;
	}

	public int getOpeningTime() {
		return openingTime;
	}

	public void setOpeningTime(int openingTime) {
		this.openingTime = openingTime;
	}

	public int getClosingTime() {
		return closingTime;
	}

	public void setClosingTime(int closingTime) {
		this.closingTime = closingTime;
	}

	public ArrayList<ChefSphere> getChefsSphere(){
		ArrayList<ChefSphere> chefs = new ArrayList<>();
		for (Sphere sphere : this.getSpheres()) {
			chefs.add(sphere.getChefSphere());
		}
		return chefs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sphere getSphere(String speciality) {
		for (Sphere sphere : spheres) {
			if(sphere.getCancer().getName().equalsIgnoreCase(speciality)){
				return sphere;
			}
		}
		return null;
	}
}
