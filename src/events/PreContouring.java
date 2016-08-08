package events;


import java.util.LinkedList;

import medical.Doctor;
import medical.Patient;
import medical.Technologist;
import tools.Time;

public class PreContouring extends ActivityEvent{
	
	
	

	public PreContouring( ) {

		super();
		
		
	}

	@Override
	public void childActions() {
		
		LinkedList<Patient> filesForPreContouring = Technologist.getFilesForPreContouring();

		int numberOfFilesTreated = 0;
		int time = Time.time();
		int min = Time.minIntoTheDay(time);

		while (!filesForPreContouring.isEmpty() && numberOfFilesTreated <= 4) {
			Patient patient = filesForPreContouring.poll();
			System.out.println("The folder of the patient id : " + patient.getId() + " with priority "
					+ patient.getPriority() + " is supported for the preContouring " + min);
			Doctor doctor = patient.getDoctor();

			LinkedList<Patient> fileForContouring = doctor.getFilesForContouring();
			fileForContouring.add(patient);
			numberOfFilesTreated++;

		}
		
		
	
			
		
	}



	@Override
	
	public ActivityEvent clone() {
		return new PreContouring() ;
	}
	

	@Override
	public void generateDelay() {
		// TODO Auto-generated method stub
	}




}
