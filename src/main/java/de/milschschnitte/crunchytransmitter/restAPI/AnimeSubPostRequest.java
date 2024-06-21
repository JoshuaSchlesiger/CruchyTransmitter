package de.milschschnitte.crunchytransmitter.restAPI;

public class AnimeSubPostRequest {
    private String password;
    private String animeID;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAnimeID() {
        return animeID;
    }

    public void setAnimeID(String animeID) {
        this.animeID = animeID;
    }
}
