package events;

import java.util.LinkedList;

import medical.Doctor;
import medical.Dosimetrist;
import medical.Patient;
import tools.Time;

public class TreatmentPlan extends ActivityEvent{

	public TreatmentPlan() {
		super();
		
	}

	@Override
	public void childActions() {
		Doctor doctor = (Doctor) this.getiSchedule();
		LinkedList<Patient> filesForPlanTreatment = doctor.getFilesForPlanTreatment();
	
		int numberOfFilesTreated =0;
		int time = Time.time();
		int min = Time.minIntoTheDay(time);
		while (!filesForPlanTreatment.isEmpty() && numberOfFilesTreated <= 4) {
			Patient patient = filesForPlanTreatment.poll();
			System.out.println("The folder of the patient id : " + patient.getId() + " with priority "
					+ patient.getPriority() + " is supported for the Contouring " + min);
	
		
			numberOfFilesTreated++;

		}
		
		
		
	}

	@Override
	public ActivityEvent clone() {
		
		return new TreatmentPlan();
	}

	@Override
	public void generateDelay() {
		// TODO Auto-generated method stub
		
	}

}
