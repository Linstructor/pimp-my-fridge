package model;

public enum InfoChange {
    TYPE("type"),
    VALEUR("valeur"),
    CONNECTION("connection");

    private String nom;

    InfoChange(String nom) {
        this.nom = nom;
    }


    @Override
    public String toString() {
        return this.nom;
    }
}
