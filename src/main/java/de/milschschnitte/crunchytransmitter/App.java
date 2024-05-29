package de.milschschnitte.crunchytransmitter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.ConfigurationException;

import de.milschschnitte.crunchytransmitter.database.DatabaseManager;
import de.milschschnitte.crunchytransmitter.reciever.JsonEpisodeFetcher;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ConfigurationException
    {
        ConfigLoader cl = new ConfigLoader();
        try {
            Connection db = DatabaseManager.getConnection(cl);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        JsonEpisodeFetcher jep = new JsonEpisodeFetcher();
        jep.fetch();
    }
    //https://cr-news-api-service.prd.crunchyrollsvc.com/v1/de-DE/stories?slug=seasonal-lineup%2F2024%2F4%2F1%2Fcrunchyroll-wochenprogramm-fruehling-2024

    //Das %2F ist ein / die Website ist einfac hdie hauptseite in umgeformt
}
