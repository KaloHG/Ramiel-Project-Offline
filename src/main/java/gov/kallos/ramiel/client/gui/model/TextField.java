package gov.kallos.ramiel.client.gui.model;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TextField extends Clickable {
    private TextFieldWidget textField;
    @Nullable
    private Predicate<String> validator;
    private int textColor = Color.WHITE.getRGB();
    private static final int mutedColor = 0x555566;
    @Nullable
    private String hint;
    @Nullable
    private Consumer<String> enterHandler = null;
    private boolean enabled = true;
    private final Consumer<String> guiResponder = s -> this.handleChanged();

    public TextField(@Nullable Predicate<String> validator, @Nullable String text) {
        this(validator, text, null);
    }

    public TextField(@Nullable Predicate<String> validator, @Nullable String text, @Nullable String hint) {
        this.validator = validator;
        this.hint = hint;
        this.textField = new TextFieldWidget(mc.textRenderer, 0, 0, 0, 0, (Text)new LiteralText("XXX Edit Box Message"));
        this.textField.setMaxLength(9999999);
        this.textField.setChangedListener(this.guiResponder);
        if (text != null) {
            this.setText(text);
        }
        this.textField.setCursorToStart();
        this.handleChanged();
        this.setMinSize(new Vec2(50, 20));
        this.setMaxSize(new Vec2(999999, 20));
    }

    private void handleChanged() {
        if (this.validator == null) {
            return;
        }
        boolean valid = false;
        try {
            valid = this.validator.test((String) this.getText());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (valid) {
            this.setColor(Color.WHITE);
        } else {
            this.setColor(Color.RED);
        }
    }

    public TextFieldWidget getTextField() {
        return this.textField;
    }

    public String getText() {
        return this.textField.getText();
    }

    public TextField setText(String text) {
        this.textField.setText(text);
        this.textField.setCursorToEnd();
        this.handleChanged();
        return this;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public TextField setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.textField.setEditable(enabled);
        return this;
    }

    public TextField setFocused(boolean focused) {
        if (this.textField.isFocused()) {
            return this;
        }
        this.textField.setTextFieldFocused(focused);
        this.textField.setCursorToEnd();
        return this;
    }

    public TextField setColor(Color color) {
        this.textColor = color.getRGB();
        this.textField.setEditableColor(this.textColor);
        return this;
    }

    public TextField onEnter(@Nullable Consumer<String> enterHandler) {
        this.enterHandler = enterHandler;
        return this;
    }

    @Override
    public void draw(MatrixStack poseStack, Vec2 mouse, Vec2 winSize, float partialTicks) {
        this.textField.renderButton(poseStack, mouse.x, mouse.y, partialTicks);
        if (this.textField.getText().isEmpty() && this.hint != null && !this.hint.isEmpty()) {
            int x = this.textField.x + 4;
            int y = this.textField.y + (this.getSize().y - 4 - 8) / 2;
            String hintTrimmed = mc.textRenderer.trimToWidth((StringVisitable)new LiteralText(this.hint), this.getSize().x - 8).getString();
            mc.textRenderer.draw(poseStack, hintTrimmed, (float)x, (float)y, 0x555566);
        }
    }

    @Override
    public void setPos(@NotNull Vec2 pos) {
        super.setPos(pos);
        this.textField.x = pos.x + 2;
        this.textField.y = pos.y + 2;
    }

    @Override
    public void updateSize(Vec2 size) {
        super.updateSize(size);
        TextFieldWidget old = this.textField;
        this.textField = new TextFieldWidget(mc.textRenderer, old.x, old.y, this.getSize().x - 4, this.getSize().y - 4, old.getMessage());
        this.textField.setEditable(this.enabled);
        this.textField.setMaxLength(9999999);
        this.textField.setChangedListener(this.guiResponder);
        this.textField.setText(old.getText());
        this.textField.setEditableColor(this.textColor);
        this.textField.setSelectionStart(old.getCursor());
        this.textField.setTextFieldFocused(old.isFocused());
    }

    @Override
    public void handleKeyPressed(int keyCode, int scanCode, int mods) {
        this.textField.keyPressed(keyCode, scanCode, mods);
        if (this.textField.isActive() && keyCode == 257 && this.enterHandler != null) {
            try {
                boolean valid;
                boolean bl = valid = this.validator == null || this.validator.test((String) this.getText());
                if (valid) {
                    this.enterHandler.accept((String) this.getText());
                    mc.getSoundManager().play((SoundInstance) PositionedSoundInstance.master((SoundEvent) SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
                } else {
                    mc.getSoundManager().play((SoundInstance)PositionedSoundInstance.master((SoundEvent)SoundEvents.ENTITY_VILLAGER_NO, (float)1.0f));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleCharTyped(char keyChar, int keyCode) {
        this.textField.charTyped(keyChar, keyCode);
    }

    @Override
    public boolean handleMouseClicked(Vec2 mouse, int mouseButton) {
        return this.textField.mouseClicked((double)mouse.x, (double)mouse.y, mouseButton);
    }
}
