import javafx.util.Pair;

public class HelperFunctions {

    public static void main(String[] args){
        for(int i = 1; i <= 12; i++ ) {
            System.out.println(getDaysInMonth(i, 2020));
        }
    }

    public static Pair<int[], String> parseDate(String dateStr) {
        String[] parts = dateStr.split("\\.");
        if(parts.length != 3){
            return new Pair<>(null, "Error: Incorrect custom date format.");
        }
        int day;
        int month;
        int year;
        try {
            day = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]);
        } catch (NumberFormatException ex){
            ex.printStackTrace();
            return new Pair<>(null, "Error: Incorrect custom date format.");
        }

        if(year < 0){
            return new Pair<>(null, "Error: Year can't be negative.");
        }

        if(month < 1 || month > 12){
            return new Pair<>(null, "Error: Incorrect custom date. Month out of range.");
        }
        int numOfDays = HelperFunctions.getDaysInMonth(month, year);
        if(day < 1 || day > numOfDays){
            return new Pair<>(null, "Error: Incorrect custom date. Day out of range.");
        }
        return new Pair<>(new int[]{day, month, year}, null);
    }

    public static boolean isLeapYear(int year){
        // source: https://en.wikipedia.org/wiki/Leap_year#Algorithm
        if (year % 4 != 0) {
            return false;
        } else if (year % 100 != 0) {
            return true;
        } else
            return year % 400 == 0;
    }

    public static int getDaysInMonth(int month, int year){
        // source: http://www.dispersiondesign.com/articles/time/number_of_days_in_a_month
        return (month == 2) ? (isLeapYear(year) ? 29 : 28) : 31 - (month - 1) % 7 % 2;
    }

    private static int[] t = { 0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4 };

    public static int getDayOfWeek(int day, int month, int year){
        // source: https://en.wikipedia.org/wiki/Determination_of_the_day_of_the_week#Sakamoto's_methods
        year -= (month < 3) ? 1 : 0;
        int dayOfWeek = ( year + year/4 - year/100 + year/400 + t[month-1] + day) % 7;
        // fix, so that monday is the first day instead of sunday
        dayOfWeek--;
        if (dayOfWeek == -1) {
            dayOfWeek = 6;
        }
        return dayOfWeek;
    }
}
