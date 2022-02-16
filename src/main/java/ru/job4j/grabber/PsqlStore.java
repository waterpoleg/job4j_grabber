package ru.job4j.grabber;

import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) throws ClassNotFoundException, SQLException {
        Class.forName(cfg.getProperty("driver-class-name"));
        cnn = DriverManager.getConnection(
                cfg.getProperty("url"),
                cfg.getProperty("username"),
                cfg.getProperty("password")
        );
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "insert into post (title, description, link, created) values (?,?,?,?)"
        )) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement prGetAll = cnn.prepareStatement(
                "select * from post"
        )) {
            try (ResultSet resultSet = prGetAll.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(getPostFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from post where id = ?"
        )) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post = getPostFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream in = new FileInputStream("src/main/resources/grabber.properties")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private Post getPostFromResultSet(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getString("link"),
                resultSet.getTimestamp("created").toLocalDateTime()
        );
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Parse sqlRuParse = new SqlRuParse(new SqlRuDateTimeParser());
        try (PsqlStore store = new PsqlStore(getProperties())) {
            List<Post> posts = sqlRuParse.list("https://www.sql.ru/forum/job-offers/");
            posts.forEach(store::save);
            List<Post> postsFromDb = store.getAll();
            postsFromDb.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
