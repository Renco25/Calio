package sk.upjs.ics.android.calio;

import java.io.Serializable;

public class Jedlo implements Serializable {
    public String nazov;
    public double kcal;
    public double bielkoviny;
    public double sacharidy;
    public double tuky;
    public double gramaz;
    public String obrazokUrl;

    public Jedlo() {
        // Potrebné pre Firebase
    }

    public String getNazov() {
        return nazov;
    }
}
