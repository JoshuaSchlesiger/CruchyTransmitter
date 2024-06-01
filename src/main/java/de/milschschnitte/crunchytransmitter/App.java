package de.milschschnitte.crunchytransmitter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
    public static void main( String[] args ) throws ConfigurationException, SQLException, IOException 
    {
        ConfigLoader cl = new ConfigLoader();

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

    }
}
