package org.dljl.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Setter
@Getter
public class Availability {
    private Long providerId; // Unique to each client
    private Map<String, Day> Availability;

    public Availability(Long providerId){
        this.providerId = providerId;
        this.Availability = this.initializeAvailability();
        MonthUpdate();
    }
    public class Day{
        public boolean Available;
        public int[][] hours;

        public Day(boolean Available, int[][] hours){
            this.Available = Available;
            this.hours = hours;
        }
    }

    public Map<String, Day> initializeAvailability(){
        Map<String, Day> Avai = new HashMap<>();
        for (int month = 1; month < 13; month++){
            for (int day = 1; day < 32; day++){
                int[][] hours = new int[24][60]; // 0 means available in this minute
                if (month < 10) {
                    if (day < 10){
                        Avai.put("0"+String.valueOf(month)+"0"+String.valueOf(day),  new Day(true, hours));
                    }
                    else{
                        Avai.put("0"+String.valueOf(month)+String.valueOf(day),  new Day(true, hours));
                    }
                }
                else {
                    if (day < 10){
                        Avai.put(String.valueOf(month)+"0"+String.valueOf(day),  new Day(true, hours));
                    }
                    else{
                        Avai.put(String.valueOf(month)+String.valueOf(day),  new Day(true, hours));
                    }
                }
            }
        }
        return Avai;
    }

    public void BookDay(String day){
        Day update = this.Availability.get(day);
        update.Available = false;
        this.Availability.put(day, update);
    }

    public void MonthUpdate(){
        //update those months which do not have 31 days
        this.BookDay("0229");
        this.BookDay("0230");
        this.BookDay("0231");
        this.BookDay("0431");
        this.BookDay("0631");
        this.BookDay("0931");
        this.BookDay("1131");
    }

    public void changeAvailability(String appointmentTime){
        //appointmentTime: MMDD:HHmm-HHmm
        String day = appointmentTime.substring(0,4);
        int hourStart = Integer.valueOf(appointmentTime.substring(5,7));
        int minuteStart = Integer.valueOf(appointmentTime.substring(7,9));
        int hourEnd = Integer.valueOf(appointmentTime.substring(10,12));
        int minuteEnd = Integer.valueOf(appointmentTime.substring(12,14));
        Day update = this.Availability.get(day);
        int[][] hours = update.hours;
        if (hourStart == hourEnd){
            for (int i = 0; i < 60; i++){
                hours[hourStart-1][i]=1;
            }
        }
        else{
            for (int i = minuteStart; i < 60; i++){
                //first hour
                hours[hourStart-1][i]=1;
            }
            for (int i = hourStart; i < hourEnd-1; i++){
                //hours in between
                for (int j = 0; j < 60; j++){
                    hours[i][j]=1;
                }
            }
            for (int i = 0; i < minuteEnd; i++){
                //last hour
                hours[hourEnd-1][i]=1;
            }
        }
        update.hours = hours;
        this.Availability.put(day, update);
    }

    public String availableDays(String Month){
        //Month in MM
        String ret = "Available Days in " + Month + "are: ";
        for (int i=1; i<32; i++){
            String day = String.valueOf(i);
            if(i<10){
                day = "0"+day;
            }
            if (this.Availability.get(Month+day).Available){
                ret += day;
            }
        }
        return ret;
    }

    public String availableAtDay(String day){
        //day as MMDD
        Day toCheck = this.Availability.get(day);
        if(!toCheck.Available){
            return "This day is not available";
        }
        int[][]availability = toCheck.hours;
        StringBuilder availablePeriods = new StringBuilder();
        boolean isAvailable = false;
        int startHour = 0, startMinute = 0;

        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute++) {
                if (availability[hour][minute] == 0) {
                    if (!isAvailable) {
                        // Start of an available period
                        startHour = hour;
                        startMinute = minute;
                        isAvailable = true;
                    }
                } else {
                    if (isAvailable) {
                        // End of an available period
                        availablePeriods.append(formatTime(startHour, startMinute))
                                        .append(" - ")
                                        .append(formatTime(hour, minute))
                                        .append("\n");
                        isAvailable = false;
                    }
                }
            }
        }
        
        // Check if the last period ends at the end of the day
        if (isAvailable) {
            availablePeriods.append(formatTime(startHour, startMinute))
                            .append(" - ")
                            .append("23:59\n");
        }

        return availablePeriods.toString();
    }
    
    // Helper method to format time as HH:mm
    private static String formatTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }
}
