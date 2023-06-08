package gov.kallos.ramiel.client.model;

/**
 * Helpful wacky Location Class
 */
public class Location {

    double x;
    double y;
    double z;

    String world;

    public Location(double x, double y, double z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }
}
