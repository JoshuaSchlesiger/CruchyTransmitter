package de.milschschnitte.crunchytransmitter.database;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.milschschnitte.crunchytransmitter.ConfigLoader;
import de.milschschnitte.crunchytransmitter.reciever.Anime;
import de.milschschnitte.crunchytransmitter.reciever.Episode;

import java.sql.Connection;
import java.sql.Date;

public class DatabaseManager {
    static Logger logger = LogManager.getLogger(DatabaseManager.class);

    private static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(ConfigLoader.getProperty("spring.datasource.url"),
                ConfigLoader.getProperty("spring.datasource.username"),
                ConfigLoader.getProperty("spring.datasource.password"));
        return connection;
    }

    /**
     * Insert anime in db
     * 
     * @param cl
     * @param anime
     * @return int anime id of inserted anime
     * @throws SQLException
     * @throws IOException
     */
    public static int insertOrUpdateAnime(Anime anime) throws SQLException {
        String selectQuery = "SELECT id, imageurl FROM anime WHERE title = ?";
        String updateQuery = "UPDATE anime SET imageurl = ? WHERE id = ?";
        String insertQuery = "INSERT INTO anime (title, imageurl) VALUES (?, ?) RETURNING id";

        try (Connection connection = getConnection()) {
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

                            logger.info("Updatet anime: " + id + ", notification will be send");
                            // SEND UPDATE TO CLIENT WITH GOOGLE FCM ONLY ANIME INFORMATION
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

    public static int insertOrUpdateEpisode(int animeId, Episode episode) throws SQLException {
        String selectQuery = "SELECT id, releaseTime, dateOfWeekday, dateOfCorrectionDate FROM episodes WHERE anime_id = ? AND episode = ?";
        String updateQuery = "UPDATE episodes SET releaseTime = ?, dateOfWeekday = ?, dateOfCorrectionDate = ? WHERE id = ?";
        String insertQuery = "INSERT INTO episodes (anime_id, episode, releaseTime, dateOfWeekday, dateOfCorrectionDate, sendedPushToUser) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection connection = getConnection()) {
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

                    if (existingCorrectionDate != null
                            && !existingCorrectionDate.equals(episode.getDateOfCorrectionDate())) {
                        needsUpdate = true;
                    } else if (existingCorrectionDate == null && episode.getDateOfCorrectionDate() != null) {
                        needsUpdate = true;
                    }

                    if (needsUpdate) {
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setTimestamp(1, episode.getReleaseTime());
                            updateStatement.setDate(2, episode.getDateOfWeekday());
                            updateStatement.setDate(3, episode.getDateOfCorrectionDate());
                            updateStatement.setInt(5, id);
                            updateStatement.executeUpdate();
                            logger.info("Updatet episode: " + id + ", notification will be send");

                            // SEND UPDATE TO CLIENTS WITH GOOGLE FCM ONLY EPISODE INFORMATION
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
                    return resultSet.getInt(1);
                } else {
                    throw new SQLException("Inserting episode failed, no ID obtained.");
                }
            }
        }
    }

    public static List<Anime> getNotifiableAnime() {
        String selectQueryEpisode = "SELECT id, anime_id, episode, releaseTime, dateOfWeekday, dateOfCorrectionDate FROM episodes WHERE sendedpushtouser = false AND releaseTime <= now() AND dateofcorrectiondate IS NULL";
        List<Anime> notifiableAnimes = new ArrayList<>();

        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(selectQueryEpisode);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int animeId = resultSet.getInt("anime_id");
                String episodeString = resultSet.getString("episode");
                Timestamp releaseTime = resultSet.getTimestamp("releaseTime");
                Date dateOfWeekday = resultSet.getDate("dateOfWeekday");
                Date dateOfCorrectionDate = resultSet.getDate("dateOfCorrectionDate");

                Episode episode = new Episode(id, episodeString, releaseTime, dateOfWeekday, dateOfCorrectionDate);

                String selectQueryAnime = "SELECT title, imageurl FROM anime WHERE id = ?";
                PreparedStatement animeStatement = connection.prepareStatement(selectQueryAnime);
                animeStatement.setInt(1, animeId);
                try (ResultSet animeResultSet = animeStatement.executeQuery()) {
                    if (animeResultSet.next()) {
                        String title = animeResultSet.getString("title");
                        String imageUrl = animeResultSet.getString("imageurl");

                        Anime anime = new Anime(episode, animeId, title, imageUrl);
                        notifiableAnimes.add(anime);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifiableAnimes;
    }

    public static void setEpisodedPushed(Integer episodeID) {
        String updateQuery = "UPDATE episodes SET sendedpushtouser = true WHERE id = ?";

        try (Connection connection = getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

            updateStatement.setInt(1, episodeID);
            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated > 0) {
                logger.info("Marked episode als pushed (episodeID): " + episodeID);
            } else {
                logger.fatal("Cannot find episode with id: " + episodeID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Anime> getEpisodesAndAnimeOfWeek() throws SQLException {
        LocalDate today = LocalDate.now();

        // Go backward to get Monday
        LocalDate monday = today;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }

        // Go forward to get Sunday
        LocalDate sunday = today;
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }

        String selectQueryEpisode = "SELECT id, anime_id, episode, releaseTime, dateOfWeekday, dateOfCorrectionDate FROM episodes WHERE releaseTime >= ? AND releaseTime <= ?";

        try (Connection connection = getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQueryEpisode)) {
                selectStatement.setTimestamp(1, Timestamp.valueOf(monday.atStartOfDay()));
                selectStatement.setTimestamp(2, Timestamp.valueOf(sunday.atStartOfDay()));
                ResultSet resultSet = selectStatement.executeQuery();

                List<Anime> animeList = new ArrayList<>();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int animeId = resultSet.getInt("anime_id");
                    String episodeNumber = resultSet.getString("episode");
                    Timestamp releaseTime = resultSet.getTimestamp("releaseTime");
                    Date weekday = resultSet.getDate("dateOfWeekday");
                    Date correctionDate = resultSet.getDate("dateOfCorrectionDate");

                    Episode episode = new Episode(id, episodeNumber, releaseTime, weekday, correctionDate);

                    String selectQueryAnime = "SELECT id, title, imageurl FROM anime WHERE id = ?";
                    try (PreparedStatement selectStatementAnime = connection.prepareStatement(selectQueryAnime)) {
                        selectStatementAnime.setInt(1, animeId);
                
                        ResultSet animeResultSet = selectStatementAnime.executeQuery();
                        if (animeResultSet.next()) {
                            String title = animeResultSet.getString("title");
                            String imageUrl = animeResultSet.getString("imageurl");
                
                            Anime anime = new Anime(episode, animeId, title, imageUrl);
                            animeList.add(anime);
                        }else{
                            logger.fatal("Can not find anime with id: " + animeId);
                        }
                    }
                }
                return animeList;
            }
        }
    }
}
