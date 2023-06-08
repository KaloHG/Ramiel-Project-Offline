package gov.kallos.ramiel.client.gui.model;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public abstract class GuiRoot extends Screen implements GuiParent {
    public final Screen parentScreen;
    @Nullable
    private GuiElement root = null;
    private boolean dirtyLayout = false;
    private Vec2 prevSize = null;
    private Vec2 prevMouse = new Vec2(0, 0);
    private Vec2 dragStart = null;

    public GuiRoot(Screen parentScreen, Text title) {
        super(title);
        this.parentScreen = parentScreen;
        this.client = MinecraftClient.getInstance();
    }

    public abstract GuiElement build();

    /**
     * If elements need to be added/removed, call this to rebuild the whole layout tree and elements list.
     */
    public void rebuild() {
        if (this.root != null) {
            this.root.handleDestroyed();
        }
        this.root = null;
        this.dirtyLayout = true;
    }

    protected void handleError(Throwable e) {
        e.printStackTrace();
        client.setScreen(parentScreen);
    }

    @Override
    public void init() {
        try {
            rebuild();
        } catch (Throwable e) {
            handleError(e);
        }
    }

    /**
     * Mark the layout to need to be recomputed on the next render.
     * Useful when the size of an element changed.
     */
    @Override
    public void invalidateLayout() {
        dirtyLayout = true;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        try {
            Vec2 newSize;
            this.renderBackground(matrices);

            if (root == null) {
                root = build();
                root.setParent(this);
                prevSize = null;
                dirtyLayout = true;
            }
            if (!(newSize = new Vec2(this.width, this.height)).equals(this.prevSize)) {
                this.prevSize = newSize;
                this.dirtyLayout = true;
            }
            if (this.dirtyLayout) {
                this.root.updateSize(newSize);
                this.root.setPos(new Vec2(0, 0));
                this.dirtyLayout = false;
            }
            Vec2 mousePos = new Vec2(mouseX, mouseY);
            this.root.draw(matrices, mousePos, newSize, partialTicks);
            this.root.drawOverlays(matrices, mousePos, newSize, partialTicks);
        } catch (Throwable e) {
            handleError(e);
        }
    }

    public boolean mouseClicked(double x, double y, int mouseButton) {
        try {
            if (this.root != null) {
                this.root.handleMouseClicked(new Vec2(x, y), mouseButton);
            }
            this.dragStart = new Vec2(x, y);
            this.prevMouse = new Vec2(x, y);
        } catch (Throwable e) {
            this.handleError(e);
        }
        return false;
    }

    public boolean mouseDragged(double x, double y, int clickedMouseButton, double xPrev, double yPrev) {
        try {
            if (this.root != null) {
                this.root.handleMouseDragged(new Vec2(x, y), this.prevMouse, this.dragStart, clickedMouseButton);
            }
            this.prevMouse = new Vec2(x, y);
        } catch (Throwable e) {
            this.handleError(e);
        }
        return false;
    }

    public boolean mouseReleased(double x, double y, int state) {
        try {
            if (this.root != null) {
                this.root.handleMouseReleased(new Vec2(x, y), this.dragStart != null ? this.dragStart : this.prevMouse, state);
            }
        } catch (Throwable e) {
            this.handleError(e);
        }
        this.dragStart = null;
        return false;
    }

    public boolean mouseScrolled(double x, double y, double scrollAmount) {
        try {
            if (scrollAmount == 0.0) {
                return false;
            }
            if (this.root != null) {
                this.root.handleMouseScrolled(new Vec2(x, y), scrollAmount);
            }
        } catch (Throwable e) {
            this.handleError(e);
        }
        return false;
    }

    public void handleEscape() {
        this.client.setScreen(null);
    }

    public boolean keyPressed(int keyCode, int scanCode, int mods) {
        try {
            if (this.root != null) {
                this.root.handleKeyPressed(keyCode, scanCode, mods);
            }
            if (keyCode == 256) {
                this.handleEscape();
            }
        } catch (Throwable e) {
            this.handleError(e);
        }
        return true;
    }

    public boolean charTyped(char keyChar, int keyCode) {
        try {
            if (this.root != null) {
                this.root.handleCharTyped(keyChar, keyCode);
            }
        } catch (Throwable e) {
            this.handleError(e);
        }
        return true;
    }

    public void close() {
        try {
            if (this.root != null) {
                this.root.handleDestroyed();
            }
        } catch (Throwable e) {
            this.handleError(e);
        }
    }
}
