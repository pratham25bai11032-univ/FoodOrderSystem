package model;

/**
 * Abstract base class representing any person in the system.
 * Defines shared fields (id, name) and enforces implementation of getDetails()
 * across all subclasses via an abstract method.
 */
public abstract class Person {
    protected final int    id;
    protected final String name;

    public Person(int id, String name) {
        this.id   = id;
        this.name = name;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int    getId()   { return id; }
    public String getName() { return name; }

    /**
     * Abstract method — every subclass MUST implement this.
     * Forces all person types to define how they describe themselves.
     */
    public abstract String getDetails();
}