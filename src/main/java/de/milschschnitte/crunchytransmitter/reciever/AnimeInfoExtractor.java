package de.milschschnitte.crunchytransmitter.reciever;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AnimeInfoExtractor {
    public static List<Anime> extractAnime(String html, String weekday) {
        // Parse the HTML content
        Document doc = Jsoup.parse(html);

        List<Anime> animeList = new ArrayList<>();

        // Extracting data
        Elements rows = doc.select("tr");

        for (int i = 1; i < rows.size() - 1; i++) {
            Element row = rows.get(i);

            Elements cols = row.select("td");

            if (cols.size() > 0) {
                for( int j = 0; j < cols.size(); j++ ) {
                    Element col = cols.get(j);

                    Anime anime = new Anime();
                    anime.setWeekday(weekday);

                    Elements aElements = col.select("a");

                    if (!aElements.isEmpty()) {
                        String title = "";
                        for (Element aElement : aElements) {
                            title += aElement.text() + " ";
                        }

                        // System.out.println("Title: " + title);
                        anime.setTitle(title);
                        animeList.add(anime);
                        System.out.println("added anime");
                        continue;
                    }

                    Elements pElements = col.select("p");

                    if (!pElements.isEmpty() && animeList.size() != 0) {
                        String episodeRaw = pElements.size() > 0 ? pElements.get(0).text() : "No episode info";
                        String time = pElements.size() > 1 ? pElements.get(1).text() : "No time info";

                        int length = episodeRaw.length();
                        String episode = episodeRaw.substring(5, length - 3);

                        System.out.println(animeList.size() + " " + (cols.size() - j));
                        animeList.get(animeList.size() - (cols.size() - j)).setEpisodes(episode);
                        animeList.get(animeList.size() - (cols.size() - j)).setReleaseTime(time);

                        /*
                         * Freitag
No p elements found in this td.
added anime
Anime{Title='NIJIYON ANIMATION 2 ', Episode='', ReleaseTime='', Weekday='Freitag', TimestampOfWeekday=''}
added anime
added anime
2 2
2 1
Anime{Title='Astro Note ', Episode='Folge 8', ReleaseTime='16:00 Uhr', Weekday='Freitag', TimestampOfWeekday=''}
Anime{Title='Schleim (Staffel 3) ', Episode='Folge 56', ReleaseTime='17:30 Uhr', Weekday='Freitag', TimestampOfWeekday=''}
                         */

                    } else {
                        System.out.println("No p elements found in this td.");
                    }
                }
            } else {
                System.out.println("No td elements found in this tr.");
            }
            // System.out.println();
        }
        return animeList;
    }
}
