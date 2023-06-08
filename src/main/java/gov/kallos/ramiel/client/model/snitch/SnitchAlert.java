package gov.kallos.ramiel.client.model.snitch;

import gov.kallos.ramiel.client.model.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Also totally didn't steal this from Gjum
 */
public class SnitchAlert {
    public final long ts;
    public final UUID uuid;
    public final String accountName;
    public final String action;
    public final String group;
    public final String snitchName;

    public final Location location;

    public SnitchAlert(@NotNull String world, int x, int y, int z, long ts, @Nullable UUID uuid, @NotNull String accountName, @NotNull String action, @Nullable String group, @NotNull String snitchName) {
        this.location = new Location(x, y, z, world);
        this.ts = ts;
        this.action = action;
        this.uuid = uuid;
        this.accountName = accountName;
        this.snitchName = snitchName;
        this.group = group;
    }
}
