package events;

import medical.Center;
import medical.ChefSphere;
import medical.Patient;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;

public class ReferredPatient extends ActivityEvent{
	/**
	 * To generate the delay in between two patients arrivals
	 */
	RandomVariateGen genRef;
	private Center center;
	
	public ReferredPatient(RandomVariateGen genRef, Center center) {
		super();
		this.genRef = genRef;
		this.center = center;
	}

	public void childActions() {
		if(center.isWelcome()){
			new ReferredPatient(genRef, center).schedule(delay); // Next referred patient
		}
		Patient patient = new Patient(center);
		center.getPatients().add(patient);
		if(patient.isEmergency()){
			for (ChefSphere chef : center.getChefSpheres()) {
				/*
				 * we suppose, for now, that there is only one sphere corresponding to the patient's cancer
				 */
				if(chef.sphereCorrespondsTo(patient.getCancer())){
					//TODO resume the implementation : chef.processUrgentDemands(patient);
				}
			}
		}
		else{
			/*
			 * after, the demands will be added to the list of demands of the chef de sphere
			 */
			center.getAdminAgent().addToDemands(patient);
		}
	}

	@Override
	public ActivityEvent clone() {
		return new ReferredPatient(genRef, center);
	}

	@Override
	public void generateDelay() {
		this.delay=(int)(genRef.nextDouble()*60);
	}

}
