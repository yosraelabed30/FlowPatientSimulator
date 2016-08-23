package events;

import medical.Center;
import medical.ChefSphere;
import medical.Patient;
import medical.Priority;
import scheduling.Date;
import tools.Time;
import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.simevents.Event;

public class ReferredPatient extends Event{
	/**
	 * To generate the delay in between two patients arrivals
	 */
	private Center center;
	/**
	 * To generate the delay in between two patients arrivals
	 */
	public static RandomVariateGen genReferredPatient=new ExponentialGen(new MRG32k3a(), 1.16);
	/**
	 * Delay separating two patients arrivals
	 */
	private int delay;
	
	public ReferredPatient(Center center) {
		super();
		this.center = center;
		this.delay = 0;
	}

	public void actions() {
		if(center.isWelcome()){
			this.generateDelay();
			new ReferredPatient(center).schedule(getDelay()); // Next referred patient
		}
		Patient patient = new Patient(center);
		center.getPatients().add(patient);
		center.getPatients().statSize().update();
//		System.out.println("Referred patient ; patient id : "+patient.getId()+" with prio : "+patient.getPriority()+", min : "+Time.minIntoTheDay(Time.now()));
		patient.setReferredDate(Date.now());
		if(patient.getPriority() == Priority.P1 || patient.getPriority() == Priority.P2  ){
			for (ChefSphere chef : center.getChefSpheres()) {
				/*
				 * we suppose, for now, that there is only one sphere corresponding to the patient's cancer
				 */
				if(chef.getSphere().getCancer()==patient.getCancer()){
					patient.setSphere(chef.getSphere());
					chef.processDemands(patient);
					break;
				}
			}
			patient.getSchedule().doNextTask();
		}
		else{
			/*
			 * after, the demands will be added to the list of demands of the chef de sphere
			 */
			center.getAdminAgent().addToDemands(patient);
		}
	}

	public void generateDelay() {
		this.setDelay((int)(genReferredPatient.nextDouble()*60));
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

}
