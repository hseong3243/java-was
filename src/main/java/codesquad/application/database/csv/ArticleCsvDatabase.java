package codesquad.application.database.csv;

import codesquad.application.database.ArticleDatabase;
import codesquad.application.model.Article;
import codesquad.application.model.Author;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArticleCsvDatabase implements ArticleDatabase {
    @Override
    public Long save(Article article) {
        String sql = "insert into board values (''{0}'', ''{1}'', ''{2}'', ''{3}'', ''{4}'')";
        sql = MessageFormat.format(sql, article.getArticleId(), article.getTitle(), article.getContent(),
                article.getAuthor().getUserId(), article.getImageFilename());

        Connection con = null;
        Statement stmt = null;
        try {
            con = CSVConnectionUtil.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            return article.getArticleId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CSVConnectionUtil.closeConnection(con, stmt, null);
        }
    }

    @Override
    public Long getNextId() {
        String sql = "select * from board";

        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = CSVConnectionUtil.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count++;
            }
            return (long) (count + 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CSVConnectionUtil.closeConnection(con, stmt, rs);
        }
    }

    @Override
    public Optional<Article> findById(Long articleId) {
        String sql = "select * from board where boardId = ''{0}''";
        sql = MessageFormat.format(sql, articleId);

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = CSVConnectionUtil.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String findArticleId = rs.getString("boardId");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String userId = rs.getString("userId");
                String imageFilename = rs.getString("imageFilename");
                String username = findUsername(userId);
                Article article = Article.create(
                        Long.parseLong(findArticleId), title, content, Author.create(userId, username)
                );
                if (imageFilename != null) {
                    article.setImage(imageFilename);
                }
                return Optional.of(article);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CSVConnectionUtil.closeConnection(con, stmt, rs);
        }
    }

    private String findUsername(String userId) {
        String sql = "select * from user where userId = ''{0}''";
        sql = MessageFormat.format(sql, userId);

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = CSVConnectionUtil.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString("name");
            }
            return "";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CSVConnectionUtil.closeConnection(con, stmt, rs);
        }
    }

    @Override
    public List<Article> findAll() {
        String sql = "select * from board";

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Article> articles = new ArrayList<>();
        try {
            con = CSVConnectionUtil.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String findArticleId = rs.getString("boardId");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String userId = rs.getString("userId");
                String imageFilename = rs.getString("imageFilename");
                String username = findUsername(userId);
                Article article = Article.create(
                        Long.parseLong(findArticleId), title, content, Author.create(userId, username)
                );
                if (imageFilename != null) {
                    article.setImage(imageFilename);
                }
                articles.add(article);
            }
            return articles;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
