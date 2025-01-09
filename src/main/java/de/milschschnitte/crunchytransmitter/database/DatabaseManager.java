package de.milschschnitte.crunchytransmitter.database;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.milschschnitte.crunchytransmitter.ConfigLoader;
import de.milschschnitte.crunchytransmitter.fcm.NotificationService;
import de.milschschnitte.crunchytransmitter.reciever.Anime;
import de.milschschnitte.crunchytransmitter.reciever.EnumWeekdays;
import de.milschschnitte.crunchytransmitter.reciever.Episode;

import java.sql.Connection;
import java.sql.Date;

public class DatabaseManager {
    static Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

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
        String selectQuery = "SELECT id, imageurl, crunchyrollurl FROM anime WHERE title = ?";
        String updateQuery = "UPDATE anime SET imageurl = ?, crunchyrollurl = ? WHERE id = ?";
        String insertQuery = "INSERT INTO anime (title, imageurl, crunchyrollurl) VALUES (?, ?, ?) RETURNING id";

        try (Connection connection = getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setString(1, anime.getTitle());
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    int id = resultSet.getInt("id");

                    String existingImageUrl = resultSet.getString("imageurl");
                    String existingCrunchyrollurl = resultSet.getString("crunchyrollurl");

                    if ((existingImageUrl == null && anime.getImageUrl() != null) ||
                            (existingImageUrl != null && !existingImageUrl.equals(anime.getImageUrl()))) {
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, anime.getImageUrl());
                            updateStatement.setString(2, anime.getCrunchyrollUrl());
                            updateStatement.setInt(3, id);
                            updateStatement.executeUpdate();

                            logger.info("Updated anime: " + id + ", notification will be send");
                        }
                    } else if ((existingCrunchyrollurl == null && anime.getCrunchyrollUrl() != null) ||
                            (existingCrunchyrollurl != null
                                    && !existingCrunchyrollurl.equals(anime.getCrunchyrollUrl()))) {
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, anime.getImageUrl());
                            updateStatement.setString(2, anime.getCrunchyrollUrl());
                            updateStatement.setInt(3, id);
                            updateStatement.executeUpdate();

                            logger.info("Updated anime: " + id + ", notification will be send");
                        }
                    }

                    return id;
                }

            } catch (Exception e) {
                logger.warn(e.getMessage());
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, anime.getTitle());
                insertStatement.setString(2, anime.getImageUrl());
                insertStatement.setString(3, anime.getCrunchyrollUrl());
                ResultSet resultSet = insertStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    throw new SQLException("Inserting anime failed, no ID obtained.");
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        throw new SQLException("Inserting anime failed, no ID obtained.");
    }

    public static int insertOrUpdateEpisode(int animeId, Episode episode) throws SQLException {
        // Catching empty episodeTitle
        if (episode.getEpisode() == null) {
            return -1;
        }

        String selectQuery = "SELECT id, releaseTime, dateOfWeekday, dateOfCorrectionDate, episode FROM episodes WHERE anime_id = ?";
        String updateQuery = "UPDATE episodes SET releaseTime = ?, dateOfWeekday = ?, dateOfCorrectionDate = ? WHERE id = ?";
        String insertQuery = "INSERT INTO episodes (anime_id, episode, releaseTime, dateOfWeekday, dateOfCorrectionDate, sendedPushToUser) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection connection = getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, animeId);
                ResultSet resultSet = selectStatement.executeQuery();

                while (resultSet.next()) {

                    Date existingDateOfWeekday = resultSet.getDate("dateOfWeekday");
                    String episodeResult = resultSet.getString("episode");
                    Date existingCorrectionDate = resultSet.getDate("dateOfCorrectionDate");

                    if (EnumWeekdays.isInCurrentWeek(existingDateOfWeekday)) {

                        int id = resultSet.getInt("id");

                        Timestamp existingReleaseTime = resultSet.getTimestamp("releaseTime");

                        if (!episodeResult.equals(episode.getEpisode())) {
                            return -1;
                        }

                        boolean needsUpdate = false;
                        if ((episode.getReleaseTime() != null && existingReleaseTime == null)
                                || (episode.getReleaseTime() != null
                                        && !episode.getReleaseTime().equals(existingReleaseTime))) {
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
                                updateStatement.setInt(4, id);
                                updateStatement.executeUpdate();
                                logger.warn("Updated episode: " + id + ", correction notification will be send");

                                Anime anime = getAnimeInformation(animeId);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm");

                                String formattedReleaseTime = dateFormat.format(episode.getReleaseTime());
                                NotificationService.sendNotificationInBlocks("Änderung zu einem Anime !!!",
                                        formattedReleaseTime + " - " + episode.getEpisode() + " - " + anime.getTitle(),
                                        anime.getCrunchyrollUrl(),
                                        animeId);
                            }
                        }

                        return id;
                    } else if (episodeResult.equals(episode.getEpisode()) && existingCorrectionDate == null) {
                        return -1;
                    }
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
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
                    logger.warn("inserted new episode with id: " + resultSet.getInt("id"));

                    return resultSet.getInt(1);
                } else {
                    throw new SQLException("Inserting episode failed, no ID obtained.");
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        throw new SQLException("Inserting episode failed, no ID obtained.");
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

                String selectQueryAnime = "SELECT title, imageurl, crunchyrollUrl FROM anime WHERE id = ?";
                PreparedStatement animeStatement = connection.prepareStatement(selectQueryAnime);
                animeStatement.setInt(1, animeId);
                try (ResultSet animeResultSet = animeStatement.executeQuery()) {
                    if (animeResultSet.next()) {
                        String title = animeResultSet.getString("title");
                        String imageUrl = animeResultSet.getString("imageurl");
                        String crunchyrollUrl = animeResultSet.getString("crunchyrollUrl");

                        Anime anime = new Anime(episode, animeId, title, imageUrl, crunchyrollUrl);
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
                logger.info("Marked episode as pushed (episodeID): " + episodeID);
            } else {
                logger.error("Cannot find episode with id: " + episodeID);
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
        LocalDateTime endOfSunday = null;
        if (sunday.getDayOfWeek() == DayOfWeek.SUNDAY) {
            endOfSunday = sunday.atTime(LocalTime.MAX);
        } else {
            while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
                sunday = sunday.plusDays(1);
                endOfSunday = sunday.atTime(LocalTime.MAX);
            }
        }

        String selectQueryEpisode = "SELECT id, anime_id, episode, releaseTime, dateOfWeekday, dateOfCorrectionDate FROM episodes WHERE dateofweekday >= ? AND dateofweekday <= ?";

        try (Connection connection = getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQueryEpisode)) {

                selectStatement.setTimestamp(1, Timestamp.valueOf(monday.atStartOfDay()));
                selectStatement.setTimestamp(2, Timestamp.valueOf(endOfSunday));
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

                    String selectQueryAnime = "SELECT id, title, imageurl, crunchyrollUrl FROM anime WHERE id = ?";
                    try (PreparedStatement selectStatementAnime = connection.prepareStatement(selectQueryAnime)) {
                        selectStatementAnime.setInt(1, animeId);

                        ResultSet animeResultSet = selectStatementAnime.executeQuery();
                        if (animeResultSet.next()) {
                            String title = animeResultSet.getString("title");
                            String imageUrl = animeResultSet.getString("imageurl");
                            String crunchyrollUrl = animeResultSet.getString("crunchyrollUrl");

                            Anime anime = new Anime(episode, animeId, title, imageUrl, crunchyrollUrl);
                            animeList.add(anime);
                        } else {
                            logger.error("Can not find anime with id: " + animeId);
                        }
                    }
                }
                return animeList;
            }
        }
    }

    public static void setToken(String token) {
        String insertQuery = "INSERT INTO tokens (token) VALUES (?)";

        try (Connection connection = getConnection();
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            insertStatement.setString(1, token);
            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                logger.info("Token inserted successfully: " + token);
            } else {
                logger.error("Failed to insert token: " + token);
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                logger.warn("Duplicate token: " + token);
            } else {
                logger.error("Error while inserting token", e);
            }
        }
    }

    public static void deleteToken(String token) {
        String deleteQuery = "DELETE FROM tokens WHERE token = ?";

        try (Connection connection = getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {

            deleteStatement.setString(1, token);
            int rowsDeleted = deleteStatement.executeUpdate();

            if (rowsDeleted > 0) {
                logger.info("Token deleted successfully: " + token);
            } else {
                logger.warn("Token not found for deletion: " + token);
            }

        } catch (SQLException e) {
            logger.error("Error while deleting token", e);
        }
    }

    public static List<String> getAllTokensForAnime(Integer animeId) {
        List<String> tokens = new ArrayList<>();
        String selectQuery = "SELECT t.token " +
                "FROM tokens t " +
                "JOIN anime_tokens at ON t.id = at.token_id " +
                "WHERE at.anime_id = ?";

        try (Connection connection = getConnection();
                PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {

            pstmt.setInt(1, animeId);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    tokens.add(resultSet.getString("token"));
                }
            }

        } catch (SQLException e) {
            logger.error("Error while retrieving tokens for animeId " + animeId, e);
        } catch (NumberFormatException e) {
            logger.error("Invalid animeId provided: " + animeId, e);
        }

        return tokens;
    }

    public static String changeAnimeSub(String token, String animeId) {
        String selectQueryToken = "SELECT id FROM tokens WHERE token = ?";

        String insertQueryToken = "INSERT INTO tokens (token) " +
                "VALUES (?)";

        String selectQuery = "SELECT 1 FROM anime_tokens WHERE token_id = (SELECT id FROM tokens WHERE token = ?) AND anime_id = ?";

        String insertQuery = "INSERT INTO anime_tokens (token_id, anime_id) " +
                "VALUES ((SELECT id FROM tokens WHERE token = ?), ?)";

        String deleteQuery = "DELETE FROM anime_tokens WHERE token_id = (SELECT id FROM tokens WHERE token = ?) AND anime_id = ?";

        try (Connection connection = getConnection();
                PreparedStatement pstmtSelectToken = connection.prepareStatement(selectQueryToken);
                PreparedStatement pstmtInsertToken = connection.prepareStatement(insertQueryToken);
                PreparedStatement pstmtSelect = connection.prepareStatement(selectQuery);
                PreparedStatement pstmtInsert = connection.prepareStatement(insertQuery);
                PreparedStatement pstmtDelete = connection.prepareStatement(deleteQuery)) {

            pstmtSelectToken.setString(1, token);
            ResultSet resultSetToken = pstmtSelectToken.executeQuery();

            if (!resultSetToken.next()) {
                pstmtInsertToken.setString(1, token);
                pstmtInsertToken.executeUpdate();
            }

            pstmtSelect.setString(1, token);
            pstmtSelect.setInt(2, Integer.parseInt(animeId));

            ResultSet resultSet = pstmtSelect.executeQuery();

            if (resultSet.next()) {
                pstmtDelete.setString(1, token);
                pstmtDelete.setInt(2, Integer.parseInt(animeId));
                pstmtDelete.executeUpdate();
                logger.info("Existing subscription for token " + token + " deleted. Animeid: " + animeId);
                return "deleted";
            }

            pstmtInsert.setString(1, token);
            pstmtInsert.setInt(2, Integer.parseInt(animeId));
            pstmtInsert.executeUpdate();
            logger.info("New subscription for token " + token + " added for animeId " + animeId);
            return "added";

        } catch (SQLException e) {
            logger.error("Error while updating anime subscription for token " + token, e);
            return "error";
        }
    }

    public static Anime getAnimeInformation(int animeId) {
        String selectQuery = "SELECT id, title, imageurl, crunchyrollurl FROM anime WHERE id = ?";
        Anime anime = null;

        try (Connection connection = getConnection();
                PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {

            pstmt.setInt(1, animeId);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    anime = new Anime();
                    anime.setAnimeId(resultSet.getInt("id"));
                    anime.setTitle(resultSet.getString("title"));
                    anime.setImageUrl(resultSet.getString("imageurl"));
                    anime.setCrunchyrollUrl(resultSet.getString("crunchyrollurl"));
                }
            }

        } catch (SQLException e) {
            logger.error("Error while retrieving anime information for animeId " + animeId, e);
        }

        return anime;
    }
}
