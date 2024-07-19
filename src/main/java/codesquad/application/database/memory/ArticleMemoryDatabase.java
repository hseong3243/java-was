package codesquad.application.database.memory;

import codesquad.application.database.ArticleDatabase;
import codesquad.application.model.Article;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ArticleMemoryDatabase implements ArticleDatabase {
    private final Map<Long, Article> database = new ConcurrentHashMap<>();

    @Override
    public Long save(Article article) {
        database.put(article.getArticleId(), article);
        return article.getArticleId();
    }

    @Override
    public Long getNextId() {
        return (long) (database.size() + 1);
    }

    @Override
    public Optional<Article> findById(Long articleId) {
        return Optional.ofNullable(database.get(articleId));
    }

    @Override
    public List<Article> findAll() {
        return database.values().stream().toList();
    }
}
