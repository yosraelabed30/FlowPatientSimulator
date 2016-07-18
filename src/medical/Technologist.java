package medical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import scheduling.Activity;
import scheduling.ActivityType;
import scheduling.Block;
import scheduling.BlockType;
import scheduling.Date;
import scheduling.Day;
import scheduling.ISchedule;
import scheduling.Schedule;
import scheduling.Week;
import simulation.FlowOfPatients;
import tools.Time;
import umontreal.iro.lecuyer.stochprocess.GeometricBrownianMotion;
import events.ActivityEvent;
import events.Arrival;
import events.CTSim;
import events.FirstTreatment;
import events.Treatment;
import fileComparators.FileComparator1;

public class Technologist extends Resource implements ISchedule{
	private static final int ArrayList = 0;
	private LinkedList<Patient> filesForPlanification;
	private ArrayList<Scan> scans;
	private ArrayList<TreatmentMachine> tmachines;
	private LinkedList<Patient> filesForPreContouring;
	private Schedule schedule ;
	private LevelTechnologist level;
	
	
	public Technologist(Center center){
		this(center, new LinkedList<Patient>(), center.getCtscans(), center.getTreatmentMachines(), new LinkedList<Patient>());
	}
	
	public Technologist(Center center, LinkedList<Patient> filesForPlanification,
			ArrayList<Scan> scans, ArrayList<TreatmentMachine> tmachines,
			LinkedList<Patient> filesForPreContouring) {
		super(center);
		this.filesForPlanification = filesForPlanification;
		this.setScans(scans);
		this.tmachines = tmachines;
		this.filesForPreContouring = filesForPreContouring;
	}
	
	public void processPatientFilesForPlanification(){
		Collections.sort(getFilesForCTSimTreatment(),new FileComparator1());
		Date dateLowerBound = Date.dateNow();
		Iterator<Patient> filesForPlanificationIter = filesForPlanification.iterator();
		Activity firstTreatment = null;
		Activity CTSim = null;
		while (filesForPlanificationIter.hasNext()) {
			Patient patient = filesForPlanificationIter.next();
			double realtime = System.currentTimeMillis();
			
			firstTreatment =  this.planificationTreatment(patient, dateLowerBound);
			
			CTSim =  this.planificationCtSim(patient, firstTreatment );
			
			if (CTSim == null){
				System.out.println("connaitre le pblm");
			}
			System.out.println("associated activity for CTSim "+CTSim);
			if(firstTreatment != null && CTSim != null){
				filesForPlanificationIter.remove();
			}
		}
	}
	
	
	private Activity planificationCtSim(Patient patient, Activity firstTreatment) {
		
		Date dateUpperBound = firstTreatment.getDate();
		Date dateCTSim =null;
		boolean planned=false;
		int duration=30;
		Date dateLowerBound= dateUpperBound.decreaseDays(patient);
		int weekLowerBound = dateLowerBound.getWeekId();
		int dayLowerBound = dateLowerBound.getDayId();
		int minuteLowerBound = dateLowerBound.getMinute();
		int weekUpperBound = dateUpperBound.getWeekId();
		int dayUpperBound = dateUpperBound.getDayId();
		int minuteUpperBound = dateUpperBound.getMinute();
		Activity best= null;
		ArrayList <Scan> adequateMachineScan= new ArrayList<>();
		ArrayList <ScanTechnic> scanTechnic = patient.getImageryTechnics();
		ArrayList<ScanTechnic> scanTechnicCopy = new ArrayList<>();
		scanTechnicCopy= (ArrayList)scanTechnic.clone();
		
		ArrayList <BlockType> blockTypes =new ArrayList<>();
		blockTypes.add(BlockType.Scan);
		blockTypes.add(BlockType.Reserved);

		for (Scan scan : this.getCenter().getScans()) {
			if (scanTechnicCopy.contains(scan.getImageryTechnic())){
				Activity tmp = null;
				adequateMachineScan.add(scan);
				scanTechnicCopy.remove(scan.getImageryTechnic());
				if (patient.getPriority()== Priority.P3 || patient.getPriority()== Priority.P4){
					tmp=scan.getSchedule().getFirstAvailabilityNotWeekend(duration, BlockType.Scan, weekLowerBound, dayLowerBound, minuteLowerBound,weekUpperBound, dayUpperBound, minuteUpperBound);
				}
				else {

					tmp = scan.getSchedule().getFirstAvailabilityNotWeekend(duration, blockTypes, weekLowerBound, dayLowerBound, minuteLowerBound, weekUpperBound, dayUpperBound, minuteUpperBound);
					
				}
				if (best == null ||tmp.startsEarlierThan(best) ) {
					best = tmp;	
				}

			}

		}


		if (best!=null){
			Scan scan = (Scan) best.getResource();
			int start = (best.getWeek().getWeekId() == weekLowerBound && best.getDay().getDayId() == dayLowerBound) ? Math.max(minuteLowerBound, best.getStart()) : best.getStart();
			
			ActivityType type= ActivityType.CTSim;
			ActivityEvent event= new CTSim(patient);
			Activity CTSim = new Activity(start,duration,type,event);
			best.insert(CTSim);
			dateCTSim = CTSim.getDate();
		}
		else {
			Activity tmpFirstTreatment = planificationTreatment(patient,dateUpperBound);
			if((tmpFirstTreatment.getDate()).compareTo(patient.getDeadLine())==-1){
				TreatmentMachine treatmentMachine= (TreatmentMachine) firstTreatment.getResource() ;
				treatmentMachine.getSchedule().deleteFirstTreatmentAssociated(dateUpperBound);
				best = planificationCtSim( patient,tmpFirstTreatment);	

			}
			
			else{
				// si tu arrives là alors il faut annuler le premier traitement planifié tmpFirstTreatment
				TreatmentMachine treatmentMachine= (TreatmentMachine) tmpFirstTreatment.getResource() ;
				treatmentMachine.getSchedule().deleteFirstTreatmentAssociated(tmpFirstTreatment.getDate());
				for (Scan scan : this.getCenter().getScans()) {
					if (scanTechnicCopy.contains(scan.getImageryTechnic())){
						Activity tmp = null;
						adequateMachineScan.add(scan);
						scanTechnicCopy.remove(scan.getImageryTechnic());
						if (patient.getPriority()== Priority.P3 || patient.getPriority()== Priority.P4){
							tmp=scan.getSchedule().getFirstAvailabilityNotWeekendWithoutConstraint(duration, BlockType.Scan,weekUpperBound, dayUpperBound, minuteUpperBound);
						}
						else {

							tmp = scan.getSchedule().getFirstAvailabilityNotWeekendWithoutConstraint(duration, blockTypes, weekUpperBound, dayUpperBound, minuteUpperBound);
							
						}
						if (best == null ||tmp.startsEarlierThan(best) ) {
							best = tmp;	
						}
						
					}

				}
				
			}
		}
	
		return best ;
	}

	private Activity planificationTreatment(Patient patient, Date dateLowerBound) {

		boolean planned= false;
		int duration = 45;
		int weekLowerBound = dateLowerBound.getWeekId();
		int dayLowerBound = dateLowerBound.getDayId();
		int minuteLowerBound = dateLowerBound.getMinute();
		Activity best = null;
		ArrayList<TreatmentMachine> adequateMachine= new ArrayList<>();
		for (TreatmentMachine treatmentMachine : this.getCenter().getTreatmentMachines()) {
			if (treatmentMachine.getTreatmentTechnic()==patient.getTreatmentTechnic()) {
				Activity tmp = null;
				adequateMachine.add(treatmentMachine);
				if (patient.getPriority()== Priority.P1 || patient.getPriority()== Priority.P2){
					ArrayList <BlockType> blockTypes =new ArrayList<>();
					blockTypes.add(BlockType.Treatment);
					blockTypes.add(BlockType.Reserved);
					//tmp = treatmentMachine.getSchedule().getFirstAvailabilityNotFriday(duration, blockTypes, weekLowerBound, dayLowerBound, minuteLowerBound);
					tmp = treatmentMachine.getSchedule().getFirstAvailabilityNotWeekend(duration, BlockType.Treatment, weekLowerBound, dayLowerBound, minuteLowerBound);
				}
				else{
					tmp = treatmentMachine.getSchedule().getFirstAvailabilityNotWeekend(duration, BlockType.Treatment, weekLowerBound, dayLowerBound, minuteLowerBound);	
				}
				if (best == null ||tmp.startsEarlierThan(best) ) {
					best = tmp;	
				}
			

			}
		}
		
		if (best!=null && best.getDate().compareTo(patient.getDeadLine())==1){
			best=null;
			ArrayList <BlockType> blockTypes =new ArrayList<>();
			blockTypes.add(BlockType.Treatment);
			blockTypes.add(BlockType.Reserved);
			for (TreatmentMachine treatmentMachine : adequateMachine) {
				Activity tmp=null;
				if((patient.getPriority()== Priority.P1 || patient.getPriority()== Priority.P2)){
					tmp = treatmentMachine.getSchedule().getFirstAvailabilityFridayReserved(duration, blockTypes, dateLowerBound);
				}
				else {
					tmp = treatmentMachine.getSchedule().getFirstAvailabilityFriday(duration, BlockType.Treatment, dateLowerBound);
				}
				if (best == null ||tmp.startsEarlierThan(best) ) {
					best = tmp;	
				}
				
				
			}
	 	}
		
		if(best!=null){
			TreatmentMachine machine = (TreatmentMachine) best.getResource();
			int start = (best.getWeek().getWeekId() == weekLowerBound && best.getDay().getDayId() == dayLowerBound) ? Math.max(minuteLowerBound, best.getStart()) : best.getStart();
			
			ActivityType type= ActivityType.FirstTreatment;
			ActivityEvent event= new FirstTreatment(patient);
			Activity firstTreatment = new Activity(start,duration,type,event);
			best.insert(firstTreatment);
			Date date = firstTreatment.getDate();
			
			if(patient.getPriority() == Priority.P1 || patient.getPriority() == Priority.P2){
				System.out.println("le premier traitement schedulé : "+date+ " la priorité est : " +patient.getPriority());
			}
			boolean scheduled = false;
			if (patient.getPriority()==Priority.P3 || patient.getPriority()==Priority.P4){
				for (int i=1; i<patient.getNbTreatments();i++){
					date = date.increaseWeekend();
					scheduled = this.scheduleTreatment(patient, machine, date, duration, adequateMachine);
					planned = planned && scheduled;
					if(!scheduled){
						System.out.println("A treatment could not be scheduled");
					}
				}
			}
			else {
				for (int i=1; i<patient.getNbTreatments();i++){
					date=date.increase();
					scheduled = this.scheduleTreatment(patient, machine, date, duration, adequateMachine);
					planned = planned && scheduled;
					if(!scheduled){
						System.out.println("A treatment could not be scheduled, time: "+Time.time()+", patient id : "+patient.getId());
					}
				}
				
			}
		}
		else{
			best = null;
		}
		
		return best;
	}

	private boolean isBeforeDeadline(int remainingDaysTillDeadLine, int weekId, int dayId) {
		boolean res = false;
		int time = Time.time();
		int weekNow = Time.weekCorrespondingToTime(time);
		int dayNow = Time.dayCorrespondingToTime(time);
		int nbDays = Integer.MAX_VALUE;
		
		if(weekId==weekNow){
			nbDays = dayId-dayNow;
		}
		else if(weekId>weekNow){
			nbDays = 7-dayNow + 7*(weekId-weekNow-1) + dayId; // TODO check that the value is correct
		}
		if(remainingDaysTillDeadLine>=nbDays){
			res = true;
		}
		return res;
	}

	public ArrayList<TreatmentMachine> getTmachines() {
		return tmachines;
	}

	public void setTmachines(ArrayList<TreatmentMachine> tmachines) {
		this.tmachines = tmachines;
	}
	
	/**
	 * Depending on the technic needed for the patient, the technologist decides if a mold is needed
	 * @return
	 */
	public boolean moldNeeded(TreatmentTechnic technic){
		//TODO
		return true;
	}
	
	/**
	 * Processing the files should be done before the contouring block of the doctor
	 * Between the contouring and first treatment max 48 hours, that is checked 48 hours before the first treatment. It 48h before the 1st treatment the contouring
	 * is not done, then the 1st treatment and others are rescheduled
	 */
	public void processPatientFilesForPreContouring(){
		/*
		 * Depending on wether or not we model the schedule of a technologist
		 * we may use the PreContouring event (in the case if it is modeled)
		 * or just do the processing here of the file otherwise
		 */
		Collections.sort(filesForPreContouring, new FileComparator1()); //TODO choose the comparator to use for this step, maybe add an input to the method
		/*
		 * The contouring step varies between 0.5 and 2 hours for the doctor
		 */
		for (Patient patient : filesForPreContouring) {
			patient.getDoctor().getFilesForContouring().add(patient);
		}
	}

	public LinkedList<Patient> getFilesForPreContouring() {
		return filesForPreContouring;
	}

	public void setFilesForPreContouring(LinkedList<Patient> filesForPreContouring) {
		this.filesForPreContouring = filesForPreContouring;
	}

	public LinkedList<Patient> getFilesForCTSimTreatment() {
		return filesForPlanification;
	}

	public void setFilesForCTSimTreatment(LinkedList<Patient> filesForCTSimTreatment) {
		this.filesForPlanification = filesForCTSimTreatment;
	}
	
	
	
	
	public ArrayList<Scan> getScans() {
		return scans;
	}

	public void setScans(ArrayList<Scan> scans) {
		this.scans = scans;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	@Override
	public Week addWeek(int weekId) {
	return (this.getSchedule().addWeek(weekId));
	}

	public LevelTechnologist getLevel() {
		return level;
	}

	public void setLevel(LevelTechnologist level) {
		this.level = level;
	}
	
	public Activity findOvertimeFreeActivity(TreatmentMachine treatmentMachine, Date date, int duration){
		Activity freeAct = null;
		Day day = treatmentMachine.getSchedule().getDay(date.getWeekId(), date.getDayId());
		for (Block currentBlock : day.getBlocks()){
			if (currentBlock.getType() == BlockType.Treatment){
				int endActivty= currentBlock.getActivities().getLast().getEnd();
				int endBlock= currentBlock.getEnd();
				int elongationMinutes = duration- endBlock + endActivty;
				if(elongationMinutes+endActivty<19*60+30){
					freeAct = currentBlock.elongation(elongationMinutes);
					break;
				}
				
			}
		}
		return freeAct;
	}

	public boolean scheduleTreatment(Patient patient, TreatmentMachine machine, Date date, int duration, ArrayList<TreatmentMachine> adequateMachine){
		int start = date.getMinute();
		Activity freeAct = machine.getSchedule().findFreeActivityToInsertOtherActivity(date, duration);
		if (freeAct == null){
			for (TreatmentMachine treatmentMachine : adequateMachine) {
				freeAct = treatmentMachine.getSchedule().findFreeActivityToInsertOtherActivity(date, duration);
				if(freeAct!=null){
					break;
				}
			}
			if (freeAct == null){
				freeAct = machine.getSchedule().getFirstAvailabilityInDay(duration,BlockType.Treatment, date); //this freeAct is obviously more than a day in the future, so we can schedule the treatment starting at the same time as freeAct
				if (freeAct == null){
					for (TreatmentMachine treatmentMachine : adequateMachine) {
						freeAct = treatmentMachine.getSchedule().getFirstAvailabilityInDay(duration, BlockType.Treatment, date);
					}
					if (freeAct == null){
						freeAct = this.findOvertimeFreeActivity(machine, date, duration);
						if(freeAct==null){
							for (TreatmentMachine treatmentMachine : adequateMachine) {
								freeAct = this.findOvertimeFreeActivity(treatmentMachine, date, duration);
							}
							if (freeAct==null){
								//TODO treat those cases
								//System.out.println("issue");
								return false;
							}
							
						}
					}
				}
				start = freeAct.getStart();
			}

		}
		Activity treatment= new Activity(start, duration, ActivityType.Treatment, new Treatment (patient));
		freeAct.insert(treatment);
		return true;
	}
}
