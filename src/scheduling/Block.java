package scheduling;

import java.util.LinkedList;

import tools.Time;
import medical.Center;

public class Block implements Comparable<Block>{
	/**
	 * day linked to this block
	 */
	private Day day;
	/**
	 * block id which is the block number in the associated day (it is the 1st block of the day then it is block id 0)
	 */
	private int blockId;
	/**
	 * start, in minutes between 0 and 24*60-1 included
	 */
	private int start;
	/**
	 * end, in minutes between 0 and 24*60-1 included
	 */
	private int end;
	/**
	 * Type of the block, see BlockType class, some activities can only be scheduled during blocks with a particular blocktype
	 */
	private BlockType blockType;
	/**
	 * List of activities
	 */
	private LinkedList<Activity> activities;
	
	public Block(int blockId, int start, int end, BlockType blockType){
		this(null, blockId, start, end, blockType);
	}
	
	public Block(int blockId, int start, int end, BlockType blockType,
			LinkedList<Activity> activities) {
		this(null, blockId, start, end, blockType, activities);
	}
	
	
	public Block(Day day, int blockId, int start,
			int end, BlockType blockType, LinkedList<Activity> activities) {
		super();
		this.day = day;
		this.setBlockId(blockId);
		this.start = start;
		this.end = end;
		this.blockType = blockType;
		this.activities = activities;
		if(activities!=null){
			for (Activity activity : activities) {
				if(activity!=null){
					activity.setBlock(this);
				}
			}
		}
	}
	
	public Block(Day day, int blockId, int start,
			int end, BlockType blockType) {
		this.day = day;
		this.blockId = blockId;
		this.start = start;
		this.end = end;
		this.blockType = blockType;
		LinkedList<Activity> acts = new LinkedList<>();
		acts.add(new Activity(this, start, end));
		this.activities = acts;
	}

	public LinkedList<Activity> getActivities() {
		return activities;
	}
	public void setActivities(LinkedList<Activity> activities) {
		this.activities = activities;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public BlockType getType() {
		return blockType;
	}
	public void setType(BlockType type) {
		this.blockType = type;
	}
	public Activity getActivity(int activityId){
		Activity res = null;
		for (Activity activity : activities) {
			if(activity.getActivityId()==activityId){
				res = activity;
				break;
			}
		}
		return res;
	}
	
	/**
	 * deep copy of this block, the layer of schedule under the block (activities) is also clones linked to the newly created clone block.
	 * The clone block is however linked to the same day as the original block.
	 */
	public Block clone(){
		Block clone = new Block(this.getDay(), this.getBlockId(), this.start, this.end, this.blockType);
		clone.activities = new LinkedList<>();
		for (Activity activity : activities) {
			activity.setBlock(clone);
			clone.activities.add(activity.clone());
			activity.setBlock(this);
		}
		return clone;
	}

	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public Schedule getSchedule() {
		Schedule res = null;
		if(this.getWeek()!=null){
			res = this.getWeek().getSchedule();
		}
		return res;
	}
	
	public Week getWeek(){
		Week res = null;
		if(this.getDay()!=null){
			res = this.getDay().getWeek();
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
	

	@Override
	public int compareTo(Block b) {
		int res;
		if(this.blockId < b.blockId){
			if(this.start>=b.start){
				System.out.println("Problem : this block has an id inferior to block b, yet this block starts after block b");
			}
			res = -1;
		}
		else if(this.blockId == b.blockId){
			res = 0;
		}
		else{
			if(this.start<=b.start){
				System.out.println("Problem : this block has an id superior to block b, yet this block starts before block b");
			}
			res = 1;
		}
		return res;
	}
	
	public int duration(){
		return Time.duration(this.getStart(), this.getEnd());
	}
}
