package de.milschschnitte.crunchytransmitter.database;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.milschschnitte.crunchytransmitter.ConfigLoader;
import de.milschschnitte.crunchytransmitter.reciever.Anime;
import de.milschschnitte.crunchytransmitter.reciever.Episode;

import java.sql.Connection;

public class DatabaseManager {
    private static Connection getConnection(ConfigLoader cl) throws SQLException, IOException {
        return DriverManager.getConnection(cl.getDatabaseUrl(), cl.getDatabaseUsername(), cl.getDatabasePassword());
    }

    /**
     * Insert anime in db
     * @param cl
     * @param anime
     * @return int anime id of inserted anime
     * @throws SQLException
     * @throws IOException
     */
    public static int insertAnime(ConfigLoader cl, Anime anime) throws SQLException, IOException {
        String query = "INSERT INTO animes (title) VALUES (?) RETURNING id";
        try (Connection connection = getConnection(cl);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, anime.getTitle());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                throw new SQLException("Inserting anime failed, no ID obtained.");
            }
        }
    }

    public static void insertEpisode(ConfigLoader cl, int animeId, Episode episode) throws SQLException, IOException {
        String query = "INSERT INTO episodes (anime_id, episode, release_time, timestamp_of_weekday, correction_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection(cl);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, animeId);
            preparedStatement.setString(2, episode.getEpisode());
            preparedStatement.setTimestamp(3, episode.getReleaseTime());
            preparedStatement.setDate(4, episode.getDateOfWeekday());
            preparedStatement.setDate(5, episode.getDateOfCorrectionDate());
            preparedStatement.executeUpdate();
        }
    }
}
