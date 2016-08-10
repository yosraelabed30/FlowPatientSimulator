package medical;

import java.util.ArrayList;

import scheduling.Block;
import scheduling.ISchedule;
import scheduling.Schedule;
import scheduling.Week;
import tools.Time;
import umontreal.iro.lecuyer.simevents.Sim;


//TODO create blocks for the scan during which they are reserved for P1 patient (not sure)

public class Scan implements ISchedule{
	static private int ctscanClassId;
	private int id;
	private Center center;
	private Schedule schedule;
	private boolean forCurative;
	private ScanTechnic imageryTechnic;
	
	/**
	 * Constructor for when it does not matter what kind of ctscan it is
	 */
	public Scan(Center center, boolean forCurative, ScanTechnic imageryTechnic, ArrayList<ArrayList<Block>> blocksTab){
		super();
		this.center = center;
		this.schedule = new Schedule(this);
		this.id = ctscanClassId++;
		this.forCurative = forCurative;
		this.imageryTechnic = imageryTechnic;
		this.schedule = new Schedule(this);
		for(int i=0;i<7;i++){
			for (Block block : blocksTab.get(i)) {
				block.setDay(getSchedule().getDefaultWeek().getDay(i));
			}
			getSchedule().getDefaultWeek().getDay(i).setBlocks(blocksTab.get(i));
		}
	}
	
	public boolean canTreat(Patient patient) {
		return patient.isCurative()==forCurative;
	}
	
	/*
	 * Getters and setters
	 */
	public static int getCtscanClassId() {
		return ctscanClassId;
	}

	public static void setCtscanClassId(int ctscanClassId) {
		Scan.ctscanClassId = ctscanClassId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isForCurative() {
		return forCurative;
	}

	public void setForCurative(boolean forCurative) {
		this.forCurative = forCurative;
	}

	public ScanTechnic getImageryTechnic() {
		return imageryTechnic;
	}

	public void setImageryTechnic(ScanTechnic imageryTechnic) {
		this.imageryTechnic = imageryTechnic;
	}

	@Override
	public Week addWeek(int weekId) {
		return getSchedule().addWeek(weekId);
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

}
