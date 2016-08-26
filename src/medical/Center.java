package medical;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import tools.Time;
import umontreal.iro.lecuyer.simevents.LinkedListStat;
import umontreal.iro.lecuyer.simevents.Sim;
/**
 * Radiotherapy center
 * @author Joffrey
 *
 */
public class Center {
	private int centerId;
	/**
	 * Is true when the center is opened and can welcome patients, is false otherwise.
	 */
	boolean welcome;
	/**
	 * Time in minutes when the center opens everyday
	 */
	private int openingTime = 7*60+30;
	/**
	 * Time in minutes when the center closes everyday
	 */
	private int closingTime = 30+21 * 60;
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
}
