package recommendation;

import model.Book;
import model.Patron;
import service.Library;

import java.util.List;

public interface RecommendationStrategy {
    List<Book> recommend(Patron patron, Library library, int limit);
}
