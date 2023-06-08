package gov.kallos.ramiel.client.gui.model;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.Objects;

public class Label extends Clickable {
    private String text;
    public Alignment alignment;
    private int color = Color.WHITE.getRGB();
    private int height = 20;

    public static enum Alignment {
        ALIGN_LEFT,
        ALIGN_CENTER,
        ALIGN_RIGHT;

    }

    public Label(String text) {
        this.text = text;
        this.alignment = Alignment.ALIGN_LEFT;
        int width = mc.textRenderer.getWidth(text) - 1;
        this.setMinSize(new Vec2(width, this.height));
        this.setMaxSize(new Vec2(9999999, this.height));
        if (this.alignment == Alignment.ALIGN_CENTER) {
            this.setWeight(new Vec2(1, 0));
        } else {
            this.setWeight(new Vec2(0, 0));
        }
    }

    public Label align(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public Label setText(String text) {
        this.text = text;
        int width = mc.textRenderer.getWidth(text) - 1;
        if (this.getMinSize().x < width) {
            this.setMinSize(new Vec2(width, this.height));
        }
        return this;
    }

    public Label setColor(Color color) {
        this.color = color.getRGB();
        return this;
    }

    public Label setHeight(int height) {
        this.height = height;
        this.setMinSize(new Vec2(this.getMinSize().x, height));
        this.setMaxSize(new Vec2(this.getMaxSize().x, height));
        return this;
    }

    @Override
    public void draw(MatrixStack poseStack, Vec2 mouse, Vec2 winSize, float partialTicks) {
        int x = switch (this.alignment) {
            case ALIGN_CENTER -> {
                int w = mc.textRenderer.getWidth(this.text) - 1;
                yield this.getPos().x + (this.getSize().x - w) / 2;
            }
            case ALIGN_LEFT -> this.getPos().x;
            case ALIGN_RIGHT -> {
                int w = mc.textRenderer.getWidth(this.text) - 1;
                yield this.getPos().x + (this.getSize().x - w);
            }
            default -> throw new IllegalStateException("Unexpected alignment " + this.alignment);
        };
        int n = this.getSize().y;
        Objects.requireNonNull((Object) mc.textRenderer);
        int dy = (n - 9) / 2;
        LiteralText displayText = new LiteralText(this.text);
        if (this.clickHandler != null && this.isMouseInside(mouse)) {
            displayText.setStyle(displayText.getStyle().withUnderline(Boolean.valueOf((boolean)true)));
        }
        mc.textRenderer.drawWithShadow(poseStack, (Text)displayText, (float)x, (float)(this.getPos().y + 1 + dy), this.color);
    }
}
