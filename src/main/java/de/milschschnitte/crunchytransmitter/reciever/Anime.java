package de.milschschnitte.crunchytransmitter.reciever;

public class Anime {
    private Integer animeId;
    private String title;
    private Episode episode;
    private String imageUrl;
    private String crunchyrollUrl;

    public Anime() {
        this.title = "";
        this.episode = new Episode();
        this.imageUrl = "";
    }

    public Anime(Episode episode, Integer animeId, String title, String imageUrl, String crunchyrollUrl) {
        this.episode = episode;
        this.animeId = animeId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.crunchyrollUrl = crunchyrollUrl;
    }

    public String getCrunchyrollUrl() {
        return this.crunchyrollUrl;
    }

    public int getAnimeId() {
        return this.animeId;
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

    public void setAnimeId(Integer id) {
        this.animeId = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        String withoutBackslashes = imageUrl.replace("\\", "");
        this.imageUrl = withoutBackslashes.replace("\"", "");
    }

    public void setCrunchyrollUrl(String crunchyrollUrl) {
        this.crunchyrollUrl = crunchyrollUrl;
    }

    @Override
    public String toString() {
        return "Anime{" +
                "animeId=" + (animeId != null ? animeId : "null") +
                ", title='" + (title != null ? title : "null") + '\'' +
                ", episode=" + (episode != null ? episode : "null") +
                ", imageUrl='" + (imageUrl != null ? imageUrl : "null") + '\'' +
                ", crunchyrollUrl='" + (crunchyrollUrl != null ? crunchyrollUrl : "null") + '\'' +
                '}';
    }
}
