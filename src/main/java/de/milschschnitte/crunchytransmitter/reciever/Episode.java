package de.milschschnitte.crunchytransmitter.reciever;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class Episode {

    private String episode;
    private Timestamp releaseTime;
    private Date dateOfWeekday;
    private Date dateOfCorretionDate;

    public Episode(){
        this.episode = "";
        this.releaseTime = null;
        this.dateOfWeekday = null;
        this.dateOfCorretionDate = null;
    }
    public void setEpisodes(String episode){
        this.episode = episode;
    }

    public void setReleaseTime(String releaseTime){

        String[] parts = releaseTime.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1].split(" ")[0]);
        
        LocalTime releaseLocalTime = LocalTime.of(hours, minutes);
        
        this.releaseTime = Timestamp.valueOf(LocalDateTime.of(this.dateOfWeekday.toLocalDate(), releaseLocalTime));
    }

    public void setDateOfWeekday(EnumWeekdays weekday){
        LocalDate today = LocalDate.now();
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();
        DayOfWeek targetDayOfWeek = EnumWeekdays.getDayOfWeek(weekday);
        int dayDifference = targetDayOfWeek.getValue() - currentDayOfWeek.getValue();
        this.dateOfWeekday = Date.valueOf(today.plusDays(dayDifference));
    }

    public void setDateOfCorretionDate(String corretionDate){
        String[] parts = corretionDate.split("am");
        String datePart = parts[1].trim();
        String[] dateParts = datePart.split(" ");
        int day = Integer.parseInt(dateParts[0].substring(0, dateParts[0].length() - 1));

        String monthString = dateParts[1];
        
        Month month = Month.from(DateTimeFormatter.ofPattern("MMMM").parse(monthString));
        
        int year = LocalDate.now().getYear();
        
        this.dateOfCorretionDate = Date.valueOf(LocalDate.of(year, month, day));
    }

    public String getEpisode() {
        return episode;
    }

    public Timestamp getReleaseTime() {
        return releaseTime;
    }

    public Date getWeekday() {
        return this.dateOfWeekday;
    }

    public Date getDateOfWeekday() {
        return this.dateOfWeekday;
    }

    public Date getDateOfCorrectionDate() {
        return this.dateOfCorretionDate;
    }

    @Override
    public String toString() {
        return "Episode: " + episode + " " +
               "Release Time: " + releaseTime + " " +
               "Weekday: " + dateOfWeekday + " " +
               "Correction Date: " + dateOfCorretionDate;
    }
}
