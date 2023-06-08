package gov.kallos.ramiel.client.model;

/**
 * Cached RamielPlayer class, used for clientside
 */
public class RamielPlayer {

    private Location location;

    private Standing standing;

    private final String username;

    private String main;

    //In milliseconds
    private long lastUpdate;

    public RamielPlayer(String username, Standing standing) {
        this.standing = standing;
        this.username = username;
        this.main = null;
        this.location = null;
        this.lastUpdate = 0;
    }

    public RamielPlayer(String username, Standing standing, Location location) {
        this.standing = standing;
        this.username = username;
        this.location = location;
        this.main = null;
        this.lastUpdate = System.currentTimeMillis();
    }

    public void updateLocation(Location loc, long milis) {
        if (this.lastUpdate < milis) {
            this.location = loc;
            this.lastUpdate = milis;
        }
    }

    public Location getLocation() {
        return location;
    }

    public Standing getStanding() {
        return standing;
    }

    public String getUsername() {
        return username;
    }

    public String getMain() { return main; }

    public boolean isAlt() { return main != null; }

    public void setMain(String main) { this.main = main; }

    public void setStanding(Standing standing) {
        this.standing = standing;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
}
