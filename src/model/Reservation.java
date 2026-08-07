package model;

import util.BaseEntity;

public class Reservation extends BaseEntity {
    public String patronId, isbn, branchId;

    public Reservation(String patronId, String isbn, String branchId) {
        super("RES");
        this.patronId = patronId; this.isbn = isbn; this.branchId = branchId;
    }
}
