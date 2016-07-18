package medical;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import scheduling.Activity;
import scheduling.ActivityType;
import scheduling.BlockType;
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
	
//	public void processUrgentDemands(Patient patient) {
//		// TODO implements missing methods
//		boolean noOncologistAvailableUnder24Hours = true;
//		Doctor firstAvailable = new Doctor();
//		int delay = Integer.MAX_VALUE;
//		int time = Time.time();
//		int day = Time.dayCorrespondingToTime(time);
//		int week = Time.weekCorrespondingToTime(time);
//		ArrayList<Doctor> competentDoctors = new ArrayList<>();
//		
//		for (Doctor oncologist : doctors) {
//			if(oncologist.hasSkillsToTreat(patient)){
//				competentDoctors.add(oncologist);
//				if(oncologist.availableBeforeForUrgentConsultation(week, day, delay)){
//					noOncologistAvailableUnder24Hours = false;
//					//availableBeforeForUrgentConsultation check if the quotas are reached, and if it is under 24hours 
//					//30 min consultation //a consultation is 60 min, 30 min with doc and 30 with a nurse (post-consultation)
//					firstAvailable = oncologist;
//				}
//			}
//		}
//		if(noOncologistAvailableUnder24Hours){
//			if(!competentDoctors.isEmpty()){ // we suppose there is always a competent doctor for the patient
//				Collections.shuffle(competentDoctors);
//				firstAvailable = competentDoctors.get(0);
//			}
//		}
//		firstAvailable.insertEmergencyConsultation(patient);
//	}

	public boolean sphereCorrespondsTo(int cancer) {
		return specialities.contains(cancer);
	}

	public void addToDemands(Patient patient) {
		this.demands.add(patient);
	}

	public void processDemands(){
		int time = Time.time();
		int weekNow = Time.weekCorrespondingToTime(time);
		int dayNow = Time.weekDayCorrespondingToTime(time);
		
		int consultationDuration = Consultation.durationForScheduling();
		Activity earliest = null;
		Activity act = null;
		Iterator<Patient> demandsIter = demands.iterator();
		while (demandsIter.hasNext()) {
			Patient patient = demandsIter.next();
			ArrayList<Doctor> competentDoctors = new ArrayList<>();
			for (Doctor oncologist : doctors) {
				if(oncologist.hasSkillsToTreat(patient)){
					competentDoctors.add(oncologist);
					act = oncologist.getSchedule().getFirstAvailability(consultationDuration, BlockType.Consultation);
					if(earliest==null || act.startsEarlierThan(earliest)){
						//availableBeforeForConsultation check if the quotas are reached
						//30 min consultation //a consultation is 60 min, 30 min with doc and 30 with a nurse (post-consultation)
						earliest = act;
					}
				}
			}
			
			if(earliest!=null){
				Doctor firstAvailable = (Doctor) earliest.getResource();
				patient.setDoctor(firstAvailable);
				int weekId = earliest.getWeek().getWeekId();
				int dayId = earliest.getDay().getDayId();
				int start = (weekId==weekNow && dayId==dayNow)? Math.max(time, earliest.getStart()) : earliest.getStart();
				int end = start+consultationDuration;
				
				CheckAndPreConsultation check = new CheckAndPreConsultation(patient, false, getCenter().getAdminAgent());
				Activity consultationForDoctor = new Activity(earliest.getBlock(), start, end, ActivityType.Consultation, check);
				earliest.insert(consultationForDoctor);
				
				Activity consultationForPatient = consultationForDoctor.clone(); //at that moment, consultationForPatient's block is the one of consultationForDoctor
				Arrival arrival =  new Arrival();
				arrival.setPriority(0); //patient arrives first then the presence is checked
				consultationForPatient.setActivityEvent(arrival);
				
				Activity free = patient.getSchedule().findFreeActivityToInsertOtherActivity(weekId, dayId, start, end);

				free.insert(consultationForPatient); //and when inserted, consultationForPatient's block is the one of free
				demandsIter.remove();
			}
			else{
				System.out.println("couldn't find a doctor");
			}
		}
	}
}
