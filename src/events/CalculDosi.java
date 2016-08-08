package events;

import java.util.LinkedList;

import medical.Doctor;
import medical.Dosimetrist;
import medical.Patient;
import medical.Priority;
import medical.Technologist;
import tools.Time;
import umontreal.iro.lecuyer.simevents.Event;

public class CalculDosi extends ActivityEvent{

	public CalculDosi() {
		super();
	}

	@Override
	public void childActions() {
		
		LinkedList<Patient> filesForDosi = Dosimetrist.getFilesForDosi();
		LinkedList<Patient> filesForVerif =Dosimetrist.getFilesForVerif();

		int numberOfFilesTreated = 0;
		int time = Time.time();
		int min = Time.minIntoTheDay(time);

		while (!filesForDosi.isEmpty() && numberOfFilesTreated <= 4) {
			Patient patient = filesForDosi.poll();
			System.out.println("The folder of the patient id : " + patient.getId() + " with priority "
					+ patient.getPriority() + " is supported for the Dosi " + min);
			

			
			filesForVerif.add(patient);
			numberOfFilesTreated++;

		}

		
	}

	@Override
	public ActivityEvent clone() {
		return new CalculDosi();
	}

	public void generateDelay(Patient patient) {

		if (patient.getPriority() == Priority.P1 || patient.getPriority() == Priority.P1) {
			this.delay = (int) (15 + Math.random() * 30 - 15);
		} else {
			this.delay = (int) (30 + Math.random() * (180 - 30));

		}

	}

	@Override
	public void generateDelay() {
		// TODO Auto-generated method stub
		
	}

}
