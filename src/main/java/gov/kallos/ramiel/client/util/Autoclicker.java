package gov.kallos.ramiel.client.util;

import gov.kallos.ramiel.client.RamielClient;
import gov.kallos.ramiel.client.mixin.MinecraftClientMixin;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class Autoclicker {
    int timerTick = 0;
    double delay;

    /**
     * Autoclicker const
     * @param enabled enabled
     * @param delay in ticks
     */
    public Autoclicker() {
        init();
    }

    private void init() {
        RamielClient.LOGGER.info("Registering autoclicker");
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(RamielClient.getInstance().getConfig().autoclickerEnabled()) {
                if (client.world != null) {
                    if (!(client.currentScreen instanceof InventoryScreen)) {
                        if (client.player.getMainHandStack() != null && (client.player.getMainHandStack().isOf(Items.NETHERITE_SWORD) || client.player.getMainHandStack().isOf(Items.DIAMOND_SWORD))) {
                            boolean mouseState = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
                            if (mouseState) {
                                timerTick++;
                                if (timerTick > cpsToTicks()) {
                                    leftClick();
                                    timerTick = 0;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public static void leftClick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.getInstance().options.attackKey.setPressed(true);
        ((MinecraftClientMixin) mc).leftClick();
        mc.options.attackKey.setPressed(false);
    }

    private float cpsToTicks() {
        int cps = RamielClient.getInstance().getConfig().getClicksPerSecond();
        return (float) 20 / cps;
    }
}
