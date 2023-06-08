package gov.kallos.ramiel.client.gui.model;

import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class FlexListLayout
        extends GuiElement {
    private final Vec2.Direction direction;
    private final ArrayList<GuiElement> children = new ArrayList();
    @Nullable
    private Layoutable1D[] childLayouts;

    public FlexListLayout(Vec2.Direction direction) {
        this.direction = direction;
    }

    public FlexListLayout add(GuiElement child) {
        this.children.add((GuiElement) child);
        child.setParent(this);
        this.invalidateLayout();
        return this;
    }

    public FlexListLayout insert(int index, GuiElement child) {
        this.children.add(index, (GuiElement) child);
        child.setParent(this);
        this.invalidateLayout();
        return this;
    }

    public FlexListLayout clear() {
        this.children.forEach(GuiElement::handleDestroyed);
        this.children.clear();
        this.invalidateLayout();
        return this;
    }

    @Override
    public void invalidateLayout() {
        super.invalidateLayout();
        this.childLayouts = null;
    }

    @Override
    public void updateSize(Vec2 sizeAvail) {
        this.childLayouts = new Layoutable1D[this.children.size()];
        for (int i = 0; i < this.childLayouts.length; ++i) {
            GuiElement child = (GuiElement)this.children.get(i);
            Vec2 minSize = child.getMinSize();
            Vec2 maxSize = child.getMaxSize();
            Vec2 weight = child.getWeight();
            this.childLayouts[i] = new Layoutable1D(minSize.getDim(this.direction), maxSize.getDim(this.direction), weight.getDim(this.direction));
        }
        int mainSize = Layoutable1D.computeLayout(sizeAvail.getDim(this.direction), this.childLayouts);
        int otherAvail = sizeAvail.getDim(this.direction.other());
        int otherSize = 0;
        int childPos = 0;
        for (int i = 0; i < this.childLayouts.length; ++i) {
            this.childLayouts[i].pos = childPos;
            childPos += this.childLayouts[i].size;
            GuiElement child = (GuiElement)this.children.get(i);
            child.updateSize(Vec2.setDims(this.childLayouts[i].size, otherAvail, this.direction));
            int childOtherSize = child.getSize().getDim(this.direction.other());
            otherSize = Math.max((int)otherSize, (int)childOtherSize);
        }
        super.updateSize(Vec2.setDims(mainSize, otherSize, this.direction));
    }

    @Override
    public void setPos(@NotNull Vec2 pos) {
        if (this.childLayouts == null || this.childLayouts.length != this.children.size()) {
            throw new IllegalStateException("setPos() was called before setSize()");
        }
        super.setPos(pos);
        int other = pos.getDim(this.direction.other());
        for (int i = 0; i < this.children.size(); ++i) {
            GuiElement child = (GuiElement)this.children.get(i);
            int main = this.childLayouts[i].pos + pos.getDim(this.direction);
            child.setPos(Vec2.setDims(main, other, this.direction));
        }
    }

    @Override
    @NotNull
    public Vec2 getWeight() {
        int sumMain = 0;
        int maxOther = 0;
        for (GuiElement child : this.children) {
            Vec2 weight = child.getWeight();
            sumMain += weight.getDim(this.direction);
            maxOther = Math.max((int)maxOther, (int)weight.getDim(this.direction.other()));
        }
        return Vec2.setDims(sumMain, maxOther, this.direction);
    }

    @Override
    public Vec2 getMaxSize() {
        int sumMain = 0;
        int maxOther = 0;
        for (GuiElement child : this.children) {
            Vec2 maxSize = child.getMaxSize();
            sumMain += maxSize.getDim(this.direction);
            maxOther = Math.max((int)maxOther, (int)maxSize.getDim(this.direction.other()));
        }
        return Vec2.setDims(sumMain, maxOther, this.direction);
    }

    @Override
    public Vec2 getMinSize() {
        int sumMain = 0;
        int maxOther = 0;
        for (GuiElement child : this.children) {
            Vec2 minSize = child.getMinSize();
            sumMain += minSize.getDim(this.direction);
            maxOther = Math.max((int)maxOther, (int)minSize.getDim(this.direction.other()));
        }
        return Vec2.setDims(sumMain, maxOther, this.direction);
    }

    @Override
    public void draw(MatrixStack poseStack, Vec2 mouse, Vec2 winSize, float partialTicks) {
        for (GuiElement child : this.children) {
            try {
                child.draw(poseStack, mouse, winSize, partialTicks);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean drawOverlays(MatrixStack poseStack, Vec2 mouse, Vec2 winSize, float partialTicks) {
        for (GuiElement child : this.children) {
            try {
                if (!child.drawOverlays(poseStack, mouse, winSize, partialTicks)) continue;
                return true;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean handleMouseClicked(Vec2 mouse, int mouseButton) {
        for (GuiElement child : this.children) {
            try {
                if (!child.handleMouseClicked(mouse, mouseButton)) continue;
                return true;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void handleMouseDragged(Vec2 mouse, Vec2 prevMouse, Vec2 dragStart, int mouseButton) {
        for (GuiElement child : this.children) {
            try {
                child.handleMouseDragged(mouse, prevMouse, dragStart, mouseButton);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleMouseReleased(Vec2 mouse, Vec2 dragStart, int state) {
        for (GuiElement child : this.children) {
            try {
                child.handleMouseReleased(mouse, dragStart, state);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean handleMouseScrolled(Vec2 mouse, double scrollAmount) {
        for (GuiElement child : this.children) {
            try {
                if (!child.handleMouseScrolled(mouse, scrollAmount)) continue;
                return true;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void handleKeyPressed(int keyCode, int scanCode, int mods) {
        for (GuiElement child : this.children) {
            try {
                child.handleKeyPressed(keyCode, scanCode, mods);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleCharTyped(char keyChar, int keyCode) {
        for (GuiElement child : this.children) {
            try {
                child.handleCharTyped(keyChar, keyCode);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleDestroyed() {
        for (GuiElement child : this.children) {
            try {
                child.handleDestroyed();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}