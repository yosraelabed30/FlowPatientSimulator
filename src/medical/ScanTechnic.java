package medical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public enum ScanTechnic {
	CTScan3D(0),
	CTScan4D(1),
	IRM(2),
	DEST3D(3),
	DEST4D(4),
	TEP(5);
	private int index ;
	private ScanTechnic( int index ) {
		this.setIndex(index) ;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public static ScanTechnic getScanTechnic(int index){

		return ScanTechnic.values()[index];
	}
	
	public static ArrayList<ScanTechnic> generateScanTechnic(int nbOfTechnics){

		ArrayList<ScanTechnic> list = new ArrayList<ScanTechnic>(Arrays.asList(ScanTechnic.values()));
		Collections.shuffle(list);
		return (ArrayList<ScanTechnic>) list.subList(0, nbOfTechnics);
	}
}
