package scheduling;

public class Availability implements Comparable<Availability>{
	private Activity activity;
	private int start;
	private int end;
	
	public Availability(Activity activity, int start, int end) {
		super();
		this.setActivity(activity);
		this.setStart(start);
		this.setEnd(end);
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	@Override
	public int compareTo(Availability a) {
		int res = 0;
		int thisMinutes = this.getActivity().getDate().toMinutes()+(start-this.getActivity().getStart());
		int aMinutes = a.getActivity().getDate().toMinutes()+(a.getStart()-a.getActivity().getStart());
		if(thisMinutes<aMinutes){
			res = -1;
		}
		else if(thisMinutes>aMinutes){
			res=1;
		}
		return res;
	}
	
	public Date getDate(){
		Date date = this.getActivity().getDate();
		date.setMinute(start);
		return date;
	}
}
