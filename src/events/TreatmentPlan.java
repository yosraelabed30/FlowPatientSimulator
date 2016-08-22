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
		int time = Time.now();
		int min = Time.minIntoTheDay(time);
		while (!filesForPlanTreatment.isEmpty() && numberOfFilesTreated <= 4) {
			Patient patient = filesForPlanTreatment.poll();
			numberOfFilesTreated++;
		}
//		System.out.println("TreatmentPlan ; done by doctor id : "+doctor.getId());
		doctor.getSchedule().doNextTask();
	}

	@Override
	public ActivityEvent clone() {
		TreatmentPlan clone = new TreatmentPlan();
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		// TODO Auto-generated method stub
	}

}
