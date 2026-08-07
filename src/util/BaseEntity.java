package util;

public abstract class BaseEntity {
    private final String id;

    protected BaseEntity(String prefix) {
        this.id = IdGenerator.next(prefix);
    }

    public String getId() { return id; }
}
