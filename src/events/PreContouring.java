package events;

public class PreContouring extends ActivityEvent{
	//TODO add the technologist

	public PreContouring() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void childActions() {
		// TODO the technologist do his stuff and once he is done, he sends the contouring files to the doctors
		// the doctor has a contouring files waitlist
		// during the precontouring the technologist sets the activities of contouring of the doctors, which take some time (random)
	}

	@Override
	public ActivityEvent clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void generateDelay() {
		// TODO Auto-generated method stub
		
	}

}
