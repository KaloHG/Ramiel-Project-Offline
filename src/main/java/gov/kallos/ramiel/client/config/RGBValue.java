package gov.kallos.ramiel.client.config;

/**
 * Utility RGB Value Storing Class.
 */
public class RGBValue {

    //Used by the config to ensure that an RGB value has been edited.
    public static final RGBValue NOT_EDITED = new RGBValue(-1,-1,-1);

    public int r;

    public int g;
    public int b;

    public RGBValue(int r, int g, int b) {
        this.r = r;
        this.b = b;
        this.g = g;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public int getRBox() {
        return 255 - r;
    }

    public int getGBox() {
        return 255 - g;
    }

    public int getBBox() {
        return 255 - b;
    }

    public void setR(int r) {
        this.r = r;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setB(int b) {
        this.b = b;
    }

    public boolean isEdited() {
        return r != -1 && g != -1 && b != -1;
    }
}
