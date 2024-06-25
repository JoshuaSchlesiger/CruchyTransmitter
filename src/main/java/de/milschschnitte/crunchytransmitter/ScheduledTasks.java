package de.milschschnitte.crunchytransmitter;

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

/**
 * Class for the automated start of tasks
 */
@Component
public class ScheduledTasks {
    static Logger logger = LogManager.getLogger(ScheduledTasks.class);

    public static String json = "";

    /**
     * Pulling the latest data from Crunchyroll. Saving or updating in the database
     * Process happens every 5 minutes
     * 
     * @throws SQLException
     */
    @Scheduled(fixedRate = 300000)
    private static void fetchAnimeAndEpisodes() throws SQLException{
        logger.info("Anime fetch begin");
        JsonEpisodeFetcher jep = new JsonEpisodeFetcher();
        List<Anime> animeList = jep.fetchAndProcess();
        logger.info("Anime fetch done");

        logger.info("Anime database input starting");
        for (Anime anime : animeList) {
            int animeId = DatabaseManager.insertOrUpdateAnime(anime);
            DatabaseManager.insertOrUpdateEpisode(animeId, anime.getEpisode());
        }
        logger.info("Anime database input done");
    }

    /**
     * Checks every minute if there are possible notifications for the Subscribers. 
     * (Checks in the DB whether a release time is less than the current time and whether it has not yet been sent)
     * 
     */
    @Scheduled(fixedRate = 60000)
    private static void checkForPossibleNotification() {
        logger.info("Start checkForPossibleNotification");
        List<Anime> anime = DatabaseManager.getNotifiableAnime();

        for (Anime animeElement : anime) {
            Episode episode = animeElement.getEpisode();

            // SENDING OUTCOMMING ANIME TO CLIENT WITH GOOGLE FCM
            NotificationService.sendNotificationInBlocks("Neue Folge !!!",
                    animeElement.getTitle() + " - " + episode.getEpisode() + " kam heraus", animeElement.getAnimeId());
            DatabaseManager.setEpisodedPushed(episode.getEpisodeID());
        }
    }

    /**
     * Writes to the JSON string. This pulls the get route of the project (/anime). 
     * This is necessary in order to avoid the requests to the DB. The process happens every 5 minutes
     * @throws SQLException
     */
    @Scheduled(fixedRate = 300000)
    private static void getAnimeAndEpisodeInformation() throws SQLException {
        logger.info("Start getAnimeAndEpisodeInformation");
        List<Anime> anime = DatabaseManager.getEpisodesAndAnimeOfWeek();

        Gson gson = new Gson();
        json = gson.toJson(anime);
    }
}
