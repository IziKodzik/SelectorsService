/**
 *
 *  @author Adarczyn Piotr S19092
 *
 */

package zad1;


import com.sun.org.apache.regexp.internal.RE;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm",new Locale("pl"));
	public static SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd"
			, new DateFormatSymbols(new Locale("pl")));


	public static String passed(String from, String to) {


		Exception exec = null;


		if(from.matches("[0-9]{4}-[0-31]{2}-[0-9]{2}")) {
			try {

				LocalDate fDate = LocalDate.parse(from);
				LocalDate tDate = LocalDate.parse(to);
				return locale(fDate, tDate);

			} catch (Exception e) {
				exec = e;
			}

		}else{

			try {

				LocalDateTime fDate = LocalDateTime.parse(from,formatter);
				LocalDateTime tDate = LocalDateTime.parse(to,formatter);
				return locale(fDate,tDate);

			}catch (Exception e){
				exec = e;
			}
		}
		return "*** " + exec.getClass().getCanonicalName() + ": " + exec.getMessage();
	}
//
//	public static String qwe(){
//
//		String result =
//	}

	public static String locale(LocalDateTime from,LocalDateTime to){

		StringBuilder result = new StringBuilder(dyh(from,to));

		int period = (int) ChronoUnit.DAYS.between(from,to);

		ZoneId id = ZoneId.of("Europe/Warsaw");
		ZonedDateTime fZoned = from.atZone(id);
		ZonedDateTime tZoned = to.atZone(id).withZoneSameInstant(simpleDF.getTimeZone().toZoneId());

		int h = (int) ChronoUnit.HOURS.between(fZoned,tZoned);
		int m = (int) ChronoUnit.MINUTES.between(fZoned,tZoned);

		result.append(" - godzin: ").append(h).append(", minut: ").append(m).append('\n');

		if(period != 0)
			result.append(calendar(from.toLocalDate(),to.toLocalDate()));

		return result.toString();

	}

	public static String locale(LocalDate from, LocalDate to){

		StringBuilder result = new StringBuilder(dy(from,to));

		int period = (int) ChronoUnit.DAYS.between(from,to);

		if(period != 0)
			result.append(calendar(from,to));

		return result.toString();

	}

	public static String dyh(LocalDateTime from,LocalDateTime to){

		StringBuilder result;

		String nmf = simpleDF.getDateFormatSymbols().getMonths()[from.getMonthValue() - 1];
		int var = from.getDayOfWeek().getValue()+1;
		if (var >= 8)
			var = 1;

		String dwf = simpleDF.getDateFormatSymbols().getWeekdays()[var];
		int fday = from.getDayOfMonth();
		int tday = to.getDayOfMonth();

		String nmt = simpleDF.getDateFormatSymbols().getMonths()[to.getMonthValue()-1];
		var = to.getDayOfWeek().getValue() + 1;
		if (var >= 8)
			var = 1;

		String dwt = simpleDF.getDateFormatSymbols().getWeekdays()[var];
		int yf = from.getYear();
		int hf = from.getHour();
		int mf = from.getMinute();



		int yt = to.getYear();
		int ht = to.getHour();
		int mt = to.getMinute();

		String tFrom = String.format("godz. %02d:%02d", hf, mf);
		String tTo = String.format("godz. %02d:%02d", ht, mt);

		result = new StringBuilder(String.format("Od %d %s %s (%s) %s do %d %s %d (%s) %s\n", fday, nmf, yf, dwf, tFrom, tday, nmt, yt, dwt, tTo));

		double period = (double) ChronoUnit.DAYS.between(from.toLocalDate(), to.toLocalDate());
		double weeks = period / 7.0;

		DecimalFormat decimalFormat = new DecimalFormat("####,####.##");

		result.append(String.format(" - mija: %d %s, tygodni %s\n",(int)period, polishD((int)period), decimalFormat.format(weeks).replace(',','.')));
		return result.toString();

	}
//
	public static String dy(LocalDate from,LocalDate to){

		StringBuilder result = new StringBuilder();

		String nmf = simpleDF.getDateFormatSymbols().getMonths()[from.getMonthValue() - 1];
		int variable = from.getDayOfWeek().getValue() + 1;
		if(variable >= 8)
			variable = 1;

		String dwf = simpleDF.getDateFormatSymbols().getWeekdays()[variable];

		int fday = from.getDayOfMonth();
		int tday = to.getDayOfMonth();

		String nmt = simpleDF.getDateFormatSymbols().getMonths()[to.getMonthValue()-1];
		variable = to.getDayOfWeek().getValue() + 1;
		if(variable >= 8)
			variable = 1;

		String dwt = simpleDF.getDateFormatSymbols().getWeekdays()[variable];
		int fy = from.getYear();
		int ty = to.getYear();

		result.append(String.format("Od %d %s %s (%s) do %d %s %d (%s)\n",  fday, nmf,fy, dwf, tday, nmt,ty, dwt ));
		double period = ChronoUnit.DAYS.between(from,to);
		double weeks = period / 7;
		DecimalFormat decimalFormat = new DecimalFormat("####,####.##");

		result.append(String.format(" - mija: %d %s, tygodni %s\n", (int)period,polishD((int)period)
				,decimalFormat.format(weeks).replace(',','.')));
		return result.toString();

	}

	public static String polishD(int day){

		if(day != 1)
			return "dni";

		return "dzień";
	}

	public static String polishM(int month){

		String string = String.valueOf(month);

		int d = Integer.parseInt(String.valueOf(string.charAt(string.length() - 1)));

		if (d == 1)
			return "miesiąc";

		if(d < 5 && d > 0)
			return "miesiące";

		return "miesięcy";

	}

	public static String polishY(int year){

		String string = String.valueOf(year);
		int d = Integer.parseInt(String.valueOf(string.charAt(string.length() - 1)));

		if(d == 1)
			return "rok";

		if(d < 5 && d > 0)
			return "lata";

		return "lat";

	}
	public static String calendar(LocalDate from,LocalDate to){

		StringBuilder result = new StringBuilder(" - kalendarzowo: ");

		Period period = Period.between(from,to);
		int yPeriod = period.getYears();
		int mPeriod = period.getMonths();
		int dPeriod = period.getDays();

		if(yPeriod != 0)
			result.append(String.format("%d %s" , yPeriod , polishY(yPeriod)));

		if (mPeriod != 0) {
			if (yPeriod != 0)
				result.append(", ");

			result.append(String.format("%d %s", mPeriod, polishM(mPeriod)));
		}
		if (dPeriod != 0) {
			if (mPeriod != 0 || yPeriod != 0)
				result.append(", ");

			result.append(String.format("%d %s", dPeriod, polishD(dPeriod)));
		}
		return result.toString();
	}
}



