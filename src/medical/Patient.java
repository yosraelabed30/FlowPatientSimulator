package medical;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.event.SwingPropertyChangeSupport;

import scheduling.Activity;
import scheduling.ActivityType;
import scheduling.Block;
import scheduling.BlockType;
import scheduling.Date;
import scheduling.ISchedule;
import scheduling.Schedule;
import scheduling.Week;
import tools.Time;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.randvar.UniformGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.simevents.Sim;

/**
 * 
 * @author Joffrey
 * Represents a patient in the system
 */
public class Patient  implements ISchedule{
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
	private Cancer cancer;
	/**
	 * doctor treating the patient
	 */
	private Doctor doctor;
	/**
	 * whether the patient is present or not in the center
	 */
	private boolean presentInCenter;
	/**
	 * whether the patient needs to be treated or not
	 */
	private boolean out;
	private MachineType machineType;
	
	/**
	 * TODO recheck for improvements, scheduled during planif and used after
	 */
	private TreatmentTechnic treatmentTechnic;
	private ArrayList<ScanTechnic> scanTechnics;
	private int nbTreatments;
	private Schedule schedule;
	private LinkedList<Activity> steps;
	private LinkedList<Activity> plannedStepsTreatments;
	private LinkedList<Activity> plannedStepsPreTreatment;
	private Date referredDate;
	private Sphere sphere;
	private Priority priority;

	public Patient(Center center){
		this.id = staticPatientId++;
		this.cancer= Cancer.generateCancer();
		this.steps = new LinkedList<>();
		this.doctor = null;
		this.presentInCenter = false;
		this.priority = Priority.generatePriority();
		/*
		 * The schedule
		 */
		this.setSchedule(new Schedule(this));
		//TODO week constructor using arraylist of arraylist of blocks
		ArrayList<ArrayList<Block>>blocksTab = new ArrayList<ArrayList<Block>>(7);
		for(int i=0;i<7;i++){
			ArrayList<Block> blocks = new ArrayList<>();
			blocks.add(new Block(0, 0*60, 24*60-1, BlockType.All));
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
		
		this.referredDate = Date.now();

		for (Sphere sphere : center.getSpheres()) {
			if (sphere.getCancer()==this.getCancer()) {
				this.sphere=sphere;
			}
		}
		this.setPlannedStepsTreatments(new LinkedList<Activity>());
		this.plannedStepsPreTreatment =new LinkedList<Activity>();
		this.steps = new LinkedList<>();
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

	public Cancer getCancer() {
		return cancer;
	}

	public void setCancer(Cancer cancer) {
		this.cancer = cancer;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public boolean isPresentInCenter() {
		return presentInCenter;
	}

	public void setPresentInCenter(boolean presentInCenter) {
		this.presentInCenter = presentInCenter;
	}

	public boolean isOut() {
		return out;
	}

	public void setOut(boolean out) {
		this.out = out;
	}
	
	public void toPatientsOut(){
		this.getSphere().getCenter().toPatientsOut(this);
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

	public ArrayList<ScanTechnic> getScanTechnics() {
		return scanTechnics;
	}

	public void setScanTechnics(ArrayList<ScanTechnic> scanTechnics) {
		this.scanTechnics = scanTechnics;
	}

	public int getNbTreatments() {
		return nbTreatments;
	}

	public void setNbTreatments(int nbTreatments) {
		this.nbTreatments = nbTreatments;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public LinkedList<Activity> getSteps() {
		return steps;
	}
	
	public Activity getLastStep(){
		
		return steps.get(getSteps().size());

	}

	public void setSteps(LinkedList<Activity> steps) {
		this.steps = steps;
	}

	/**
	 * 
	 * @return the deadline (as a Date) before which this patient should start its first treatment
	 */
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

	/**
	 * 
	 * @return the maximum number of days recommended between the CTSim and the first treatment
	 */
	public int delaysInDaysFirstTreatmentCTSim() {
		
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

	public Sphere getSphere() {
		return sphere;
	}

	public void setSphere(Sphere sphere) {
		this.sphere = sphere;
	}

	public LinkedList<Activity> getPlannedStepsPreTreatment() {
		return plannedStepsPreTreatment;
	}

	public void setPlannedStepsPreTreatment(LinkedList<Activity> plannedStepsPreTreatment) {
		this.plannedStepsPreTreatment = plannedStepsPreTreatment;
	}

	public LinkedList<Activity> getPlannedStepsTreatments() {
		return plannedStepsTreatments;
	}

	public void setPlannedStepsTreatments(LinkedList<Activity> plannedStepsTreatments) {
		this.plannedStepsTreatments = plannedStepsTreatments;
	}

	public String toString(){
		String s = "Patient id : "+this.getId()+", priority : "+this.getPriority()+" ";
		s+="\nReferred, "+this.getReferredDate();
		
		int nbMin=-1;
		boolean firstTreatment = true;
		for (Activity activity : this.getSteps()) {
			s+="\n"+activity.getType()+", "+activity.getDate();
			if(activity.getType()==ActivityType.Treatment && firstTreatment){
				firstTreatment=false;
				nbMin = activity.getDate().toMinutes()-this.getReferredDate().toMinutes();
			}
		}

		s+="\nDuration between the consultation and the first treatment : "+Date.toDates(nbMin);
		return s;
	}
}
