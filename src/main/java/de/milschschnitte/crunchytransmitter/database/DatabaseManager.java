package de.milschschnitte.crunchytransmitter.database;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import de.milschschnitte.crunchytransmitter.ConfigLoader;
import de.milschschnitte.crunchytransmitter.reciever.Anime;
import de.milschschnitte.crunchytransmitter.reciever.Episode;

import java.sql.Connection;
import java.sql.Date;

public class DatabaseManager {
    private static Connection getConnection(ConfigLoader cl) throws SQLException, IOException {
    Connection connection = DriverManager.getConnection(cl.getDatabaseUrl(), cl.getDatabaseUsername(), cl.getDatabasePassword());    
    return connection;
    }

    /**
     * Insert anime in db
     * @param cl
     * @param anime
     * @return int anime id of inserted anime
     * @throws SQLException
     * @throws IOException
     */
    public static int insertOrUpdateAnime(ConfigLoader cl, Anime anime) throws SQLException, IOException {
        String selectQuery = "SELECT id, imageurl FROM anime WHERE title = ?";
        String updateQuery = "UPDATE anime SET imageurl = ? WHERE id = ?";
        String insertQuery = "INSERT INTO anime (title, imageurl) VALUES (?, ?) RETURNING id";
        
        try (Connection connection = getConnection(cl)) {
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setString(1, anime.getTitle());
                ResultSet resultSet = selectStatement.executeQuery();
                
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String existingImageUrl = resultSet.getString("imageurl");
                    
                    if (!existingImageUrl.equals(anime.getImageUrl())) {
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, anime.getImageUrl());
                            updateStatement.setInt(2, id);
                            updateStatement.executeUpdate();
                        }
                    }

                    return id;
                }
            }
    
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, anime.getTitle());
                insertStatement.setString(2, anime.getImageUrl());
                ResultSet resultSet = insertStatement.executeQuery();
                
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    throw new SQLException("Inserting anime failed, no ID obtained.");
                }
            }
        }
    }

    public static int insertOrUpdateEpisode(ConfigLoader cl, int animeId, Episode episode) throws SQLException, IOException {
        String selectQuery = "SELECT id, releaseTime, dateOfWeekday, dateOfCorrectionDate FROM episodes WHERE anime_id = ? AND episode = ?";
        String updateQuery = "UPDATE episodes SET releaseTime = ?, dateOfWeekday = ?, dateOfCorrectionDate = ?, correctionFlag = ? WHERE id = ?";
        String insertQuery = "INSERT INTO episodes (anime_id, episode, releaseTime, dateOfWeekday, dateOfCorrectionDate, correctionFlag) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection connection = getConnection(cl)) {
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, animeId);
                selectStatement.setString(2, episode.getEpisode());
                ResultSet resultSet = selectStatement.executeQuery();
                
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");

                    Timestamp existingReleaseTime = resultSet.getTimestamp("releaseTime");
                    Date existingDateOfWeekday = resultSet.getDate("dateOfWeekday");
                    Date existingCorrectionDate = resultSet.getDate("dateOfCorrectionDate");
                    
                    boolean needsUpdate = false;
    
                    if (!existingReleaseTime.equals(episode.getReleaseTime())) {
                        needsUpdate = true;
                    }
                    if (!existingDateOfWeekday.equals(episode.getDateOfWeekday())) {
                        needsUpdate = true;
                    }

                    if (existingCorrectionDate != null && !existingCorrectionDate.equals(episode.getDateOfCorrectionDate())) {
                        needsUpdate = true;
                    } else if (existingCorrectionDate == null && episode.getDateOfCorrectionDate() != null) {
                        needsUpdate = true;
                    }
                    
                    if (needsUpdate) {
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setTimestamp(1, episode.getReleaseTime());
                            updateStatement.setDate(2, episode.getDateOfWeekday());
                            updateStatement.setDate(3, episode.getDateOfCorrectionDate());
                            updateStatement.setBoolean(4, true);
                            updateStatement.setInt(5, id);
                            updateStatement.executeUpdate();
                        }
                    }
                    
                    return id;
                }
            }
    
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setInt(1, animeId);
                insertStatement.setString(2, episode.getEpisode());
                insertStatement.setTimestamp(3, episode.getReleaseTime());
                insertStatement.setDate(4, episode.getDateOfWeekday());
                insertStatement.setDate(5, episode.getDateOfCorrectionDate());
                insertStatement.setBoolean(6, false);
                ResultSet resultSet = insertStatement.executeQuery();
                
                if (resultSet.next()) {
                    return resultSet.getInt(1); // Rückgabe der neuen ID
                } else {
                    throw new SQLException("Inserting episode failed, no ID obtained.");
                }
            }
        }
    }
}
