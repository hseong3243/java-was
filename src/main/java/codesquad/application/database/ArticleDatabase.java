package codesquad.application.database;

import codesquad.application.model.Article;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ArticleDatabase {
    private final Map<Long, Article> database = new ConcurrentHashMap<>();

    public Long save(Article article) {
        database.put(article.getArticleId(), article);
        return article.getArticleId();
    }

    public Long getNextId() {
        return (long) (database.size() + 1);
    }

    public Optional<Article> findById(Long articleId) {
        return Optional.ofNullable(database.get(articleId));
    }
}
