package gov.kallos.ramiel.client.model;

public enum Standing {

    /*
    Green
     */
    FRIENDLY(84, 249, 117),

    /*
    White
     */
    NEUTRAL(252, 252, 252),

    /*
    Oange
    @Deprecated //TODO Remove next update.
     */
    SUS(252, 181, 75),

    ADMIN(198, 43, 196),

    FOCUSED(214, 2, 168),

    /*
    Red
     */
    ENEMY(252, 83, 83);

    public int r;
    public int g;
    public int b;

    Standing(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
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
}
