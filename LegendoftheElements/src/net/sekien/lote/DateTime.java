package net.sekien.lote;

import org.newdawn.slick.util.Log;

/**
 * Created with IntelliJ IDEA. User: matt Date: 10/03/13 Time: 10:28 AM To change this template use File | Settings |
 * File Templates.
 */
public class DateTime {
private Integer date;
private Integer time;
private static final String[] monthName={"Winter", "Spring", "Summer", "Autumn"};
private static final String[] dayName={"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
private static final String[] daySuffix={"st", "nd", "rd", "th"};

public DateTime(Integer date, Integer time) {
	if (date==null&&time==null)
		Log.error("DateTime ctor: both date and time can not be null");
	this.date=date;
	this.time=time;
}

public DateTime(String str) {
	String[] parts=str.split(":", 2);
	date=(parts[0].equals("null")?null:Integer.parseInt(str));
	time=(parts[1].equals("null")?null:Integer.parseInt(str));
}

public String toString() {
	if (date==null)
		return "null:"+time;
	if (time==null)
		return date+":null";
	return date+":"+time;
}

public void set(int date, int time) {
	this.date=date;
	this.time=time;
}

public String asDetailedString() {
	String timestr="";
	if (time!=null)
		timestr=((time/60)%12)+":"+(time%60)+(time<720?" AM":" PM");
	String datestr="";
	if (date!=null)
		datestr=(dayName[date%7]+", "+(date%24+1)+daySuffix[Math.min((date%24)%20, 3)]+" "+monthName[(date/24)%4]+" "+(date/96+1400)+" ("+date+":"+time+")");
	if (date!=null&&time!=null)
		return datestr+" "+timestr;
	else
		return datestr+timestr;
}

public long timeSince(DateTime other) {
	return this.time-other.time+(this.date-other.date)*1440;
}
}
