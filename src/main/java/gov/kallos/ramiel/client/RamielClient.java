package gov.kallos.ramiel.client;

import gov.kallos.ramiel.client.config.RamielConfiguration;
import gov.kallos.ramiel.client.gui.ConfigGUI;
import gov.kallos.ramiel.client.manager.PlayerRegistry;
import gov.kallos.ramiel.client.model.RamielPlayer;
import gov.kallos.ramiel.client.model.Standing;
import gov.kallos.ramiel.client.util.Autoclicker;
import gov.kallos.ramiel.client.util.OutlineUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Timer;

public class RamielClient implements ClientModInitializer {

    private static RamielClient INSTANCE;

    public static final Logger LOGGER = LoggerFactory.getLogger("ramiel");
    public static final File DIRECTORY = new File(MinecraftClient.getInstance().runDirectory, "ramielclient");

    public static final String STABILITY = "BETA";

    public static final double VERSION = 1.2;

    private RamielConfiguration CONFIG;

    private Timer locationTimer = new Timer("Ramiel Location Scheduler");

    public static final String STANDINGS_REPO = "https://pastebin.com/raw/pAyuwiNV";

    public static final String ALLOWED_SERVER = "pvp.citadelpvp.net";

    private static final String PREFIX = Formatting.DARK_GRAY + "[" + Formatting.GOLD + "Ramiel" + Formatting.DARK_GRAY + "] ";

    private int maxWaypointDist = 5000; //Default of 5000 Meters.

    private boolean enabled;

    public static RamielClient getInstance() { return INSTANCE; }

    public boolean visualsEnabled() { return enabled; }

    public int getMaxWaypointDist() { return maxWaypointDist; }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        enabled = true; //Enabled by default.
        CONFIG = new RamielConfiguration(DIRECTORY);
        LOGGER.info("-=<*>=- KALLOS GOVERNMENT PROPERTY -=<*>=-");
        LOGGER.info("UNAUTHORIZED USERS WILL BE PEARLED");
        LOGGER.info("Attention! This is a " + STABILITY + " BUILD");
        LOGGER.info("Currently active version " + VERSION + "-" + STABILITY);
        DIRECTORY.mkdir();



        LOGGER.info("Starting our json load...");
        PlayerRegistry.getInstance().loadStandingsFromRepository(STANDINGS_REPO);

        //Register autoclicker info
        Autoclicker clicker = new Autoclicker();

        EntityRendererRegistry.INSTANCE.register(EntityType.PLAYER, (dispatcher) -> new OutlineUtil(dispatcher, false));

        // Register the event handler for ticking
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Get the local player entity
            if(client.world != null) {
                for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10));
                }
            }
        });

        // Register the event handler for rendering entities
        ClientEntityEvents.ENTITY_LOAD.register((entity, clientWorld) -> {
            // Check if the entity is the local player
            if (entity instanceof AbstractClientPlayerEntity playerEntity) {
                // Apply the glow effect to the player
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10));
            }
        });
        registerBinds();
        CONFIG.save();
    }

    private PlayerEntity getPlayerUnderCrosshair() {
        try {
            if(MinecraftClient.getInstance().crosshairTarget.getType() == HitResult.Type.BLOCK) { return null; }
            Entity entityHit = MinecraftClient.getInstance().targetedEntity;
            if(!(entityHit instanceof PlayerEntity)) return null;
            final PlayerEntity player = (PlayerEntity) entityHit;
            return player;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private void registerBinds() {
        KeyBinding modEnableBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Visuals Toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_INSERT, "Ramiel"));
        KeyBinding incrementDistanceBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Increment Waypoint Distance", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_EQUAL, "Ramiel"));
        KeyBinding decrementDistanceBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Decrement Waypoint Distance", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS, "Ramiel"));
        KeyBinding modConfigScreenBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Config GUI", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, "Ramiel"));
        KeyBinding focusPlayerBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Focus Player", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_MIDDLE, "Ramiel"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(modEnableBind.wasPressed()) {
                enabled = !enabled;
                String status = enabled ? "Enabled" : "Disabled";
                client.player.sendMessage(new LiteralText(PREFIX + Formatting.GRAY + "Visuals " + status), false);
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            }
            while(modConfigScreenBind.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new ConfigGUI(MinecraftClient.getInstance().currentScreen));
            }
            while(incrementDistanceBind.wasPressed()) {
                if(maxWaypointDist >= 12000) {
                    //Do nothing.
                    maxWaypointDist = 12000;
                } else {
                    if(maxWaypointDist >= 1000) {
                        maxWaypointDist = maxWaypointDist + 500;
                    } else {
                        //Sub 1000 increment in 100's
                        maxWaypointDist = maxWaypointDist + 100;
                    }
                }
                client.player.sendMessage(new LiteralText(PREFIX + Formatting.GRAY + "Increased Waypoint Distance to " + Formatting.AQUA + maxWaypointDist), false);
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            }
            while(decrementDistanceBind.wasPressed()) {
                if(maxWaypointDist <= 0) {
                    //Do nothing.
                    maxWaypointDist = 0;
                } else {
                    if(maxWaypointDist >= 1000) {
                        maxWaypointDist = maxWaypointDist - 500;
                    } else {
                        //Sub 1000 increment in 100's
                        maxWaypointDist = maxWaypointDist - 100;
                    }
                }
                client.player.sendMessage(new LiteralText(PREFIX + Formatting.GRAY + "Decreased Waypoint Distance to " + Formatting.AQUA + maxWaypointDist), false);
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }
            while(focusPlayerBind.wasPressed()) {
                if(getPlayerUnderCrosshair() != null) {
                    sendPlayerMessage("Focusing " + getPlayerUnderCrosshair().getDisplayName().getString());
                    PlayerRegistry.getInstance().clearFocus(); //Clear all focused players.
                    RamielPlayer player = PlayerRegistry.getInstance().getOrCreatePlayer(getPlayerUnderCrosshair().getDisplayName().getString());
                    if(player.getStanding() == Standing.FOCUSED) {
                        PlayerRegistry.getInstance().clearFocus();
                        continue;
                    } else {
                        player.setStanding(Standing.FOCUSED);
                    }
                    client.player.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT, 1, 1);
                }
            }
        });
    }

    public RamielConfiguration getConfig() {
        return CONFIG;
    }

    public void sendPlayerMessage(String text) {
        MinecraftClient.getInstance().player.sendMessage(new LiteralText(PREFIX + Formatting.GRAY + " " + text), false);
    }
}
