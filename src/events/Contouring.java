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

public class Contouring extends ActivityEvent {
	public static RandomVariateGen genContouringUnif =new UniformGen(new MRG32k3a(),0,1);

	public Contouring() {
		super();
	}

	@Override
	public void childActions() {
		Doctor doctor = (Doctor) this.getiSchedule();
		LinkedList<Patient> filesForContouring = doctor.getFilesForContouring();
		LinkedList< Patient> filesForDosi = Dosimetrist.getFilesForDosi();
		int numberOfFilesTreated =0;
		int time = Time.now();
		int min = Time.minIntoTheDay(time);
		
		while (!filesForContouring.isEmpty() && numberOfFilesTreated <= 4) {
			Patient patient = filesForContouring.poll();
			filesForDosi.add(patient);
			numberOfFilesTreated++;
		}
//		System.out.println("Contouring ; done by doctor id : "+doctor.getId()+", at min : "+min);
		doctor.getSchedule().doNextTask();
	}

	@Override
	public ActivityEvent clone() {
		Contouring clone = new Contouring();
		clone.setActivity(this.getActivity());
		return clone;
	}


	public void generateDelay(Patient patient) {
		
		if (patient.getPriority() == Priority.P1 || patient.getPriority() == Priority.P1) {
			this.delay = (int) (30+ genContouringUnif.nextDouble() * 60 - 30);
		} else {
			this.delay = (int) (60 + genContouringUnif.nextDouble() * (180 - 60));
		}
	}
	
	public void generateDelay() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
