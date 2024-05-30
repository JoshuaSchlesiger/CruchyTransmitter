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
}
