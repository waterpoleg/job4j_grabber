package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.time.LocalDateTime;

public class SqlRuParse {

    private Post getSqlRuPostDetails(String link) throws Exception {
        Document doc = Jsoup.connect(link).get();
        String title = doc.select(".messageHeader").get(0).text();
        String description = doc.select(".msgBody").get(1).text();
        LocalDateTime created = new SqlRuDateTimeParser()
                .parse(doc.select(".msgFooter").text().split(" \\[")[0]);
        return new Post(title, link, description, created);
    }

    public static void main(String[] args) throws Exception {
        for (int i = 1; i < 5; i++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + i).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                Element parent = td.parent();
                System.out.println(parent.children().get(5).text());
                System.out.println(new SqlRuDateTimeParser().parse(parent.children().get(5).text()));
                System.out.println();
            }
        }
        Post post = new SqlRuParse().getSqlRuPostDetails("https://www.sql.ru/"
                + "forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
        System.out.println(post);
        System.out.println();
    }
}
