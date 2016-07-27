package medical;

import java.util.ArrayList;

public class Sphere{
	
	
	private Center center;
	private Cancer cancer;
	private ChefSphere chefSphere;
	private ArrayList <Doctor> doctors ;
	private ArrayList <Patient> patients;
	
	public Sphere(Center center, Cancer cancer, ChefSphere chefSphere, ArrayList <Doctor> doctors,ArrayList <Patient> patients ) {
		this.center = center;
		this.setCancer(cancer);
		this.setChefSphere(chefSphere);
		this.setDoctors(doctors);
		this.setPatients(patients);
	
	}
	public Cancer getCancer() {
		return cancer;
	}
	public void setCancer(Cancer cancer) {
		this.cancer = cancer;
	}
	public ChefSphere getChefSphere() {
		return chefSphere;
	}
	public void setChefSphere(ChefSphere chefSphere) {
		this.chefSphere = chefSphere;
	}
	public ArrayList <Doctor> getDoctors() {
		return doctors;
	}
	public void setDoctors(ArrayList <Doctor> doctors) {
		this.doctors = doctors;
	}
	public ArrayList <Patient> getPatients() {
		return patients;
	}
	public void setPatients(ArrayList <Patient> patients) {
		this.patients = patients;
	}
	public Center getCenter() {
		return center;
	}
	public void setCenter(Center center) {
		this.center = center;
	}

	
	

}
