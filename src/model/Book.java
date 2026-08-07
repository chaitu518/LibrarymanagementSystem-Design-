package model;

import util.BaseEntity;

public class Book extends BaseEntity {
    public String isbn, title, author;
    public int year;

    public Book(String isbn, String title, String author, int year) {
        super("BOOK");
        this.isbn = isbn; this.title = title; this.author = author; this.year = year;
    }
}
