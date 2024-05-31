package de.milschschnitte.crunchytransmitter.reciever;

public class Anime {
    private String title;
    private Episode episode;
    private String imageUrl;

    public Anime() {
        this.title = "";
        this.episode = new Episode();
        this.imageUrl = "";
    }

    public Episode getEpisode() {
        return this.episode;
    }

    public String getTitle() {
        return this.title;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        String withoutBackslashes = imageUrl.replace("\\", "");
        this.imageUrl = withoutBackslashes.replace("\"", "");
    }
}
