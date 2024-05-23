package de.milschschnitte.crunchytransmitter.reciever;

public class Anime {
    String Title;
    String Episode;
    String ReleaseTime;
    String Weekday;
    String TimestampOfWeekday;

    public Anime(){
        this.Title = "";
        this.Episode = "";
        this.ReleaseTime = "";
        this.Weekday = "";
        this.TimestampOfWeekday = "";
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

    @Override
    public String toString() {
        return "Anime{" +
                "Title='" + Title + '\'' +
                ", Episode='" + Episode + '\'' +
                ", ReleaseTime='" + ReleaseTime + '\'' +
                ", Weekday='" + Weekday + '\'' +
                ", TimestampOfWeekday='" + TimestampOfWeekday + '\'' +
                '}';
    }
}
