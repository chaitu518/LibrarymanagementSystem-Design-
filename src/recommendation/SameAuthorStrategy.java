package recommendation;

import model.Book;
import model.Patron;
import service.Library;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SameAuthorStrategy implements RecommendationStrategy {
    public List<Book> recommend(Patron patron, Library library, int limit) {
        Set<String> readIsbns = patron.history.stream().map(b -> b.isbn).collect(Collectors.toSet());
        Set<String> authors = readIsbns.stream()
            .map(library::findByIsbn).filter(Objects::nonNull)
            .map(b -> b.author).collect(Collectors.toSet());

        return library.books.values().stream()
            .filter(b -> authors.contains(b.author) && !readIsbns.contains(b.isbn))
            .limit(limit)
            .collect(Collectors.toList());
    }
}
