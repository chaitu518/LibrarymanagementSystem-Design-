package service;

import model.Book;
import model.Borrow;
import model.Branch;
import model.Patron;
import model.Reservation;
import notification.Notifier;
import recommendation.RecommendationStrategy;
import recommendation.SameAuthorStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.logging.Logger;

public class Library {
    private static Library instance;
    static Logger log = Logger.getLogger("Library");

    public Map<String, Book> books = new HashMap<>();
    public Map<String, Patron> patrons = new HashMap<>();
    public Map<String, Branch> branches = new HashMap<>();
    public Map<String, Borrow> borrows = new HashMap<>();
    public Map<String, Queue<Reservation>> reservations = new HashMap<>();
    public List<Notifier> notifiers = new ArrayList<>();
    public RecommendationStrategy recommender = new SameAuthorStrategy();

    private Library() {}

    public static synchronized Library getInstance() {
        if (instance == null) instance = new Library();
        return instance;
    }

    // Books
    public void addBook(Book b) { books.put(b.isbn, b); }
    public void removeBook(String isbn) { books.remove(isbn); }
    public Book findByIsbn(String isbn) { return books.get(isbn); }
    public List<Book> searchByTitle(String t) { return books.values().stream().filter(b -> b.title.equalsIgnoreCase(t)).toList(); }
    public List<Book> searchByAuthor(String a) { return books.values().stream().filter(b -> b.author.equalsIgnoreCase(a)).toList(); }

    // Patrons
    public Patron addPatron(String name, String email) {
        Patron p = new Patron(name, email);
        patrons.put(p.getId(), p);
        return p;
    }

    //  Branches & stock
    public Branch addBranch(String name) {
        Branch b = new Branch(name);
        branches.put(b.getId(), b);
        return b;
    }
    public void addStock(String branchId, String isbn, int copies) { branches.get(branchId).addStock(isbn, copies); }

    public boolean transfer(String fromId, String toId, String isbn, int qty) {
        Branch from = branches.get(fromId), to = branches.get(toId);
        // fixed lock order by id string avoids deadlock between concurrent transfers
        Branch first = fromId.compareTo(toId) < 0 ? from : to;
        Branch second = fromId.compareTo(toId) < 0 ? to : from;
        synchronized (first) {
            synchronized (second) {
                int avail = from.availableCopies.getOrDefault(isbn, 0);
                if (avail < qty) return false;
                for (int i = 0; i < qty; i++) from.checkout(isbn);
                from.totalCopies.merge(isbn, -qty, Integer::sum);
                to.addStock(isbn, qty);
                log.info("Transferred " + qty + " of " + isbn + " from " + fromId + " to " + toId);
                return true;
            }
        }
    }

    // Lending
    public Borrow checkout(String patronId, String isbn, String branchId)
    {
        Patron patron = patrons.get(patronId);
        Branch branch = branches.get(branchId);
        if (patron == null) throw new NoSuchElementException("Patron not found");
        if (branch == null) throw new NoSuchElementException("Branch not found");
        if (findByIsbn(isbn) == null) throw new NoSuchElementException("Book not found");
        if (!branch.checkout(isbn)) throw new IllegalStateException("No copies available");

        Borrow borrow = new Borrow(patronId, isbn, branchId);
        borrows.put(borrow.getId(), borrow);
        patron.history.add(borrow);
        log.info("Patron " + patronId + " checked out " + isbn + " (" + borrow.getId() + ")");
        return borrow;
    }

    // Returning
    public void returnBook(String borrowId) {
        Borrow borrow = borrows.get(borrowId);
        if (borrow == null || borrow.returned) throw new IllegalStateException("Invalid return");
        borrow.returned = true;
        branches.get(borrow.branchId).returnCopy(borrow.isbn);
        log.info("Returned " + borrowId);
        notifyNextReservation(borrow.isbn, borrow.branchId);
    }

    //  Reservations
    public void reserve(String patronId, String isbn, String branchId) {
        Reservation r = new Reservation(patronId, isbn, branchId);
        reservations.computeIfAbsent(branchId + "-" + isbn, k -> new LinkedList<>()).add(r);
        log.info("Patron " + patronId + " reserved " + isbn + " (" + r.getId() + ")");
    }

    // notify the next patron in the reservation queue when a book is returned
    private void notifyNextReservation(String isbn, String branchId) {
        Queue<Reservation> q = reservations.get(branchId + "-" + isbn);
        if (q == null || q.isEmpty()) return;
        Patron patron = patrons.get(q.poll().patronId);
        notifiers.forEach(n -> n.notify(patron, isbn));
    }

    // Recommendations
    public List<Book> recommend(Patron patron, int limit) { return recommender.recommend(patron, this, limit); }
}
