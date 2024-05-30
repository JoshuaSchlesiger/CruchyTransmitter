package de.milschschnitte.crunchytransmitter.reciever;

public class Anime {
    private String title;
    private Episode episode;

    public Anime(){
        this.title = "";
        this.episode = new Episode();
    }

    public Episode getEpisode(){
        return this.episode;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    @Override
    public String toString() {
        return "Anime{" +
                "Title='" + this.title + '\'' +
                ", Episode='" + episode.getEpisode() + '\'' +
                ", ReleaseTime='" + episode.getReleaseTime() + '\'' +
                ", Weekday='" + episode.getWeekday() + '\'' +
                ", TimestampOfWeekday='" + episode.getTimestampOfWeekday() + '\'' +
                ", CorretionDate='" + episode.getCorrectionDate() + '\'' +
                '}';
    }
}
