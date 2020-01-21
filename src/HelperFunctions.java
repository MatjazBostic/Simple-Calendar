public class HelperFunctions {

    public static void main(String[] args){
        System.out.println(getDayOfWeek(25, 1, 2020));
    }

    public static boolean isLeapYear(int year){
        // source: https://en.wikipedia.org/wiki/Leap_year#Algorithm
        if (year % 4 != 0) {
            return false;
        } else if (year % 100 != 0) {
            return true;
        } else if (year % 400 != 0) {
            return false;
        } else {
            return true;
        }
    }

    public static int getDayOfWeek(int d, int m, int y){
        // source: https://en.wikipedia.org/wiki/Determination_of_the_day_of_the_week#Sakamoto's_methods
        int[] t = { 0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4 };
        y -= (m < 3) ? 1 : 0;
        int dayOfWeek = ( y + y/4 - y/100 + y/400 + t[m-1] + d) % 7;
        // fix, so that monday is the first day instead of sunday
        dayOfWeek--;
        if (dayOfWeek == -1) {
            dayOfWeek = 6;
        }
        return dayOfWeek;
    }
}
