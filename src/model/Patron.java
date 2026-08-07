package model;

import util.BaseEntity;

import java.util.ArrayList;
import java.util.List;

public class Patron extends BaseEntity {
    public String name, email;
    public List<Borrow> history = new ArrayList<>();

    public Patron(String name, String email) {
        super("PATRON");
        this.name = name; this.email = email;
    }
}
