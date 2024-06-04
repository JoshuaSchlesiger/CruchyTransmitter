package de.milschschnitte.crunchytransmitter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.milschschnitte.crunchytransmitter.database.DatabaseManager;
import de.milschschnitte.crunchytransmitter.reciever.Anime;
import de.milschschnitte.crunchytransmitter.reciever.JsonEpisodeFetcher;

@Component
public class ScheduledTasks {
    static Logger logger = LogManager.getLogger(ScheduledTasks.class);

    @Scheduled(fixedRate = 300000) // 300000 Millisekunden = 5 Minuten
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

    @Scheduled(fixedRate = 60000) // 60000 Millisekunden = 1 Minuten
    private static void checkForPossibleNotification() {
        logger.info("Start checkForPossibleNotification");
    }

    @Scheduled(fixedRate = 300000) // 300000 Millisekunden = 5 Minuten
    private static void checkForPossibleCorrections() {
        logger.info("Start checkForPossibleCorrections");

    }
}
