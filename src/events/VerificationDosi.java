package events;

import java.util.LinkedList;

import medical.Doctor;
import medical.Dosimetrist;
import medical.Patient;
import tools.Time;

public class VerificationDosi extends ActivityEvent{

	public VerificationDosi() {
		super();
	
	}

	@Override
	public void childActions() {
		
		LinkedList<Patient> filesForVerif =Dosimetrist.getFilesForVerif();
		
		

		int numberOfFilesTreated = 0;
		int time = Time.time();
		int min = Time.minIntoTheDay(time);

		while (!filesForVerif.isEmpty() && numberOfFilesTreated <= 4) {
			Patient patient = filesForVerif.poll();
			Doctor doctor = patient.getDoctor();
			LinkedList<Patient> filesForPlanTreatment = doctor.getFilesForPlanTreatment();
			System.out.println("The folder of the patient id : " + patient.getId() + " with priority "
					+ patient.getPriority() + " is supported for the verification " + min);
			

			
			filesForPlanTreatment.add(patient);
			numberOfFilesTreated++;
		
	}
	}
		
		
		
		
		

	@Override
	public ActivityEvent clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void generateDelay() {
		// TODO Auto-generated method stub
		
	}

}
