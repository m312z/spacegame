package utils;


import java.util.Comparator;

public class IntegerArrayComparitor implements Comparator<Integer[]> {

	@Override
	public int compare(Integer[] o1, Integer[] o2) {
		if(o1.length>o2.length)
			return -1;
		if(o2.length>o1.length)
			return 1;
		for(int i=0;i<o1.length;i++) {
			if(o1[i]>o2[i])
				return -1;
			if(o1[i]<o2[i])
				return 1;
		}
		return 0;
	}

	private static IntegerArrayComparitor singleton;
	
	public static IntegerArrayComparitor getInstance() {
		if(singleton==null)
			singleton = new IntegerArrayComparitor(); 
		return singleton;

	}
	
}
