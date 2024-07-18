package codesquad.application.database;

import codesquad.application.model.Article;
import codesquad.application.model.Author;
import codesquad.application.util.DBConnectionUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleJdbcDatabase implements ArticleDatabase {

    private static final Logger log = LoggerFactory.getLogger(ArticleJdbcDatabase.class);

    @Override
    public Long save(Article article) {
        String sql = "insert into article(article_id, title, content, user_id, image_filename) values(?, ?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DBConnectionUtils.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, article.getArticleId());
            pstmt.setString(2, article.getTitle());
            pstmt.setString(3, article.getContent());
            pstmt.setString(4, article.getAuthor().getUserId());
            pstmt.setString(5, article.getImageFilename());
            pstmt.executeUpdate();
            return article.getArticleId();
        } catch (SQLException e) {
            log.error("SQL 에러", e);
            throw new IllegalArgumentException("SQL 예외가 발생했습니다.", e);
        } finally {
            DBConnectionUtils.closeConnection(con, pstmt, null);
        }
    }

    @Override
    public Long getNextId() {
        String sql = "select max(article_id) as next_id from article";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try  {
            con = DBConnectionUtils.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                return rs.getLong("next_id") + 1;
            } else {
                throw new IllegalArgumentException("다음 아이디값을 찾지 못했습니다.");
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("SQL 에러 발생", e);
        } finally {
            DBConnectionUtils.closeConnection(con, pstmt, rs);
        }
    }

    @Override
    public Optional<Article> findById(Long articleId) {
        String sql = "select * from article a"
                + " join users u on a.user_id = u.user_id"
                + " where article_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBConnectionUtils.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, articleId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Article article = Article.create(
                        rs.getLong("article_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        Author.create(
                                rs.getString("user_id"),
                                rs.getString("name")
                        )
                );
                return Optional.of(article);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalArgumentException("SQL 예외 발생", e);
        } finally {
            DBConnectionUtils.closeConnection(con, pstmt, rs);
        }
    }

    @Override
    public List<Article> findAll() {
        String sql = "select * from article a join users u on a.user_id = u.user_id";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBConnectionUtils.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            List<Article> articles = new ArrayList<>();
            while (rs.next()) {
                Article article = Article.create(
                        rs.getLong("article_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        Author.create(
                                rs.getString("user_id"),
                                rs.getString("name")
                        )
                );
                articles.add(article);
            }
            return articles;
        } catch (SQLException e) {
            throw new IllegalArgumentException("SQL 예외 발생", e);
        } finally {
            DBConnectionUtils.closeConnection(con, pstmt, rs);
        }
    }
}
