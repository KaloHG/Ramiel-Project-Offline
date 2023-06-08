package gov.kallos.ramiel.client.gui.model;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GuiElement
        implements GuiParent {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    @Nullable
    protected GuiParent parent = null;
    @NotNull
    private Vec2 pos = new Vec2(0, 0);
    @NotNull
    private Vec2 size = new Vec2(0, 0);
    @NotNull
    private Vec2 weight = new Vec2(0, 0);
    @Nullable
    private Vec2 minSize;
    @Nullable
    private Vec2 maxSize;
    private boolean dirtyConstraints = true;

    public void setParent(@Nullable GuiParent parent) {
        if (parent != this.parent) {
            this.invalidateLayout();
            if (parent != null) {
                parent.invalidateLayout();
            }
        }
        this.parent = parent;
    }

    @Override
    public void invalidateLayout() {
        if (this.parent != null) {
            this.parent.invalidateLayout();
        }
    }

    @NotNull
    public Vec2 getPos() {
        return this.pos;
    }

    public void setPos(@NotNull Vec2 pos) {
        this.pos = pos;
    }

    public Vec2 getSize() {
        return this.size;
    }

    public void updateSize(Vec2 sizeAvail) {
        if (this.dirtyConstraints) {
            this.checkLayout();
        }
        Vec2 maxSize = this.getMaxSize();
        Vec2 minSize = this.getMinSize();
        this.size = new Vec2(Math.min((int)maxSize.x, (int)Math.max((int)minSize.x, (int)sizeAvail.x)), Math.min((int)maxSize.y, (int)Math.max((int)minSize.y, (int)sizeAvail.y)));
    }

    public GuiElement setFixedSize(@Nullable Vec2 size) {
        this.setMaxSize(size);
        this.setMinSize(size);
        this.setWeight(new Vec2(0, 0));
        return this;
    }

    @NotNull
    public Vec2 getWeight() {
        return this.weight;
    }

    public GuiElement setWeight(@NotNull Vec2 weight) {
        this.weight = weight;
        this.invalidateLayout();
        return this;
    }

    public Vec2 getMaxSize() {
        if (this.dirtyConstraints) {
            this.checkLayout();
        }
        return this.maxSize;
    }

    public GuiElement setMaxSize(@Nullable Vec2 size) {
        this.dirtyConstraints = true;
        this.maxSize = size;
        this.invalidateLayout();
        return this;
    }

    public Vec2 getMinSize() {
        if (this.dirtyConstraints) {
            this.checkLayout();
        }
        return this.minSize;
    }

    public GuiElement setMinSize(@Nullable Vec2 size) {
        this.dirtyConstraints = true;
        this.minSize = size;
        this.invalidateLayout();
        return this;
    }

    private void checkLayout() {
        if (this.minSize == null) {
            this.minSize = new Vec2(0, 0);
        }
        if (this.maxSize == null) {
            this.maxSize = new Vec2(999999, 999999);
        }
        if (this.maxSize.x < this.minSize.x) {
            this.maxSize = new Vec2(this.minSize.x, this.maxSize.y);
        }
        if (this.maxSize.y < this.minSize.y) {
            this.maxSize = new Vec2(this.maxSize.x, this.minSize.y);
        }
        this.dirtyConstraints = false;
    }

    public void draw(MatrixStack poseStack, Vec2 mouse, Vec2 winSize, float partialTicks) {
    }

    public boolean drawOverlays(MatrixStack poseStack, Vec2 mouse, Vec2 winSize, float partialTicks) {
        return false;
    }

    public boolean handleMouseClicked(Vec2 mouse, int mouseButton) {
        return false;
    }

    public void handleMouseDragged(Vec2 mouse, Vec2 prevMouse, Vec2 dragStart, int mouseButton) {
    }

    public void handleMouseReleased(Vec2 mouse, Vec2 dragStart, int state) {
    }

    public boolean handleMouseScrolled(Vec2 mouse, double scrollAmount) {
        return false;
    }

    public void handleKeyPressed(int keyCode, int scanCode, int mods) {
    }

    public void handleCharTyped(char keyChar, int keyCode) {
    }

    public void handleDestroyed() {
    }
}
