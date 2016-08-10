package medical;


import java.util.ArrayList;
import java.util.LinkedList;

import scheduling.Block;
import scheduling.ISchedule;
import scheduling.Schedule;
import scheduling.Week;

public class Dosimetrist implements ISchedule{
	static private int dosimetristClassId=0;
	private int id;
	private Center center;
	private Schedule schedule;
	private static LinkedList<Patient> filesForDosi= new LinkedList<Patient>();
	private static LinkedList<Patient> filesForVerif= new LinkedList<Patient>();
	
	public Dosimetrist(Center center, ArrayList<ArrayList<Block>> blocksTab) {
		super();
		this.id = dosimetristClassId++;
		this.setCenter(center);
		this.setSchedule(new Schedule(this));
		for (int i = 0; i < 7; i++) {
			for (Block block : blocksTab.get(i)) {
				block.setDay(getSchedule().getDefaultWeek().getDay(i));
			}
			getSchedule().getDefaultWeek().getDay(i).setBlocks(blocksTab.get(i));
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static LinkedList<Patient> getFilesForDosi() {
		return filesForDosi;
	}

	public void setFilesForDosi(LinkedList<Patient> filesForDosi) {
		Dosimetrist.filesForDosi = filesForDosi;
	}

	public static LinkedList<Patient> getFilesForVerif() {
		return filesForVerif;
	}

	public static void setFilesForVerif(LinkedList<Patient> filesForVerif) {
		Dosimetrist.filesForVerif = filesForVerif;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
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
