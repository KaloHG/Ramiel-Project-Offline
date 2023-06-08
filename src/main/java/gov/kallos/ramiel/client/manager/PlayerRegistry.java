package gov.kallos.ramiel.client.manager;

import com.google.common.io.Files;
import gov.kallos.ramiel.client.RamielClient;
import gov.kallos.ramiel.client.model.Location;
import gov.kallos.ramiel.client.model.RamielPlayer;
import gov.kallos.ramiel.client.model.Standing;
import net.minecraft.entity.player.PlayerEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Keeps all information about players seen by this client.
 */
public class PlayerRegistry {

    private static PlayerRegistry INSTANCE;

    //Eventually we'll switch to this to allow standing configuration.
    private final File SAVE_FILE = new File(RamielClient.DIRECTORY, "players.txt");

    private final File BACKUP_FILE = new File(RamielClient.DIRECTORY, "players.txt.old");

    //All players cached by the client.
    private ConcurrentHashMap<String, RamielPlayer> cachedPlayers = new ConcurrentHashMap<>();

    //Players to be sent to the server.
    private ConcurrentLinkedQueue<RamielPlayer> pendingOutbound = new ConcurrentLinkedQueue<>();

    public PlayerRegistry() {
    }

    public RamielPlayer getOrCreatePlayer(String username) {
        if (cachedPlayers.containsKey(username)) {
            return cachedPlayers.get(username);
        } else {
            RamielClient.LOGGER.info("Created new RamielPlayer Profile for " + username);
            cachedPlayers.put(username, new RamielPlayer(username, Standing.NEUTRAL));
        }
        return cachedPlayers.get(username);
    }

    public RamielPlayer getOrCreatePlayer(PlayerEntity player) {
        if (cachedPlayers.containsKey(player.getDisplayName().getString())) {
            return cachedPlayers.get(player.getDisplayName().getString());
        } else {
            RamielClient.LOGGER.info("Created new RamielPlayer Profile for " + player.getDisplayName().getString());
            cachedPlayers.put(player.getDisplayName().getString(), new RamielPlayer(player.getDisplayName().getString(), Standing.NEUTRAL));
        }
        return cachedPlayers.get(player.getDisplayName().getString());
    }

    public void clearFocus() {
        for(RamielPlayer player : cachedPlayers.values()) {
            if(player.getStanding() == Standing.FOCUSED) {
                //TODO Eventually load standing back in from memory
                player.setStanding(Standing.ENEMY);
            }
        }
    }

    /*
    Updates internal location for cached player, may be a snitch hit or radar ping.
     */
    public void updateLocation(PlayerEntity entity, Location location) {
        RamielPlayer player = getOrCreatePlayer(entity);
        if(!pendingOutbound.contains(player)) {
            pendingOutbound.add(player);
        }
        player.updateLocation(location, System.currentTimeMillis());
    }

    public Collection<RamielPlayer> fetchPlayersInCache() {
        return cachedPlayers.values();
    }

    public ConcurrentLinkedQueue<RamielPlayer> getPendingOutbound() {
        return pendingOutbound;
    }

    public void removePlayerFromPending(RamielPlayer player) {
        pendingOutbound.remove(player);
    }

    public void loadFromStorage() {
        if (!SAVE_FILE.exists()) {
            try {
                if (SAVE_FILE.createNewFile()) {
                    RamielClient.LOGGER.info("Created new players.txt file.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                RamielClient.LOGGER.error("Failed to create players.txt file!");
            }
            return;
        }
        //TODO finish loading using Scanners yippeeeeee
    }

    public void saveToStorage() {
        if (!SAVE_FILE.exists()) {
            try {
                if (SAVE_FILE.createNewFile()) {
                    RamielClient.LOGGER.info("Created new players.txt file.");
                } else {
                    RamielClient.LOGGER.info("An error didn't occur, but for some reason a new players.txt file wasn't created...");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                RamielClient.LOGGER.error("Failed to create players.txt file!");
                return;
            }
        }
        try {
            PrintWriter writer = new PrintWriter(SAVE_FILE);
            Files.copy(SAVE_FILE, BACKUP_FILE);
            RamielClient.LOGGER.info("Saved backup file.");
            //TODO continue file writing output
        } catch (IOException ex) {
            ex.printStackTrace();
            RamielClient.LOGGER.error("Failed to write Player Cache Contents to players.txt file!");
        }
    }

    /**
     * Currently loads standings from a json online and loads it into memory
     * @param url the url for the json file
     */
    public void loadStandingsFromRepository(String url) {
        try {
            JSONArray standingsArray = readJsonFromUrl(url);
            RamielClient.LOGGER.info("Approximately " + standingsArray.length() + " standings loaded from JSON...");
            Iterator it = standingsArray.iterator();
            while (it.hasNext()) {
                JSONObject standObj = (JSONObject) it.next();
                RamielClient.LOGGER.info("Player JSON Loaded for: " + standObj.getString("username") + " at standing: " + standObj.getString("standing"));
                RamielPlayer newPlayer = new RamielPlayer(standObj.getString("username"), Standing.valueOf(standObj.getString("standing")));
                if(!standObj.getString("main").equals("")) {
                    newPlayer.setMain(standObj.getString("main"));
                }
                cachedPlayers.put(standObj.getString("username"), newPlayer);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            RamielClient.LOGGER.error("Failed to acquire standings list, the URL may be incorrect?");
        }
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static PlayerRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerRegistry();
        }
        return INSTANCE;
    }
}
