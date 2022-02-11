package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.html.Post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public Post detail(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        String title = doc.select(".messageHeader").get(0).text();
        String description = doc.select(".msgBody").get(1).text();
        LocalDateTime created = dateTimeParser.parse(doc.select(".msgFooter").text().split(" \\[")[0]);
        return new Post(title, link, description, created);
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            try {
                Document doc = Jsoup.connect(link + "/" + i).get();
                Elements row = doc.select(".postslisttopic");
                for (Element td : row) {
                    Post post = detail(td.child(0).attr("href"));
                    posts.add(post);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return posts;
    }

    public static void main(String[] args) throws Exception {
        DateTimeParser dateTimeParser = new SqlRuDateTimeParser();
        SqlRuParse sqlRuParse = new SqlRuParse(dateTimeParser);
        List<Post> posts = sqlRuParse.list("https://www.sql.ru/forum/job-offers");
        System.out.println(posts);
        System.out.println(sqlRuParse.detail("https://www.sql.ru/"
                + "forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t"));
    }
}
