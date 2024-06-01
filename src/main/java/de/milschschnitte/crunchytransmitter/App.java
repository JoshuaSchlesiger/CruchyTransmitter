package de.milschschnitte.crunchytransmitter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.naming.ConfigurationException;

import de.milschschnitte.crunchytransmitter.database.DatabaseManager;
import de.milschschnitte.crunchytransmitter.reciever.Anime;
import de.milschschnitte.crunchytransmitter.reciever.JsonEpisodeFetcher;

/**
 * Hello world!
 *
 */
public class App 
{
    private static ConfigLoader cl;
    public static void main( String[] args ) throws ConfigurationException
    {
        cl = new ConfigLoader();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2); // 2 threads for 2 tasks

        // Schedule fetchAnimeAndEpisodes to run every minute
        scheduler.scheduleAtFixedRate(() -> {
            try {
                fetchAnimeAndEpisodes();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.MINUTES);

        // Schedule checkForPossibleNotification to run every minute
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkForPossibleNotification();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private static void fetchAnimeAndEpisodes() throws SQLException, IOException{
        System.out.println("fetch begin");
        JsonEpisodeFetcher jep = new JsonEpisodeFetcher();
        List<Anime> animeList = jep.fetch(cl.getCrunchyrollSeasonURL());
        System.out.println("fetch done");
       
        for (Anime anime : animeList) {
            // System.out.print(anime.getTitle() + ": ");
            // System.out.println(anime.getImageUrl() + ": ");
            // System.out.println((anime.getEpisode()).toString());

            int animeId = DatabaseManager.insertOrUpdateAnime(cl, anime);
            DatabaseManager.insertOrUpdateEpisode(cl, animeId, anime.getEpisode());
        }
        System.out.println("database input");
    }

    private static void checkForPossibleNotification(){
        System.out.println("checkForPossibleNotification");
    }
}
