package model;

public enum Element {
    HUMIDITE("humidite"),
    TEMPERATURE("temperature"),
    TEMPERATURE_EXT("temperature_ext"),
    ROSEE("rosee"),
    CONSIGNE("consigne"),
    PORTE("porte"),
    ETAT("etat");

    private String nom;

    Element(String nom) {
        this.nom = nom;
    }


    @Override
    public String toString() {
        return this.nom;
    }
}
