package it.albemiglio.seniorparkour.utils;

public class TimeUtils {

    public static String formattedTime(long time) {
        int minutes = 0;
        int seconds = 0;
        int temp = (int) time;
        while (temp > 60) {
            minutes++;
            temp -= 60;
        }
        seconds = temp;
        String minutesS = minutes > 9 ? "" + minutes : "0" + minutes;
        String secondsS = seconds > 9 ? "" + seconds : "0" + seconds;
        return minutesS + ":" + secondsS;
    }
}
