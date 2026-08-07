package model;

import util.BaseEntity;

import java.util.HashMap;
import java.util.Map;

public class Branch extends BaseEntity {
    public String name;
    public Map<String, Integer> totalCopies = new HashMap<>();     // isbn -> total
    public Map<String, Integer> availableCopies = new HashMap<>(); // isbn -> available

    public Branch(String name) {
        super("BRANCH");
        this.name = name;
    }

    public void addStock(String isbn, int copies) {
        totalCopies.merge(isbn, copies, Integer::sum);
        availableCopies.merge(isbn, copies, Integer::sum);
    }

    public synchronized boolean checkout(String isbn) {
        int avail = availableCopies.getOrDefault(isbn, 0);
        if (avail <= 0) return false;
        availableCopies.put(isbn, avail - 1);
        return true;
    }

    public synchronized void returnCopy(String isbn) {
        availableCopies.merge(isbn, 1, Integer::sum);
    }
}
