package jocke.helper;

public class JSONhelper {
	
	/**
	 * name + value => "name":value
	 * @param name
	 * @param value
	 * @return
	 */
	public static String pairToJson(String name, String value){
		return stringToName(name) + value;
	}
	
	public static String pairToJson(String name, float value){
		return pairToJson(name, String.valueOf(value));
	}
	public static String pairToJson(String name, int value){
		return pairToJson(name, String.valueOf(value));
	}
	public static String pairToJson(String name, long value){
		return pairToJson(name, String.valueOf(value));
	}
	
	/**
	 * name => "value":
	 * @param name
	 * @return
	 */
	public static String stringToName(String name){
		return "\""+ name + "\":";
	}

}
