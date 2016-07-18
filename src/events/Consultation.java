package events;

import java.util.ArrayList;

import medical.Doctor;
import medical.ScanTechnic;
import medical.Patient;
import medical.TreatmentTechnic;
import tools.Time;

public class Consultation extends ActivityEvent{
	private Patient patient;
	
	public Consultation(Patient patient) {
		super();
		this.patient = patient;
	}

	@Override
	public void childActions() {
		boolean radiotherapyNeeded = patient.getDoctor().isRadiotherapyNeeded(patient); //TODO check how it works at the CHUM;
		if(radiotherapyNeeded){
			Doctor doctor = patient.getDoctor();
			ArrayList<ScanTechnic> imagery = doctor.decidesImageryTechnics(patient);
			patient.setImageryTechnics(imagery);
			
			TreatmentTechnic technic = doctor.decidesTechnic(patient);
			patient.setTreatmentTechnic(technic);
			//the number of treatments is fixed during the consultation 
			int nbTreatments = doctor.decidesNbTreatments(patient);
			patient.setNbTreatments(nbTreatments);
			new PostConsultation(this.getPatient()).schedule(delay);
		}
		else{
			patient.setOut(true);
			int time = Time.time();
			int min = Time.minIntoTheDay(time);
			//System.out.println("consultation done for patient id : "+patient.getId()+", at minute : "+min);
			patient.getDoctor().getSchedule().doNextTask();
		}
	}

	@Override
	public ActivityEvent clone() {
		return new Consultation(this.getPatient());
	}

	@Override
	public void generateDelay() {
		//TODO implements something that represents reality
		this.delay = 30;
	}

	public static int durationForScheduling() {
		return 60;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
}
 