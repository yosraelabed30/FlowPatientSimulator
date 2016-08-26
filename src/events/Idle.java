package events;

import scheduling.Schedule;
import tools.Time;
import umontreal.iro.lecuyer.simevents.Event;

public class Idle extends ActivityEvent{

//	private Event end;
	
	public Idle() {
		super();
//		this.setEnd(new EndIdle());
	}

	@Override
	public void endActions() {
//		this.getSchedule().setCurrentActivityBeingDone(this.getActivity());
//		getEnd().schedule(delay);
//		int now = Time.minIntoTheDay(Time.now());
//		System.out.println("Idle : Time : "+now+", activity id : "+this.getActivity().getActivityId());
		Schedule s = getSchedule();
		s.doNextTask();
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
		int now = Time.now();
		int min = Time.minIntoTheDay(now);
		this.delay = Math.max(0, Time.duration(min, Math.max(min, this.getActivity().getEnd())));
	}

	@Override
	public void startActions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean conditions() {
		return true;
	}
	
//	public Event getEnd() {
//		return end;
//	}
//
//	public void setEnd(Event end) {
//		this.end = end;
//	}

//	class EndIdle extends Event {
//		
//		public EndIdle(){
//			super();
//			this.setPriority(0); // for instance if a doctor finishes his task at the same time a patient arrives, the doctor should be able to take care of the patient
//		}
//		
//		@Override
//		public void actions() {
//			Schedule s = getSchedule();
//			getSchedule().setCurrentActivityBeingDone(null);
//			s.doNextTask();
//		}
//		
//	}
}
