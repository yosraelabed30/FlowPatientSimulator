package events;

import java.util.LinkedList;

import medical.Doctor;
import medical.Dosimetrist;
import medical.Patient;
import medical.Priority;
import medical.Technologist;
import tools.Time;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.randvar.UniformGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.simevents.Event;

public class CalculDosi extends ActivityEvent{
	public static RandomVariateGen genCalculDosiUnif =new UniformGen(new MRG32k3a(),0,1);

	public CalculDosi() {
		super();
	}

	@Override
	public void endActions() {
		
		LinkedList<Patient> filesForDosi = Dosimetrist.getFilesForDosi();
		LinkedList<Patient> filesForVerif =Dosimetrist.getFilesForVerif();

		int numberOfFilesTreated = 0;
		int time = Time.now();
		int min = Time.minIntoTheDay(time);
		Dosimetrist dosimetrist = (Dosimetrist) this.getiSchedule();

		while (!filesForDosi.isEmpty() && numberOfFilesTreated <= 4) {
			Patient patient = filesForDosi.poll();
			filesForVerif.add(patient);
			numberOfFilesTreated++;
		}
		new VerificationDosi(dosimetrist).schedule(0); // something wrong here, if it's supposed to be on the dosimetrist schedule then it should be linked to an activity or else it's an event and not an activityEvent
//		System.out.println("CalculDosi ; done by dosimetrist id : "+dosimetrist.getId()+", at min : "+min);
	}

	@Override
	public ActivityEvent clone() {
		CalculDosi clone = new CalculDosi();
		clone.setActivity(this.getActivity());
		return clone;
	}

	public void generateDelay(Patient patient) {

		if (patient.getPriority() == Priority.P1 || patient.getPriority() == Priority.P1) {
			this.delay = (int) (15 + genCalculDosiUnif.nextDouble() * 30 - 15);
		} else {
			this.delay = (int) (30 + genCalculDosiUnif.nextDouble() * (180 - 30));

		}

	}

	@Override
	public void generateDelay() {
		this.setDelay(0);
	}

	@Override
	public void startActions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean conditions() {
		return true;
	}

}
