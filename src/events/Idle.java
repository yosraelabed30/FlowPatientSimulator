package events;

import scheduling.Schedule;
import tools.Time;
import umontreal.iro.lecuyer.simevents.Event;

public class Idle extends ActivityEvent{

	private Event end;
	
	public Idle() {
		super();
		this.setEnd(new EndIdle());
	}

	@Override
	public void childActions() {
//		if(this.getSchedule()==null){
//			int now = Time.minIntoTheDay(Time.time());
//			System.out.println("time : "+now+", activity id : "+this.getActivity().getActivityId());
//		}
		this.getSchedule().setCurrentActivityBeingDone(this.getActivity());
		getEnd().schedule(delay);
		int now = Time.minIntoTheDay(Time.time());
//		System.out.println("Idle : Time : "+now+", end supposedly at : "+(delay+now)+", activity id : "+this.getActivity().getActivityId());
	}
	
	/**
	 * The line " clone.setActivity(this.getActivity()); " is compulsory in any clone method
	 */
	@Override
	public ActivityEvent clone() {
		Idle clone = new Idle();
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		int now = Time.time();
		int min = Time.minIntoTheDay(now);
		this.delay = Math.max(0, Math.max(min, this.getActivity().getEnd())-min);

//		System.out.println("Idle generateDelay : Time : "+min+", end supposedly at : "+(delay+min)+", activity id : "+this.getActivity().getActivityId());
	}
	
	public Event getEnd() {
		return end;
	}

	public void setEnd(Event end) {
		this.end = end;
	}

	class EndIdle extends Event {
		
		public EndIdle(){
			super();
			this.setPriority(0); // for instance if a doctor finishes his task at the same time a patient arrives, the doctor should be able to take care of the patient
		}
		
		@Override
		public void actions() {
			Schedule s = getSchedule();
//			if(s==null){
//				System.out.println("EndIdle ; time : "+Time.minIntoTheDay(Time.time())+", activity id : "+getActivity().getActivityId());
//			}
			getSchedule().setCurrentActivityBeingDone(null);
			s.doNextTask();
		}
		
	}
}
