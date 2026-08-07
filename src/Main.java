import model.Book;
import model.Borrow;
import model.Branch;
import model.Patron;
import notification.EmailNotifier;
import service.Library;

public class Main {
    public static void main(String[] args) {
        Library library = Library.getInstance();
        library.notifiers.add(new EmailNotifier());

        Branch downtown = library.addBranch("Downtown");
        Branch uptown = library.addBranch("Uptown");
        System.out.println(downtown.getId() + " = Downtown, " + uptown.getId() + " = Uptown");

        library.addBook(new Book("ISBN-1", "Clean Code", "Robert Martin", 2008));
        library.addBook(new Book("ISBN-2", "Effective Java", "Joshua Bloch", 2018));
        library.addBook(new Book("ISBN-3", "1984", "George Orwell", 1949));
        library.addBook(new Book("ISBN-4", "The Clean Coder", "Robert Martin", 2011));

        library.addStock(downtown.getId(), "ISBN-1", 2);
        library.addStock(downtown.getId(), "ISBN-3", 1);
        library.addStock(uptown.getId(), "ISBN-2", 3);
        library.addStock(downtown.getId(), "ISBN-4", 1);

        Patron alice = library.addPatron("Alice", "alice@mail.com");
        Patron bob = library.addPatron("Bob", "bob@mail.com");
        System.out.println(alice.getId() + " = Alice, " + bob.getId() + " = Bob");

        Borrow b1 = library.checkout(bob.getId(), "ISBN-3", downtown.getId());
        library.reserve(alice.getId(), "ISBN-3", downtown.getId());
        library.returnBook(b1.getId());

        library.transfer(uptown.getId(), downtown.getId(), "ISBN-2", 1);
        System.out.println("Downtown now has " + downtown.availableCopies.get("ISBN-2") + " copies of ISBN-2");

        library.checkout(alice.getId(), "ISBN-1", downtown.getId());
        System.out.println("Recommended for Alice: " + library.recommend(alice, 3).stream().map(b -> b.title).toList());
    }
}
