package gov.kallos.ramiel.client.gui.model;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Clickable extends GuiElement {
    private static int nextId = 1;
    @Nullable
    public Consumer<Clickable> clickHandler = null;

    protected static int getId() {
        return nextId++;
    }

    public <T extends Clickable> T onClick(@Nullable Consumer<T> clickHandler) {
        this.clickHandler = (Consumer<Clickable>) clickHandler;
        return (T)this;
    }

    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean handleMouseClicked(Vec2 mouse, int mouseButton) {
        if (mouseButton != 0) {
            return false;
        }
        if (!this.isEnabled()) {
            return false;
        }
        if (!this.isMouseInside(mouse)) {
            return false;
        }
        if (this.clickHandler == null) {
            return false;
        }
        mc.getSoundManager().play((SoundInstance) PositionedSoundInstance.master((SoundEvent) SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        try {
            this.clickHandler.accept((Clickable) this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    boolean isMouseInside(Vec2 mouse) {
        if (this.getPos() == null) {
            return false;
        }
        return mouse.x >= this.getPos().x && mouse.y >= this.getPos().y && mouse.x < this.getPos().x + this.getSize().x && mouse.y < this.getPos().y + this.getSize().y;
    }
}
