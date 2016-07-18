package medical;

import java.util.ArrayList;

import javax.swing.event.SwingPropertyChangeSupport;

import scheduling.Activity;
import scheduling.Block;
import scheduling.BlockType;
import scheduling.Date;
import scheduling.ISchedule;
import scheduling.Schedule;
import scheduling.Week;
import tools.Time;
import umontreal.iro.lecuyer.simevents.Sim;

/**
 * 
 * @author Joffrey
 * Represents a patient in the system
 */
public class Patient extends Resource implements ISchedule{
	/**
	 * static variable to keep track of the patients ids
	 * at first patientClassId is equal to 0
	 * when the first patient arrives, its id is set to patientClassId (=0)
	 * then patientClassId is increased, so that when the next patient arrives, its id will be equal to patientClassId (=1)
	 * and so on...
	 */
	static private int staticPatientId;
	/**
	 * patient's unique identifier
	 */
	private int id;
	/**
	 * int identifying the patient's cancer
	 */
	private int cancer;
	/**
	 * True if the patient is curative, False if palliative
	 */
	private boolean curative;
	/**
	 * id of the day on which the patient arrived
	 */
	private int arrivalDay;
	/**
	 * number of minutes into the day at which the patient arrived
	 */
	private int arrivalMinutes;
	/**
	 * doctor treating the patient
	 */
	private Doctor doctor;
	/**
	 * the next activity to come in the schedule
	 */
	private Activity nextActivity;
	/**
	 * if the patient referred is an emergency or not
	 */
	private boolean emergency;
	/**
	 * whether the patient is present or not in the center
	 */
	private boolean present;
	/**
	 * whether the patient needs to be treated or not
	 */
	private boolean out;
	/**
	 * 1 = P1
	 * 2 = P2, etc
	 */
	private Priority priority;
	
	private MachineType machineType;
	
	/**
	 * TODO recheck for improvements, scheduled during planif and used after
	 */
	private Activity firstTreatment;
	private Activity otherTreatments;
	private Activity CTSim;
	
	private TreatmentTechnic treatmentTechnic;
	private boolean moldNeeded;
	
	private ArrayList<ScanTechnic> imageryTechnics;
	
	private int nbTreatments;
	
	
	private Schedule schedule;
	private ArrayList<Activity> steps;
	private Date referredDate;
	



	/**
	 * id fo the center to which the patient is affiliated
	 */
	private int centerId;

	public Patient(Center center){
		super(center);
		this.id = staticPatientId++;
		this.cancer = (int) (Math.random()*10);
		this.curative = Math.random()>0.5 ? true : false;
		this.arrivalDay = -10;
		this.arrivalMinutes =-10;
		this.steps = new ArrayList<>();
		
		this.doctor = null;
		this.setNextActivity(null);
		//TODO change the emergency 
		double rd = Math.random();
		if (rd<=0.2){
			this.setEmergency(true);
		}
		else{
			this.setEmergency(false);
		}
		this.present = false; //TODO change that
		
		int prio = (int)(Math.random()*4+1);
		switch (prio) {
		case 1:
			this.setPriority(Priority.P1);
			break;
		case 2 :
			this.setPriority(Priority.P2);
			break;
		case 3 :
			this.setPriority(Priority.P3);
			break;
		case 4 :
			this.setPriority(Priority.P4);
			break;
		default:
			System.out.println("Issue : the patient priority at its creation");
			break;
		}
		
		/*
		 * The schedule
		 */
		this.setSchedule(new Schedule(this));
		//TODO week constructor using arraylist of arraylist of blocks
		ArrayList<ArrayList<Block>>blocksTab = new ArrayList<ArrayList<Block>>(7);
		for(int i=0;i<7;i++){
			ArrayList<Block> blocks = new ArrayList<>();
			blocks.add(new Block(0, 0*60, 23*60-1, BlockType.All));
			blocksTab.add(blocks);
		}
		for(int i=0;i<7;i++){
			for (Block block : blocksTab.get(i)) {
				block.setDay(getSchedule().getDefaultWeek().getDay(i));
			}
			getSchedule().getDefaultWeek().getDay(i).setBlocks(blocksTab.get(i));
		}
		
		
		int time = (int)Sim.time();
		int weekId = Time.weekCorrespondingToTime(time);
		this.addWeek(weekId);
		
		this.referredDate = Date.dateNow();
	}
	
	public ArrayList<Doctor> doctorsTreatingPatientCancer(ArrayList<Doctor> doctors) {
		ArrayList<Doctor> competentDoctors = new ArrayList<>();
		for (Doctor doctor : doctors) {
			if(doctor.canTreat(this)){
				competentDoctors.add(doctor);
			}
		}
		return competentDoctors;
	}



	public static int getPatientClassId() {
		return staticPatientId;
	}



	public static void setPatientClassId(int patientClassId) {
		Patient.staticPatientId = patientClassId;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public int getCancer() {
		return cancer;
	}



	public void setCancer(int cancer) {
		this.cancer = cancer;
	}



	public boolean isCurative() {
		return curative;
	}



	public void setCurative(boolean curative) {
		this.curative = curative;
	}


	public Doctor getDoctor() {
		return doctor;
	}



	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}



	public int getArrivalDay() {
		return arrivalDay;
	}



	public void setArrivalDay(int arrivalDay) {
		this.arrivalDay = arrivalDay;
	}



	public int getArrivalMinutes() {
		return arrivalMinutes;
	}



	public void setArrivalMinutes(int arrivalMinutes) {
		this.arrivalMinutes = arrivalMinutes;
	}



	public Activity getNextActivity() {
		return nextActivity;
	}



	public void setNextActivity(Activity nextActivity) {
		this.nextActivity = nextActivity;
	}

	public boolean newlyReferred() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEmergency() {
		return emergency;
	}

	public void setEmergency(boolean emergency) {
		this.emergency = emergency;
	}

	public boolean isPresent() {
		return present;
	}

	public void setPresent(boolean present) {
		this.present = present;
	}

	public boolean isOut() {
		return out;
	}

	public void setOut(boolean out) {
		this.out = out;
	}

	@Override
	public Week addWeek(int weekId){
		Week res = null;
		if(this.isOut()){
			//System.out.println("Patient is already out, no need to add a week to its schedule");
		}
		else{
			res = getSchedule().addWeek(weekId);
		}
		return res;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public int remainingDaysTillDeadLine() {
		int dayReferred = this.getReferredDate().getWeekId()*7+this.getReferredDate().getDayId();
		int time = (int)Sim.time();
		int deadLine = dayReferred + priority.getDelay();
		int today = Time.dayCorrespondingToTime(time);
		return deadLine-today;
	}


	public MachineType getMachineType() {
		return machineType;
	}

	public void setMachineType(MachineType machineType) {
		this.machineType = machineType;
	}

	public TreatmentTechnic getTreatmentTechnic() {
		return treatmentTechnic;
	}

	public void setTreatmentTechnic(TreatmentTechnic technic) {
		this.treatmentTechnic = technic;
	}

	public boolean isMoldNeeded() {
		return moldNeeded;
	}

	public void setMoldNeeded(boolean moldNeeded) {
		this.moldNeeded = moldNeeded;
	}

	public ArrayList<ScanTechnic> getImageryTechnics() {
		return imageryTechnics;
	}

	public void setImageryTechnics(ArrayList<ScanTechnic> imageryTechnics) {
		this.imageryTechnics = imageryTechnics;
	}

	public int getNbTreatments() {
		return nbTreatments;
	}

	public void setNbTreatments(int nbTreatments) {
		this.nbTreatments = nbTreatments;
	}

	public int getCenterId() {
		return centerId;
	}

	public void setCenterId(int centerId) {
		this.centerId = centerId;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public ArrayList<Activity> getSteps() {
		return steps;
	}

	public void setSteps(ArrayList<Activity> steps) {
		this.steps = steps;
	}

	public Date getDeadLine() {
		
		int minDeadLine=this.getReferredDate().toMinutes();
		switch (this.getPriority()) {
		case P1:
			minDeadLine=minDeadLine+24*60;
			break;
		case P2:
			minDeadLine=minDeadLine+3*24*60;
			break;
		case P3:
			minDeadLine=minDeadLine+14*24*60;
			break;
		case P4:
			minDeadLine=minDeadLine+28*24*60;
			break;
		default:
			break;
		}
		Date deadLine= Date.toDates(minDeadLine);
		return deadLine;
	}

	public Date getReferredDate() {
		return referredDate;
	}

	public void setReferredDate(Date referredDate) {
		this.referredDate = referredDate;
	}

	public int getDelaysFirstTreatmentCTSim() {
		
		int businessDays = 6;
		switch (this.getPriority()) {
		case P1:
			businessDays=0;
			break;
		case P2:
			businessDays=2;
			break;

		default:

			break;
		}
		
		return businessDays;
	}



}
