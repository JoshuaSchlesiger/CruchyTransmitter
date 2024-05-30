package de.milschschnitte.crunchytransmitter.reciever;

public class Episode {

    private String episode;
    private String releaseTime;
    private String weekday;
    private Integer timestampOfWeekday;
    private String corretionDate;

    public Episode(){
        this.episode = "";
        this.releaseTime = "";
        this.weekday = "";
        this.timestampOfWeekday = 0;
        this.corretionDate = "";
    }
    public void setEpisodes(String Episode){
        this.episode = Episode;
    }

    public void setReleaseTime(String ReleaseTime){
        this.releaseTime = ReleaseTime;
    }

    public void setWeekday(String Weekday){
        this.weekday = Weekday;
        setTimestampOfWeekday(Weekday);
    }

    private void setTimestampOfWeekday(String TimestampOfWeekday){
        
    }

    public void setCorretionDate(String CorretionDate){
        this.corretionDate = CorretionDate;
    }

    public String getEpisode() {
        return episode;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public String getWeekday() {
        return weekday;
    }

    public Integer getTimestampOfWeekday() {
        return this.timestampOfWeekday;
    }

    public String getCorrectionDate() {
        return this.corretionDate;
    }
}
