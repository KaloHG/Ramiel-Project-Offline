package gov.kallos.ramiel.client.config;

import gov.kallos.ramiel.client.RamielClient;
import gov.kallos.ramiel.client.model.Standing;
import org.json.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * Ramiel Configuration Class
 * Everything related to File Configuration goes in here.
 */
public class RamielConfiguration {

    //The Directory for the Mod (.minecraft/ramielclient)
    private File modDirectory;

    //The ClientConfiguration File (.minecraft/ramielclient/config.json)
    private File clientConfiguration;
    //The backup file for above.
    private File clientBackupConfiguration;

    //Standing RGB Values
    private RGBValue friendlyRgb;
    private RGBValue neutralRgb;
    private RGBValue enemyRgb;

    //Integer Values
    private int personalSpace;
    private int timeToDisappear;

    //Boolean Values
    private boolean renderHoops;
    private boolean renderHitboxes;

    private boolean autoclicker;

    private int clicksPerSecond;

    public RamielConfiguration(File modDirectory) {
        this.modDirectory = modDirectory;
        this.clientConfiguration = new File(modDirectory, "config.json");
        this.clientBackupConfiguration = new File(modDirectory, "config.json.old");
        initialize();
    }

    /**
     * Checks that config exists, and if not generates the default one.
     */
    private void initialize() {
        if(!clientConfiguration.exists()) {
            try {
                RamielClient.LOGGER.info("Configuration file did not exist, values won't be saved until client shutdown...");
                clientConfiguration.createNewFile();
                load(true);
            } catch (IOException ex) {
                ex.printStackTrace();
                RamielClient.LOGGER.error("Failed to create configuration file with default values.");
            }
        } else {
            load(false);
        }
    }

    /**
     * Loads all configuration values.
     * @param defaultValues whether the default values should be used or not.
     */
    public void load(boolean defaultValues) {
        try {
            InputStream is = new FileInputStream(clientConfiguration);
            Scanner reader = new Scanner(is);
            //Don't load the array unless we're not default values.
            JSONObject configObject = null;
            if(defaultValues) {
                this.friendlyRgb = DefaultValues.DEFAULT_FRIENDLY_COLOUR;
                this.enemyRgb = DefaultValues.DEFAULT_ENEMY_COLOUR;
                this.neutralRgb = DefaultValues.DEFAULT_NEUTRAL_COLOUR;
            } else {
                configObject = new JSONObject(reader.nextLine());
                JSONArray colourArray = configObject.getJSONArray("colours");
                JSONObject friendlyObj = colourArray.getJSONObject(0);
                JSONObject neutralObj = colourArray.getJSONObject(1);
                JSONObject enemyObj = colourArray.getJSONObject(2);
                this.friendlyRgb = new RGBValue(friendlyObj.getInt("r"),
                        friendlyObj.getInt("g"), friendlyObj.getInt("b"));
                this.neutralRgb = new RGBValue(neutralObj.getInt("r"),
                        neutralObj.getInt("g"), neutralObj.getInt("b"));
                this.enemyRgb = new RGBValue(enemyObj.getInt("r"),
                        enemyObj.getInt("g"), enemyObj.getInt("b"));
            }
            this.personalSpace = defaultValues ? DefaultValues.DEFAULT_PERSONAL_SPACE : configObject.getInt("personalSpace");
            this.timeToDisappear = defaultValues ? DefaultValues.DEFAULT_DISAPPEAR_TIME : configObject.getInt("timeToDisappear");
            //Json isn't wonderful and has a tendency to throw exceptions when new values are added. Eventually this will be negated with config versions.
            try {
                this.renderHoops = defaultValues ? DefaultValues.RENDER_HOOPS : configObject.getBoolean("renderHoops");
            } catch (JSONException exception) {
                //Could not find hoop render. Set default.
                this.renderHoops = DefaultValues.RENDER_HOOPS;
            }
            try {
                this.renderHitboxes = defaultValues ? DefaultValues.RENDER_HITBOXES : configObject.getBoolean("renderHitboxes");
            } catch (JSONException exception) {
                //Could not find hoop render. Set default.
                this.renderHitboxes = DefaultValues.RENDER_HITBOXES;
            }
            try {
                this.autoclicker = defaultValues ? DefaultValues.AUTOCLICKER : configObject.getBoolean("autoclicker");
                this.clicksPerSecond = defaultValues ? DefaultValues.CLICKS_PER_SECOND : configObject.getInt("cps");
            } catch (JSONException exception) {
                this.autoclicker = DefaultValues.AUTOCLICKER;
                this.clicksPerSecond = DefaultValues.CLICKS_PER_SECOND;
            }
            RamielClient.LOGGER.info("Loaded Configuration Successfully.");
            reader.close();
        } catch (FileNotFoundException | SecurityException ex) {
            ex.printStackTrace();
            RamielClient.LOGGER.error("Failed to load client configuration, the mod will not function.");
        }
    }

    /**
     * Saves all configuration values to files.
     */
    public void save() {
        JSONObject configObject = new JSONObject();

        JSONArray colourArray = new JSONArray();
        //Colours
        JSONObject friendlyColour = new JSONObject();
        friendlyColour.put("r", friendlyRgb.r);
        friendlyColour.put("g", friendlyRgb.g);
        friendlyColour.put("b", friendlyRgb.b);
        colourArray.put( friendlyColour);
        JSONObject neutralColour = new JSONObject();
        neutralColour.put("r", neutralRgb.r);
        neutralColour.put("g", neutralRgb.g);
        neutralColour.put("b", neutralRgb.b);
        colourArray.put( neutralColour);
        JSONObject enemyColour = new JSONObject();
        enemyColour.put("r", enemyRgb.r);
        enemyColour.put("g", enemyRgb.g);
        enemyColour.put("b", enemyRgb.b);
        colourArray.put( enemyColour);
        configObject.put("colours", colourArray);

        //Personal Space
        configObject.put("personalSpace", personalSpace);

        //Time to Disappear (in minutes)
        configObject.put("timeToDisappear", this.timeToDisappear);

        configObject.put("renderHoops", this.renderHoops);
        configObject.put("renderHitboxes", this.renderHitboxes);
        configObject.put("autoclicker", this.autoclicker);
        configObject.put("cps", this.clicksPerSecond);

        try {
            Files.copy(clientConfiguration.toPath(), clientBackupConfiguration.toPath(), StandardCopyOption.REPLACE_EXISTING);
            OutputStream os = new FileOutputStream(clientConfiguration, false);
            PrintWriter writer = new PrintWriter(os);
            writer.print(configObject);
            writer.flush();
            writer.close();
            RamielClient.LOGGER.info("Saved Client Configuration.");
        } catch (FileNotFoundException | SecurityException exception) {
            exception.printStackTrace();
            RamielClient.LOGGER.error("Failed to save configuration, check the stack to find the issue.");
        } catch (IOException e) {
            e.printStackTrace();
            RamielClient.LOGGER.error("Failed to backup current configuration.");
        }
    }

    /**
     * Fetches the RGB Value by the standing provided
     * @param standing the standing to get the RGB Value for
     * @return Associated RGBValue
     */
    public RGBValue getRgbByStanding(Standing standing) {
        switch (standing) {
            case SUS -> {
                return new RGBValue(Standing.SUS.r, Standing.SUS.g, Standing.SUS.b);
            }
            case ADMIN -> {
                return new RGBValue(Standing.ADMIN.r, Standing.ADMIN.g, Standing.ADMIN.b);
            }
            case ENEMY -> {
               return enemyRgb;
            }
            case FRIENDLY -> {
                return friendlyRgb;
            }
            case NEUTRAL -> {
                return neutralRgb;
            }
            case FOCUSED -> {
                return new RGBValue(Standing.FOCUSED.r, Standing.FOCUSED.g, Standing.FOCUSED.b);
            }
            default -> {
                return null; //Literally impossible chat.
            }
        }
    }

    public void setFriendlyRgb(RGBValue friendlyRgb) {
        if(friendlyRgb == RGBValue.NOT_EDITED) {
            return;
        }
        this.friendlyRgb = friendlyRgb;
    }

    public void setNeutralRgb(RGBValue neutralRgb) {
        if(neutralRgb == RGBValue.NOT_EDITED) {
            return;
        }
        this.neutralRgb = neutralRgb;
    }

    public void setEnemyRgb(RGBValue enemyRgb) {
        if(enemyRgb == RGBValue.NOT_EDITED) {
            return;
        }
        this.enemyRgb = enemyRgb;
    }

    public void setPersonalSpace(int personalSpace) {
        this.personalSpace = personalSpace;
    }

    public void setTimeToDisappear(int timeToDisappear) {
        this.timeToDisappear = timeToDisappear;
    }

    /**
     * Toggles the render boolean for hoops
     * @return the new value
     */
    public boolean toggleRenderHoops() {
        this.renderHoops = !renderHoops;
        return renderHoops;
    }

    public boolean renderHoops() {
        return renderHoops;
    }

    public boolean toggleRenderHitboxes() {
        this.renderHitboxes = !renderHitboxes;
        return renderHitboxes;
    }

    public boolean autoclickerEnabled() {
        return this.autoclicker;
    }

    public boolean toggleAutoclicker() {
        this.autoclicker = !autoclicker;
        return autoclicker;
    }

    public boolean renderHitboxes() {
        return renderHitboxes;
    }

    public RGBValue getFriendlyRgb() {
        return friendlyRgb;
    }

    public RGBValue getNeutralRgb() {
        return neutralRgb;
    }

    public RGBValue getEnemyRgb() {
        return enemyRgb;
    }

    public int getPersonalSpace() {
        return personalSpace;
    }

    public int getTimeToDisappear() {
        return timeToDisappear;
    }

    public int getClicksPerSecond() {
        return clicksPerSecond;
    }

    public void setClicksPerSecond(int clicksPerSecond) {
        this.clicksPerSecond = clicksPerSecond;
    }
}
