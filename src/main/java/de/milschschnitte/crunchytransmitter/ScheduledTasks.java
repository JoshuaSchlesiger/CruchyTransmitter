package de.milschschnitte.crunchytransmitter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import de.milschschnitte.crunchytransmitter.database.DatabaseManager;
import de.milschschnitte.crunchytransmitter.fcm.NotificationService;
import de.milschschnitte.crunchytransmitter.reciever.Anime;
import de.milschschnitte.crunchytransmitter.reciever.Episode;
import de.milschschnitte.crunchytransmitter.reciever.JsonEpisodeFetcher;

@Component
public class ScheduledTasks {
    static Logger logger = LogManager.getLogger(ScheduledTasks.class);

    public static String json = "";

    @Scheduled(fixedRate = 300000) // 300000 Millisec = 5 minutes
    private static void fetchAnimeAndEpisodes() throws SQLException, IOException {
        logger.info("Anime fetch begin");
        JsonEpisodeFetcher jep = new JsonEpisodeFetcher();
        List<Anime> animeList = jep.fetch(ConfigLoader.getProperty("crunchyroll.seasonURL"));
        logger.info("Anime fetch done");

        for (Anime anime : animeList) {
            int animeId = DatabaseManager.insertOrUpdateAnime(anime);
            DatabaseManager.insertOrUpdateEpisode(animeId, anime.getEpisode());
        }
        logger.info("Anime databse input done");
    }

    @Scheduled(fixedRate = 60000) // 60000 Millisec = 1 minutes
    private static void checkForPossibleNotification() {
        logger.info("Start checkForPossibleNotification");
        List<Anime> anime = DatabaseManager.getNotifiableAnime();
        Gson gson = new Gson();
        for (Anime animeElement : anime) {
            Episode episode = animeElement.getEpisode();

            String episodeJson = gson.toJson(episode);

            //SENDING OUTCOMMING ANIME TO CLIENT WITH GOOGLE FCM
            NotificationService.sendNotificationInBlocks("release", episodeJson);
            DatabaseManager.setEpisodedPushed(episode.getEpisodeID());
        }
    }

    @Scheduled(fixedRate = 300000) // 300000 Millisec = 5 minutes
    private static void getAnimeAndEpisodeInformation() throws SQLException {
        logger.info("Start getAnimeAndEpisodeInformation");
        List<Anime> anime = DatabaseManager.getEpisodesAndAnimeOfWeek();

        Gson gson = new Gson();
        json = gson.toJson(anime);
    }
}
