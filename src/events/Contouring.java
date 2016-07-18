package events;

public class Contouring extends ActivityEvent{

	public Contouring() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void childActions() {
		// TODO send the patients files to the dosimetrist which will process the files, it takes an amount of time which follow a probability law, then only the 
		// TODO schedule random activities of dosimetry approbations during the blocks Plan de traitement
		
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
