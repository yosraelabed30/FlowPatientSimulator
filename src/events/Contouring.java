package events;

import java.util.LinkedList;

import medical.Doctor;
import medical.Dosimetrist;
import medical.Patient;
import medical.Priority;
import medical.Technologist;
import tools.Time;

public class Contouring extends ActivityEvent {

	public Contouring() {
		super();
	}

	@Override
	public void childActions() {
		Doctor doctor = (Doctor) this.getiSchedule();
		LinkedList<Patient> filesForContouring = doctor.getFilesForContouring();
		LinkedList< Patient> filesForDosi =Dosimetrist.getFilesForDosi();
		int numberOfFilesTreated =0;
		int time = Time.time();
		int min = Time.minIntoTheDay(time);
		
		while (!filesForContouring.isEmpty() && numberOfFilesTreated <= 4) {
			Patient patient = filesForContouring.poll();
			System.out.println("The folder of the patient id : " + patient.getId() + " with priority "
					+ patient.getPriority() + " is supported for the Contouring " + min);
			filesForDosi.add(patient);
			numberOfFilesTreated++;
		}
	}

	@Override
	public ActivityEvent clone() {
		Contouring clone = new Contouring();
		clone.setActivity(this.getActivity());
		return clone;
	}


	public void generateDelay(Patient patient) {
		
		if (patient.getPriority() == Priority.P1 || patient.getPriority() == Priority.P1) {
			this.delay = (int) (30+ Math.random() * 60 - 30);
		} else {
			this.delay = (int) (60 + Math.random() * (180 - 60));
		}
	}
	
	public void generateDelay() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
