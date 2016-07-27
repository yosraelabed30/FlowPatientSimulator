package scheduling;

import java.util.ArrayList;

import medical.Center;
import medical.Resource;
import tools.Time;
/**
 * Day of a Week, in the Schedule
 * @author Joffrey
 *
 */
public class Day implements Comparable<Day>{
	/**
	 * Week to which the Day is linked
	 */
	private Week week;
	/**
	 * id of the day, between 0 and 6;
	 */
	private int dayId;
	/**
	 * quotas of the day for the consultation
	 */
	private int quotasConsultation;
	/**
	 * List of blocks of the day
	 */
	private ArrayList<Block> blocks;

	public Day(Week week, int dayId, int quotasConsultation,
			ArrayList<Block> blocks) {
		this.week = week;
		this.dayId = dayId;
		this.quotasConsultation = quotasConsultation;
		this.blocks = blocks;
		if(blocks!=null){
			for (Block block : blocks) {
				if(block!=null){
					block.setDay(this);
				}
			}
		}
	}

	public Day(Week week, int dayId, ArrayList<Block> blocks) {
		this(week, dayId, getQuotasConsultationForThatWeekDay(dayId), blocks);
	}

	public Day(Week week, int dayId) {
		this(week, dayId, getQuotasConsultationForThatWeekDay(dayId),
				new ArrayList<Block>());
	}

	/**
	 * deep copy of this day, the layer of schedule under the day (blocks) is also clones linked to the newly created clone day.
	 * The clone day is however linked to the same week as the original day.
	 */
	public Day clone(){
		Day clone = new Day(this.week, this.dayId);
		for (Block block : blocks) {
			block.setDay(clone);
			clone.getBlocks().add(block.clone());
			block.setDay(this);
		}
		return clone;
	}
	
	private static int getQuotasConsultationForThatWeekDay(int id) {
		int qC = -1;
		int weekDay = Time.fromIdToWeekDay(id);
		switch (weekDay) {
		case 0:
			qC = 1;
			break;
		case 1:
			qC = 8;
			break;
		case 2:
			qC = 1;
			break;
		case 3:
			qC = 7;
			break;
		case 4:
			qC = 1;
			break;
		default:
			qC = 0;
			break;
		}
		return qC;
	}
	
	public void decreaseQuotasConsultation(){
		this.quotasConsultation--;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Day other = (Day) obj;
		if (dayId != other.dayId)
			return false;
		return true;
	}
	
	/*
	 * Getters and setters
	 */
	
	public int getQuotasConsultation() {
		return quotasConsultation;
	}

	public int getDayId() {
		return dayId;
	}

	public void setDayId(int id) {
		this.dayId = id;
	}

	public void setQuotasConsultation(int quotasConsultation) {
		this.quotasConsultation = quotasConsultation;
	}


	public boolean areConsultationsQuotasNotReached() {
		return quotasConsultation>0;
	}


	@Override
	public int compareTo(Day d) {
		int res;
		if(this.dayId < d.dayId){
			res = -1;
		}
		else if(this.dayId == d.dayId){
			res = 0;
		}
		else{
			res = 1;
		}
		return res;
	}

	public ArrayList<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(ArrayList<Block> blocks) {
		this.blocks = blocks;
	}
	
	public Block getBlock(int blockId){
		return this.getBlocks().get(blockId);
	}
	
	public void setBlock(int blockId, Block block){
		this.getBlocks().set(blockId, block);
	}

	public Week getWeek() {
		return week;
	}

	public void setWeek(Week week) {
		this.week = week;
	}

	public Schedule getSchedule() {
		Schedule res = null;
		if(this.getWeek()!=null){
			res = this.getWeek().getSchedule();
		}
		return res;
	}
	
	public ISchedule getiSchedule() {
		ISchedule res = null;
		if(this.getSchedule()!=null){
			res = this.getSchedule().getiSchedule();
		}
		return res;
	}




	
}
