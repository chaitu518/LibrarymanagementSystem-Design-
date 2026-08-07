package model;

import util.BaseEntity;

import java.time.LocalDate;

public class Borrow extends BaseEntity {
    public String patronId, branchId, isbn;
    public LocalDate dueDate;
    public boolean returned = false;

    public Borrow(String patronId, String isbn, String branchId) {
        super("BORROW");
        this.patronId = patronId; this.isbn = isbn; this.branchId = branchId;
        this.dueDate = LocalDate.now().plusDays(10);
    }
}
