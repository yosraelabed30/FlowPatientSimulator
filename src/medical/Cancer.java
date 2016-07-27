package medical;

public enum Cancer {

	poumon(0),
	sein(1),
	cerveau(2),
	peau(3);
	private int id;
	
	Cancer (int id){
		this.setId(id);
		
	}
	
	public static Cancer getCancer(int id){
		for (Cancer cancer : Cancer.values()) {
			if (cancer.getId()==id){
				return cancer;
			}
		}
		return null;
	}
	
	public static Cancer generateCancer(){
		int length = Cancer.values().length;
		int index = (int) (Math.random()*length);
		Cancer cancer = getCancer(index);
		if(cancer==null){
			System.out.println("qsd");
		}
		return cancer;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
