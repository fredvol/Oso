package fr.kriket.oso.tools;

/**
 * Created by fred on 1/30/17.
 */

public class Timetools {

    /**
     * Format seconds string.
     *
     * To be changed.
     *
     * @param timeInMiliSeconds the time in miliseconds
     * @return the string
     */
    public static String formatSeconds(long timeInMiliSeconds)
    {
        long timeInSeconds=timeInMiliSeconds/1000;

        long hours = timeInSeconds / 3600;
        long secondsLeft = timeInSeconds - hours * 3600;
        long minutes = secondsLeft / 60;
        long seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10)
            formattedTime += "0";
        formattedTime += hours + ":";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds ;

        return formattedTime;
    }
}
