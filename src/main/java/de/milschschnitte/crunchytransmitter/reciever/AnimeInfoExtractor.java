package de.milschschnitte.crunchytransmitter.reciever;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 */
public class AnimeInfoExtractor {
    static Logger logger = LogManager.getLogger(AnimeInfoExtractor.class);

    public static List<Anime> extractAnime(String html, EnumWeekdays weekday) {
        // Parse the HTML content
        Document doc = Jsoup.parse(html);

        List<Anime> animeList = new ArrayList<>();

        // Extracting datanull;
        Elements rows = doc.select("tr");
        List<String> imageUrlList = new ArrayList<String>();
        List<String> crunchyrollUrlList = new ArrayList<String>();
        List<String> titleList = new ArrayList<String>();

        // The 1st and 2nd and 3rd only have relevant information
        /*
         * Structure explaned
         *  1 tr    (td image)     (td image)
         *  2 tr    (td Anime)     (td Anime)
         *  3 tr    (td EpisodeI)  (td EpisodeI)
         *    tr useless shit ...
         */
        for (int i = 0; i <= 3; i++) {
            Element row;
            try {
                row = rows.get(i);
            } catch (Exception e) {
                break;
            }

            Elements cols = row.select("td");

            if (cols.size() > 0) {
                for (int j = 0; j < cols.size(); j++) {

                    Element col = cols.get(j);

                    if (i == 0) {
                        String imageUrl = col.select("img").attr("src");
                        imageUrlList.add(imageUrl);

                        String crunchyrollUrl = col.select("a").attr("href");
                        crunchyrollUrl = crunchyrollUrl.replace("\\\"", "").replace("\\\\", "\\");
                        crunchyrollUrlList.add(crunchyrollUrl);
                    } else if (i == 1) {
                        Elements strongElements = col.select("strong");

                        if (!strongElements.isEmpty()) {
                            String title = "";
                            for (Element sElement : strongElements) {
                                title += sElement.text() + " ";
                            }
                            titleList.add(title);
                        }
                    } else if (i == 2) {
                        Elements pElements = col.select("p");
                        if (!pElements.isEmpty() && titleList.size() != 0) {
                            String episodeRaw = "";
                            if(pElements.size() > 0){
                                episodeRaw =  pElements.get(0).text();
                                Elements imageElements = pElements.get(0).getElementsByTag("img");
                                if(imageElements.size() > 0){
                                    if(imageElements.get(0).attr("src").contains("deutschland-flagge")){
                                        continue;
                                    }
                                }
                            }
                             
                            int length = episodeRaw.length();

                            if (length >= 4) {
                                int index = episodeRaw.indexOf('F');
                                if (index != -1) {
                                    episodeRaw = episodeRaw.substring(index, length - 3);
                                } else {
                                    logger.warn("Faulty episodeRaw, skip anime: " + titleList.get(j));
                                    continue;
                                }
                            }

                            String time = pElements.size() > 1 ? pElements.get(1).text() : "";

                            String correctionDate = "";
                            if (pElements.size() > 2 && time.endsWith("*"))
                                correctionDate = pElements.get(2).text();

                            Anime anime = new Anime();
                            anime.setImageUrl(imageUrlList.get(j));
                            anime.setCrunchyrollUrl(crunchyrollUrlList.get(j));
                            anime.setTitle(titleList.get(j));
                            
                            Episode episode = anime.getEpisode();
                            episode.setDateOfWeekday(weekday);
                            episode.setEpisodes(episodeRaw);
                            episode.setReleaseTime(time);
                            if (!correctionDate.equals(""))
                                episode.setDateOfCorretionDate(correctionDate);
                            
                            animeList.add(anime);

                        } else {
                            logger.warn("No p elements found in this td. This is not good");
                        }
                    }
                }
            } else {
                // System.out.println("No td elements found in this tr.");
            }
        }
        return animeList;
    }
}
