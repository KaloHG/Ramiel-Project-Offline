package gov.kallos.ramiel.client.gui.model;

import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//TODO make fucking work. i spent 10 hours on this instead of getting something useful done.
public class Slider extends Clickable {
    private Text text;

    private String suffix;

    private double currentValue;

    private SliderAbstractWidget slider;

    public Slider(@Nullable Text text, String suffix, double value, double min, double max, double step) {
        if (text == null) {
            text = new LiteralText("");
        }
        this.text = text;
        this.suffix = suffix;
        int textWidth = mc.textRenderer.getWidth((StringVisitable) text);
        this.setMinSize(new Vec2(Math.max((int) 310, (int) (textWidth + 10)), 20));
        this.setMaxSize(new Vec2(380, 20));
        this.slider = new SliderAbstractWidget(this.getPos().x, this.getPos().y, text.getString(), suffix,
        this.getSize().x, this.getSize().y, min, max, step, value) {};
        this.currentValue = value;
    }

    @Override
    public void updateSize(Vec2 size) {
        super.updateSize(size);
        this.reconstructButton();
    }

    private void reconstructButton() {
        boolean wasEnabled = this.slider.active;
        this.slider = new SliderAbstractWidget(this.slider.x, this.slider.y, text.getString(), suffix,
                this.getSize().x, this.getSize().y, slider.min, slider.max, slider.step, currentValue) {};
        this.slider.active = wasEnabled;
    }

    @Override
    public void setPos(@NotNull Vec2 pos) {
        super.setPos(pos);
        this.slider.x = pos.x;
        this.slider.y = pos.y;
    }

    private abstract class SliderAbstractWidget extends SliderWidget{
        private double param;

        private final String title;

        private final String suffix;

        private final double min;
        private final double max;
        private final double step;

        public SliderAbstractWidget(int x, int y, String title, String suffix,
                                                  int width, int height, double min, double max, double step, double param) {
            super(x, y, width, height, text, (param - min) / (max - min));
            this.param = param;
            this.title = title;
            this.suffix = suffix;
            this.min = min;
            this.max = max;
            this.step = step;
            this.update();
        }

        public void update() {
            updateMessage();
            this.value = (this.param - min) / (max - min);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(new LiteralText(title + " " + this.value + " " + suffix));
        }

        @Override
        protected void applyValue() {
            param = MathHelper.lerp(this.value, min, max);
        }
    }

    @Override
    public void draw(MatrixStack poseStack, Vec2 mouse, Vec2 winsize, float partialTicks) {
        this.slider.render(poseStack, mouse.x, mouse.y, partialTicks);
    }
}
