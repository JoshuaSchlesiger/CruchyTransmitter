package de.milschschnitte.crunchytransmitter.reciever;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Episode {
    private Integer episodeID;
    private String episode;
    private Timestamp releaseTime;
    private Date dateOfWeekday;
    private Date dateOfCorretionDate;

    static Logger logger = LoggerFactory.getLogger(Episode.class);

    public Episode(){
        this.episodeID = null;
        this.episode = "";
        this.releaseTime = null;
        this.dateOfWeekday = null;
        this.dateOfCorretionDate = null;
    }

    public Episode(Integer episodeID, String episode, Timestamp releaseTime, Date dateOfWeekday, Date dateOfCorretionDate){
        this.episodeID = episodeID;
        this.episode = episode;
        this.releaseTime = releaseTime;
        this.dateOfWeekday = dateOfWeekday;
        this.dateOfCorretionDate = dateOfCorretionDate;
    }

    public void setEpisodes(String episode){
        this.episode = episode;
    }

    public void setReleaseTime(String releaseTime){
        //TBA equals to be announced
        if(releaseTime.equals("") || releaseTime.equals("TBA")){
            return;
        }
        try {
            String[] parts = releaseTime.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1].split(" ")[0]);
            
            LocalTime releaseLocalTime = LocalTime.of(hours, minutes);
            
            this.releaseTime = Timestamp.valueOf(LocalDateTime.of(this.dateOfWeekday.toLocalDate(), releaseLocalTime));
        } catch (Exception e) {
            logger.warn("faulty releaseTime: " + releaseTime );
        }

    }

    public void setDateOfWeekday(EnumWeekdays weekday){
        LocalDate today = LocalDate.now();
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();
        DayOfWeek targetDayOfWeek = EnumWeekdays.getDayOfWeek(weekday);
        int dayDifference = targetDayOfWeek.getValue() - currentDayOfWeek.getValue();
        this.dateOfWeekday = Date.valueOf(today.plusDays(dayDifference));
    }

    public void setDateOfCorrectionDate(String correctionDate) {

        int startIndex = -1;
        for (int i = 1; i < correctionDate.length(); i++) {
            if (Character.isDigit(correctionDate.charAt(i))) {
                startIndex = i;
                break;
            }
        }

        if (startIndex == -1) {
            logger.error("Cannot find digit of Date in CorrectionDate: " + correctionDate);
            return;
        }
        
        String datePart = correctionDate.substring(startIndex).trim();
        String[] dateParts = datePart.split(" ");

        int day = Integer.parseInt(dateParts[0].substring(0, dateParts[0].length() - 1));

        String monthString = dateParts[1];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.GERMAN);
        Month month = Month.from(formatter.parse(monthString));

        int year = LocalDate.now().getYear();

        this.dateOfCorretionDate = Date.valueOf(LocalDate.of(year, month, day));
    }

    public Integer getEpisodeID(){
        return this.episodeID;
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
