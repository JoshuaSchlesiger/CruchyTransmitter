package de.milschschnitte.crunchytransmitter.reciever;

public class Anime {
    private String Title;
    private String Episode;
    private String ReleaseTime;
    private String Weekday;
    private String TimestampOfWeekday;
    private String CorretionDate;

    public Anime(){
        this.Title = "";
        this.Episode = "";
        this.ReleaseTime = "";
        this.Weekday = "";
        this.TimestampOfWeekday = "";
        this.CorretionDate = "";
    }

    public void setTitle(String Title){
        this.Title = Title;
    }

    public void setEpisodes(String Episode){
        this.Episode = Episode;
    }

    public void setReleaseTime(String ReleaseTime){
        this.ReleaseTime = ReleaseTime;
    }

    public void setWeekday(String Weekday){
        this.Weekday = Weekday;
    }

    public void setCorretionDate(String CorretionDate){
        this.CorretionDate = CorretionDate;
    }

    public String getEpisode(){
        return this.Episode;
    }

    public String getReleaseTime(){
        return this.ReleaseTime;
    }

    @Override
    public String toString() {
        return "Anime{" +
                "Title='" + Title + '\'' +
                ", Episode='" + Episode + '\'' +
                ", ReleaseTime='" + ReleaseTime + '\'' +
                ", Weekday='" + Weekday + '\'' +
                ", TimestampOfWeekday='" + TimestampOfWeekday + '\'' +
                ", CorretionDate='" + CorretionDate + '\'' +
                '}';
    }
}
