package notification;

import model.Patron;

import java.util.logging.Logger;

public class EmailNotifier implements Notifier {
    static Logger log = Logger.getLogger("Library");

    public void notify(Patron patron, String isbn) {
        log.info("Email to " + patron.email + ": '" + isbn + "' is available now");
    }
}
