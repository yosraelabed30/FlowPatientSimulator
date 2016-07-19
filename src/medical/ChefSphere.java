package medical;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import scheduling.Activity;
import scheduling.ActivityType;
import scheduling.BlockType;
import scheduling.Date;
import tools.Time;
import events.Arrival;
import events.CheckAndPreConsultation;
import events.Consultation;

public class ChefSphere extends Resource{
	private ArrayList<Doctor> doctors;
	ArrayList<Integer> specialities;
	LinkedList<Patient> demands;
	private int centerId;
	
	public ChefSphere(Center center, ArrayList<Doctor> doctors,
			ArrayList<Integer> specialities) {
		super(center);
		this.doctors = doctors;
		this.specialities = specialities;
		this.demands = new LinkedList<>();
	}


	public void processUrgentDemands(Patient patient){
		int duration =45;
		Date dateLowerBound= Date.dateNow();
		Activity best= null; 
		ArrayList<Doctor> competentDoctors = new ArrayList<>();
		for (Doctor doctor : doctors) {
			Activity tmp = null;
			if (doctor.hasSkillsToTreat(patient)){
				competentDoctors.add(doctor);
				tmp=doctor.getSchedule().getFirstAvailabilityFridayQuotas(duration, BlockType.Consultation,dateLowerBound);
		    }
			if (best == null ||tmp.startsEarlierThan(best) ) {
				best = tmp;
			}
					
		}
		if (best==null || !(best.getDate().compareTo(patient.getDeadLine())==-1)){
			for (Doctor doctor : competentDoctors) {
				Activity tmp = null;
				if (doctor.isOverTime()== true){
					tmp= doctor.getSchedule().getFirstAvailabilityFridayQuotas(duration, BlockType.NotWorking, dateLowerBound);
					if (best == null ||tmp.startsEarlierThan(best) ) {
						best = tmp;	
					}
				}
			}
			
		}
	
		if(best!=null){
			Doctor firstAvailable = (Doctor) best.getResource();
			patient.setDoctor(firstAvailable);
			int weekId = best.getWeek().getWeekId();
			int dayId = best.getDay().getDayId();
			int start = (weekId==dateLowerBound.getWeekId() && dayId==dateLowerBound.getDayId())? Math.max(dateLowerBound.getMinute(), best.getStart()) : best.getStart();
			int end = start+duration;
			
			CheckAndPreConsultation check = new CheckAndPreConsultation(patient, false, getCenter().getAdminAgent());
			Activity consultationForDoctor = new Activity(best.getBlock(), start, end, ActivityType.Consultation, check);
			best.insert(consultationForDoctor);
			
			//Quotas of the week for the doc are decreased
			best.getWeek().decreaseQuotas();
			
			Activity consultationForPatient = consultationForDoctor.clone(); //at that moment, consultationForPatient's block is the one of consultationForDoctor
			Arrival arrival =  new Arrival();
			arrival.setPriority(0); //patient arrives first then the presence is checked
			consultationForPatient.setActivityEvent(arrival);
			
			Activity free = patient.getSchedule().findFreeActivityToInsertOtherActivity(weekId, dayId, start, end);

			free.insert(consultationForPatient); //and when inserted, consultationForPatient's block is the one of free
		}
		else{
			System.out.println("A consultation could not be found for a patient : "+patient.getId()+", "+patient.getPriority());
		}

	}
	
	public boolean sphereCorrespondsTo(int cancer) {
		return specialities.contains(cancer);
	}

	public void addToDemands(Patient patient) {
		this.demands.add(patient);
	}

	
	/**
	 * A consultation can always be found for a patient
	 */
	public void processDemands(){
		Iterator<Patient> demandsIter = demands.iterator();
		while (demandsIter.hasNext()) {
			Patient patient = demandsIter.next();
			this.processUrgentDemands(patient);
			demandsIter.remove();
		}
	}
}
