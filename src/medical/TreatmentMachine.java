package medical;

import java.util.ArrayList;

import scheduling.Block;
import scheduling.ISchedule;
import scheduling.Schedule;
import scheduling.Week;


public class TreatmentMachine extends Resource implements ISchedule{
	private Schedule schedule;
	private TreatmentTechnic treatmentTechnic;
	
	public TreatmentMachine(Center center, TreatmentTechnic treatmentTechnic, ArrayList<ArrayList<Block>> blocksTab){
		super(center);
		this.treatmentTechnic = treatmentTechnic;
		this.schedule = new Schedule(this);
		for(int i=0;i<7;i++){
			for (Block block : blocksTab.get(i)) {
				block.setDay(getSchedule().getDefaultWeek().getDay(i));
			}
			getSchedule().getDefaultWeek().getDay(i).setBlocks(blocksTab.get(i));
		}
	}
	
	public TreatmentTechnic getTreatmentTechnic() {
		return treatmentTechnic;
	}

	public void setTreatmentTechnic(TreatmentTechnic treatmentTechnic) {
		this.treatmentTechnic = treatmentTechnic;
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

}
