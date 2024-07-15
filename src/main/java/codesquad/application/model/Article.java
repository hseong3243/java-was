package codesquad.application.model;

public class Article {
    private final Long articleId;
    private final String content;

    public Article(Long articleId, String content) {
        this.articleId = articleId;
        this.content = content;
    }
}
