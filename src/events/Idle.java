package events;

public class Idle extends ActivityEvent{

	public Idle() {
		super();
	}

	@Override
	public void childActions() {
		//System.out.println("Someone does nothing");
		getSchedule().doNextTask();
	}

	@Override
	public ActivityEvent clone() {
		Idle clone = new Idle();
		clone.setActivity(getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		this.delay = this.getActivity().duration();
	}

}
