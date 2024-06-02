package de.milschschnitte.crunchytransmitter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.milschschnitte.crunchytransmitter.database.DatabaseManager;
import de.milschschnitte.crunchytransmitter.reciever.Anime;
import de.milschschnitte.crunchytransmitter.reciever.JsonEpisodeFetcher;

@Component
public class ScheduledTasks {

    @Scheduled(fixedRate = 300000) // 300000 Millisekunden = 5 Minuten
    private static void fetchAnimeAndEpisodes() throws SQLException, IOException {
        System.out.println("fetch begin");
        JsonEpisodeFetcher jep = new JsonEpisodeFetcher();
        List<Anime> animeList = jep.fetch(ConfigLoader.getProperty("crunchyroll.seasonURL"));
        System.out.println("fetch done");

        for (Anime anime : animeList) {
            // System.out.print(anime.getTitle() + ": ");
            // System.out.println(anime.getImageUrl() + ": ");
            // System.out.println((anime.getEpisode()).toString());

            int animeId = DatabaseManager.insertOrUpdateAnime(anime);
            DatabaseManager.insertOrUpdateEpisode(animeId, anime.getEpisode());
        }
        System.out.println("database input");
    }

    @Scheduled(fixedRate = 60000) // 60000 Millisekunden = 1 Minuten
    private static void checkForPossibleNotification() {
        System.out.println("checkForPossibleNotification");
    }

    @Scheduled(fixedRate = 300000) // 300000 Millisekunden = 5 Minuten
    private static void checkForPossibleCorrections() {
        System.out.println("checkForPossibleCorrections");
    }
}
