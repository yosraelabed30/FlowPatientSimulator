package events;

import java.util.LinkedList;

import medical.Doctor;
import medical.Dosimetrist;
import medical.Patient;
import tools.Time;

public class VerificationDosi extends ActivityEvent{
	private Dosimetrist dosimetrist;
	
	public VerificationDosi(Dosimetrist dosimetrist) {
		super();
		this.dosimetrist = dosimetrist;
	}

	@Override
	public void childActions() {
		LinkedList<Patient> filesForVerif = Dosimetrist.getFilesForVerif();
		int numberOfFilesTreated = 0;
		int time = Time.time();
		int min = Time.minIntoTheDay(time);
			
		while (!filesForVerif.isEmpty() && numberOfFilesTreated <= 4) {
			Patient patient = filesForVerif.poll();
			Doctor doctor = patient.getDoctor();
			LinkedList<Patient> filesForPlanTreatment = doctor
					.getFilesForPlanTreatment();
//			System.out.println("The folder of the patient id : "
//					+ patient.getId() + " with priority "
//					+ patient.getPriority()
//					+ " is supported for the verification " + min);
			filesForPlanTreatment.add(patient);
			numberOfFilesTreated++;
		}
		System.out.println("VerificationDosi ; done");
		dosimetrist.getSchedule().doNextTask();
	}
	
	@Override
	public ActivityEvent clone() {
		VerificationDosi clone = new VerificationDosi(this.getDosimetrist());
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		// TODO Auto-generated method stub
		
	}

	public Dosimetrist getDosimetrist() {
		return dosimetrist;
	}

	public void setDosimetrist(Dosimetrist dosimetrist) {
		this.dosimetrist = dosimetrist;
	}

}
