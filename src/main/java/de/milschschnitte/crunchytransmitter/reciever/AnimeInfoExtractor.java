package de.milschschnitte.crunchytransmitter.reciever;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Class for extracting the anime information from the json provided by the
 * Crunchyroll website
 */
public class AnimeInfoExtractor {
    static Logger logger = LoggerFactory.getLogger(AnimeInfoExtractor.class);

    public static Anime extractAnime(JsonNode animeItem, EnumWeekdays weekday) {

        Anime anime = new Anime();
        Episode episode = anime.getEpisode();

        episode.setDateOfWeekday(weekday);

        JsonNode animeBaseNode = animeItem.get("content");

        // Image
        JsonNode animeImageNode = animeBaseNode.get(0).get("content").get(0);
        String imageUrl = animeImageNode.get("attrs").get("src").asText();
        anime.setImageUrl(imageUrl.replace("\"", ""));

        // Link to anime, can be null
        try {
            String animeURL = animeImageNode.get("marks").get(0).get("attrs").get("href").asText();
            anime.setCrunchyrollUrl(animeURL.replace("\"", ""));
        } catch (Exception e) {
            anime.setCrunchyrollUrl("");
            logger.warn("No crunchyroll link found for anime: " + anime.getTitle());
        }

        // Title
        JsonNode animeTitleNode = animeBaseNode.get(1);
        String title = animeTitleNode.get("content").get(0).get("text").asText();
        anime.setTitle(title);

        // Episode
        JsonNode animeEpisodeNode = animeBaseNode.get(2);

        try {
            String languageFlag = animeEpisodeNode.get("content").get(0).get("attrs").get("src").asText();
            if (languageFlag.contains("deutschland-flagge")) {
                return null;
            }
        } catch (Exception e) {
            logger.warn("animetile: " + title + " has no language flag");
        }

        String episodeRaw = animeEpisodeNode.get("content").get(1).get("text").asText();
        episodeRaw = episodeRaw.replace("\"", "");

        int length = episodeRaw.length();

        if (length >= 4) {
            int index = episodeRaw.indexOf('F');
            if (index != -1) {
                episodeRaw = episodeRaw.substring(index);
            } else {
                logger.info("Faulty episodeRaw, skip anime title: " + title);
                return null;
            }
        }

        String episodeText = episodeRaw.substring(episodeRaw.indexOf('F'));

        if (episodeText.endsWith("*")) {
            try {
                episode.setDateOfCorrectionDate(animeEpisodeNode.get("content").get(3).get("text").asText());
            } catch (Exception e) {
                //if not a date e.g. "im dezember"
                return null;
            }
        } else {
            try {
                episode.setReleaseTime(animeEpisodeNode.get("content").get(3).get("text").asText());
            } catch (Exception e) {
                // If the release TIem is "veröffentlicht"
                return null;
            }
        }

        episodeText = episodeText.replace("*", "");
        episode.setEpisodes(episodeText);

        return anime;
    }
}