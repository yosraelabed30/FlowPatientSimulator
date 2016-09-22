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
	public void endActions() {
		boolean radiotherapyNeeded = patient.getDoctor().isRadiotherapyNeeded(patient); //TODO check how it works at the CHUM;
		if(radiotherapyNeeded){
			Doctor doctor = patient.getDoctor();
			ArrayList<ScanTechnic> imagery = doctor.decidesImageryTechnics(patient);
			patient.setScanTechnics(imagery);
			
			TreatmentTechnic technic = doctor.decidesTechnic(patient);
			patient.setTreatmentTechnic(technic);
			//the number of treatments is fixed during the consultation 
			int nbTreatments = doctor.decidesNbTreatments2(patient);
			patient.setNbTreatments(nbTreatments);
			getPatient().getDoctor().getSchedule().doNextTask();
			new PostConsultation(this.getPatient()).schedule(delay);
		}
		else{
			patient.toPatientsOut();
			int time = Time.now();
			int min = Time.minIntoTheDay(time);
//			System.out.println("No radiotherapy : consultation done for patient id : "+patient.getId()+" with priority : "+patient.getPriority()+", at minute : "+min);
			patient.getDoctor().getSchedule().doNextTask();
		}
	}

	@Override
	public ActivityEvent clone() {
		return new Consultation(this.getPatient());
	}

	@Override
	public void generateDelay() {
		this.delay = 30;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public void startActions() {
		
	}

	@Override
	public boolean conditions() {
		return true;
	}
	
}
 