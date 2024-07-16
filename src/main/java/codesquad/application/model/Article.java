package codesquad.application.model;

public class Article {
    private final Long articleId;
    private final String title;
    private final String content;
    private final Author author;

    private Article(Long articleId, String title, String content, Author author) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public static Article create(Long articleId, String title, String content, User user) {
        return new Article(articleId, title, content, Author.create(user));
    }

    public static Article create(Long articleId, String title, String content, Author author) {
        return new Article(articleId, title, content, author);
    }

    public Long getArticleId() {
        return articleId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Author getAuthor() {
        return author;
    }
}
