package utils;


public class StringParser {

	public static String getBracket(String line) {
		int start = line.indexOf('{');
		if(start<0)
			return line;
		int count = 1;
		for(int i=start+1;i<line.length();i++) {
			if(line.charAt(i)=='{')
				count++;
			if(line.charAt(i)=='}')
				count--;
			if(count==0)
				return line.substring(start+1,i);
		}
		return "";
	}
	
	public static Integer[] readIntegerArray(String line) {
		String[] s = line.split("/");
		return new Integer[] {
				Integer.parseInt(s[0]),
				Integer.parseInt(s[1])
		};
	}

	public static float[] readFloatArray(String line) {
		String[] s = line.split("/");
		return new float[] {
				Float.parseFloat(s[0]),
				Float.parseFloat(s[1])
		};
	}
	
	public static String removeBracket(String line) {
		String brack = getBracket(line);
		if(brack.length()>=line.length()-2)
			return "";
		return line.substring(brack.length()+2);
	}
}
