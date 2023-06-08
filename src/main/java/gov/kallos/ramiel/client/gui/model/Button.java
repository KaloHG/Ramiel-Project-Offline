package gov.kallos.ramiel.client.gui.model;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Button extends Clickable {
    private Text text;
    private ButtonWidget button;

    public Button(@Nullable String text) {
        this((Text)(text == null ? null : new LiteralText(text)));
    }

    public Button(@Nullable Text text) {
        if (text == null) {
            text = new LiteralText("");
        }
        this.text = text;
        int textWidth = mc.textRenderer.getWidth((StringVisitable)text);
        this.setMinSize(new Vec2(Math.max((int)20, (int)(textWidth + 10)), 20));
        this.setMaxSize(new Vec2(380, 20));
        this.button = new ButtonWidget(0, 0, this.getSize().x, this.getSize().y, text, null);
    }

    public Text getText() {
        return this.text;
    }

    public Button setText(Text text) {
        this.text = text;
        int textWidth = mc.textRenderer.getWidth((StringVisitable)text);
        this.setMinSize(new Vec2(Math.max((int)20, (int)(textWidth + 10)), this.getMinSize().y));
        this.reconstructButton();
        return this;
    }

    @Override
    public boolean isEnabled() {
        return this.button.active;
    }

    public Button setEnabled(boolean enabled) {
        this.button.active = enabled;
        return this;
    }

    @Override
    public void draw(MatrixStack poseStack, Vec2 mouse, Vec2 winSize, float partialTicks) {
        this.button.renderButton(poseStack, mouse.x, mouse.y, partialTicks);
    }

    @Override
    public void setPos(@NotNull Vec2 pos) {
        super.setPos(pos);
        this.button.x = pos.x;
        this.button.y = pos.y;
    }

    @Override
    public void updateSize(Vec2 size) {
        super.updateSize(size);
        this.reconstructButton();
    }

    private void reconstructButton() {
        boolean wasEnabled = this.button.active;
        this.button = new ButtonWidget(this.button.x, this.button.y, this.getSize().x, this.getSize().y, this.text, null);
        this.button.active = wasEnabled;
    }
}
