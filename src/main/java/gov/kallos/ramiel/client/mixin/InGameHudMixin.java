package gov.kallos.ramiel.client.mixin;

import gov.kallos.ramiel.client.RamielClient;
import gov.kallos.ramiel.client.manager.PlayerRegistry;
import gov.kallos.ramiel.client.model.chat.ChatMatches;
import gov.kallos.ramiel.client.model.snitch.SnitchAlert;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    /**
     * Handles chat messages, currently marks them as a snitch location
     */
    @Inject(method = "addChatMessage", at = @At("HEAD"))
    public void handleChat(MessageType type, Text message, UUID sender, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isInSingleplayer()) {
            if (ChatMatches.getSnitchAlertFromChat(message, "world") != null) {
                SnitchAlert newSnitch = ChatMatches.getSnitchAlertFromChat(message,
                        "world");
                RamielClient.LOGGER.info("Logged Snitch Internally. It should be visible now.");
                PlayerRegistry.getInstance().getOrCreatePlayer(newSnitch.accountName).updateLocation(newSnitch.location, newSnitch.ts);
            }
        }
    }

}
