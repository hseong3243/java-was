package codesquad.application.database;

import codesquad.application.model.Article;
import java.util.List;
import java.util.Optional;

public interface ArticleDatabase {
    Long save(Article article);

    Long getNextId();

    Optional<Article> findById(Long articleId);

    List<Article> findAll();
}
