package medical;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import tools.Time;
import umontreal.iro.lecuyer.simevents.Sim;

public class Center {
	private int centerId;
	/**
	 * Is true when the center is opened and can welcome patients, is false otherwise.
	 */
	boolean welcome;
	ArrayList<ChefSphere> chefSpheres;
	ArrayList<Doctor> doctors;
	LinkedList<Patient> patients;
	LinkedList<Patient> patientsOut;
	ArrayList<Scan> scans;
	ArrayList<TreatmentMachine> treatmentMachines;
	
	private ArrayList<Dosimetrist> dosimetrists;
	private AdminAgent adminAgent;
	private Technologist technologist;
	private ArrayList <Sphere> spheres;
	
	public Center(){
		super();
		this.welcome = false;
		this.doctors = new ArrayList<>();
		this.patients = new LinkedList<>();
		this.patientsOut = new LinkedList<>();
		this.scans = new ArrayList<>();
		this.chefSpheres = new ArrayList<>();
		this.treatmentMachines = new ArrayList<>();
		this.spheres = new ArrayList<>();
		this.dosimetrists = new ArrayList<>();
	}

	
	
	public Center(int centerId, boolean welcome,
			ArrayList<ChefSphere> chefSpheres, ArrayList<Doctor> doctors,
			LinkedList<Patient> patients, LinkedList<Patient> patientsOut,
			ArrayList<Scan> scans,
			ArrayList<TreatmentMachine> treatmentMachines,
			ArrayList<Dosimetrist> dosimetrists, AdminAgent adminAgent,
			Technologist technologist, ArrayList <Sphere> spheres) {
		super();
		this.centerId = centerId;
		this.welcome = welcome;
		this.chefSpheres = chefSpheres;
		this.doctors = doctors;
		this.patients = patients;
		this.patientsOut = patientsOut;
		this.scans = scans;
		this.treatmentMachines = treatmentMachines;
		this.setDosimetrists(dosimetrists);
		this.adminAgent = adminAgent;
		this.technologist = technologist;
		this.spheres=spheres;
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

	public LinkedList<Patient> getPatients() {
		return patients;
	}

	public void setPatients(LinkedList<Patient> patients) {
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

	public ArrayList<ChefSphere> getChefSpheres() {
		return chefSpheres;
	}

	public void setChefSpheres(ArrayList<ChefSphere> chefSpheres) {
		this.chefSpheres = chefSpheres;
	}
	
	public AdminAgent getAdminAgent() {
		return adminAgent;
	}

	public void setAdminAgent(AdminAgent adminAgent) {
		this.adminAgent = adminAgent;
	}

	public void doScheduleToday() {
		for (Patient patient : patients) {
			if(!patient.isOut()){
				patient.getSchedule().doNextTask();
			}
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
	 * adds the beginning week to every participant
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
	
	public void fromPatientsToPatientsOut(){
		Iterator<Patient> patientsIter = patients.iterator();
		while(patientsIter.hasNext()){
			Patient p = patientsIter.next();
			if(p.isOut()){
				patientsIter.remove();
				patientsOut.add(p);
			}
		}
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

	
}
