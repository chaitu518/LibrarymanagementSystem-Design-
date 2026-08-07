package notification;

import model.Patron;

public interface Notifier {
    void notify(Patron patron, String isbn);
}
