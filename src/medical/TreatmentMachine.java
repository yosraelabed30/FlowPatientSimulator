package medical;

import java.util.ArrayList;

import scheduling.Block;
import scheduling.ISchedule;
import scheduling.Schedule;
import scheduling.Week;


public class TreatmentMachine implements ISchedule{
	private Center center;
	static private int treatmentMachineClassId;
	private int id;
	private Schedule schedule;
	private ArrayList < TreatmentTechnic> treatmentTechnics;
	
	public TreatmentMachine(Center center, ArrayList < TreatmentTechnic> treatmentTechnics, ArrayList<ArrayList<Block>> blocksTab){
		super();
		this.id = treatmentMachineClassId++;
		this.setCenter(center);
		this.setTreatmentTechnics(treatmentTechnics);
		this.schedule = new Schedule(this);
		for(int i=0;i<7;i++){
			for (Block block : blocksTab.get(i)) {
				block.setDay(getSchedule().getDefaultWeek().getDay(i));
			}
			getSchedule().getDefaultWeek().getDay(i).setBlocks(blocksTab.get(i));
		}
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

	public ArrayList < TreatmentTechnic> getTreatmentTechnics() {
		return treatmentTechnics;
	}

	public void setTreatmentTechnics(ArrayList < TreatmentTechnic> treatmentTechnics) {
		this.treatmentTechnics = treatmentTechnics;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

}
