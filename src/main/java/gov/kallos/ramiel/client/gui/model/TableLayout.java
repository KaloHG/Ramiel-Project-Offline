package gov.kallos.ramiel.client.gui.model;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TableLayout extends GuiElement {
    @Nullable
    private final List<List<GuiElement>> rows = new ArrayList<>();
    private int maxCols = 0;
    @Nullable
    private Layoutable1D[] rowLayouts;
    @Nullable
    private Layoutable1D[] colLayouts;

    public TableLayout addRow(List<@Nullable GuiElement> row) {
        this.rows.add(row);
        for (GuiElement child : row) {
            if (child == null) continue;
            child.setParent(this);
        }
        if (this.maxCols < row.size()) {
            this.maxCols = row.size();
        }
        this.invalidateLayout();
        return this;
    }

    public TableLayout updateRow(List<@Nullable GuiElement> row) {
        for (GuiElement child : row) {
            if (child == null) continue;
            child.setParent(this);
        }
        if (this.maxCols < row.size()) {
            this.maxCols = row.size();
        }
        this.invalidateLayout();
        return this;
    }

    public TableLayout clear() {
        this.rows.forEach(row -> row.forEach(guiElement -> {
            if (guiElement != null) {
                guiElement.handleDestroyed();
            }
        }));
        this.rows.clear();
        this.maxCols = 0;
        this.invalidateLayout();
        return this;
    }

    @Override
    public void invalidateLayout() {
        super.invalidateLayout();
        this.rowLayouts = null;
        this.colLayouts = null;
    }

    @Override
    public void updateSize(Vec2 sizeAvail) {
        Vec2 weight;
        int i;
        this.rowLayouts = new Layoutable1D[this.rows.size()];
        for (i = 0; i < this.rowLayouts.length; ++i) {
            this.rowLayouts[i] = new Layoutable1D(0, 0, 0);
        }
        this.colLayouts = new Layoutable1D[this.maxCols];
        for (i = 0; i < this.colLayouts.length; ++i) {
            this.colLayouts[i] = new Layoutable1D(0, 0, 0);
        }
        for (int rowNr = 0; rowNr < this.rows.size(); ++rowNr) {
            List<GuiElement> row = this.rows.get(rowNr);
            for (int colNr = 0; colNr < row.size(); ++colNr) {
                GuiElement cell = (GuiElement) row.get(colNr);
                if (cell == null) continue;
                Vec2 minSize = cell.getMinSize();
                Vec2 maxSize = cell.getMaxSize();
                weight = cell.getWeight();
                this.rowLayouts[rowNr].update(minSize.y, maxSize.y, weight.y);
                this.colLayouts[colNr].update(minSize.x, maxSize.x, weight.x);
            }
        }
        super.updateSize(new Vec2(Layoutable1D.computeLayout(sizeAvail.x, this.colLayouts), Layoutable1D.computeLayout(sizeAvail.y, this.rowLayouts)));
        int rowPos = 0;
        for (Layoutable1D rowLayout : this.rowLayouts) {
            rowLayout.pos = rowPos;
            rowPos += rowLayout.size;
        }
        int colPos = 0;
        for (Layoutable1D colLayout : this.colLayouts) {
            colLayout.pos = colPos;
            colPos += colLayout.size;
        }
        for (int rowNr = 0; rowNr < this.rows.size(); ++rowNr) {
            List row = (List)this.rows.get(rowNr);
            for (int colNr = 0; colNr < row.size(); ++colNr) {
                GuiElement cell = (GuiElement)row.get(colNr);
                if (cell == null) continue;
                weight = cell.getWeight();
                Vec2 minSize = cell.getMinSize();
                cell.updateSize(new Vec2(weight.x > 0 ? this.colLayouts[colNr].size : minSize.x, weight.y > 0 ? this.rowLayouts[rowNr].size : minSize.y));
            }
        }
    }

    @Override
    public void setPos(@NotNull Vec2 pos) {
        if (this.rowLayouts == null || this.rowLayouts.length != this.rows.size() || this.colLayouts == null || this.colLayouts.length != this.maxCols) {
            throw new IllegalStateException("setPos() was called before setSize()");
        }
        super.setPos(pos);
        for (int rowNr = 0; rowNr < this.rows.size(); ++rowNr) {
            List row = (List)this.rows.get(rowNr);
            int y = this.rowLayouts[rowNr].pos + pos.y;
            for (int colNr = 0; colNr < row.size(); ++colNr) {
                GuiElement child = (GuiElement)row.get(colNr);
                if (child == null) continue;
                int x = this.colLayouts[colNr].pos + pos.x;
                child.setPos(new Vec2(x, y));
            }
        }
    }

    @Override
    public Vec2 getSize() {
        if (this.rowLayouts != null && this.rowLayouts.length == this.rows.size() && this.colLayouts != null && this.colLayouts.length == this.maxCols) {
            Layoutable1D lastCol = this.colLayouts[this.colLayouts.length - 1];
            int colSum = lastCol.pos - this.colLayouts[0].pos + lastCol.size;
            Layoutable1D lastRow = this.rowLayouts[this.rowLayouts.length - 1];
            int rowSum = lastRow.pos - this.rowLayouts[0].pos + lastRow.size;
            return new Vec2(colSum, rowSum);
        }
        int maxSumW = 0;
        int sumMaxH = 0;
        for (List row : this.rows) {
            int sumW = 0;
            int maxH = 0;
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                Vec2 size = childElement.getSize();
                sumW += size.x;
                maxH = Math.max((int)maxH, (int)size.y);
            }
            maxSumW = Math.max((int)maxSumW, (int)sumW);
            sumMaxH += maxH;
        }
        return new Vec2(maxSumW, sumMaxH);
    }

    @Override
    @NotNull
    public Vec2 getWeight() {
        int maxX = 0;
        int sumY = 0;
        for (List row : this.rows) {
            int sumX = 0;
            int maxY = 0;
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                Vec2 weight = childElement.getWeight();
                sumX += weight.x;
                maxY = Math.max((int)maxY, (int)weight.y);
            }
            maxX = Math.max((int)maxX, (int)sumX);
            sumY += maxY;
        }
        return new Vec2(maxX, sumY);
    }

    @Override
    public Vec2 getMaxSize() {
        int maxW = 0;
        int sumH = 0;
        for (List row : this.rows) {
            int sumW = 0;
            int maxH = 0;
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                Vec2 size = childElement.getMaxSize();
                sumW += size.x;
                maxH = Math.max((int)maxH, (int)size.y);
            }
            maxW = Math.max((int)maxW, (int)sumW);
            sumH += maxH;
        }
        return new Vec2(maxW, sumH);
    }

    @Override
    public Vec2 getMinSize() {
        int maxW = 0;
        int sumH = 0;
        for (List row : this.rows) {
            int sumW = 0;
            int maxH = 0;
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                Vec2 size = childElement.getMinSize();
                sumW += size.x;
                maxH = Math.max((int)maxH, (int)size.y);
            }
            maxW = Math.max((int)maxW, (int)sumW);
            sumH += maxH;
        }
        return new Vec2(maxW, sumH);
    }

    @Override
    public void draw(MatrixStack poseStack, Vec2 mouse, Vec2 winSize, float partialTicks) {
        for (List row : this.rows) {
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                try {
                    childElement.draw(poseStack, mouse, winSize, partialTicks);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean drawOverlays(MatrixStack poseStack, Vec2 mouse, Vec2 winSize, float partialTicks) {
        for (List row : this.rows) {
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                try {
                    if (!childElement.drawOverlays(poseStack, mouse, winSize, partialTicks)) continue;
                    return true;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public boolean handleMouseClicked(Vec2 mouse, int mouseButton) {
        for (List row : this.rows) {
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                try {
                    if (!childElement.handleMouseClicked(mouse, mouseButton)) continue;
                    return true;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public void handleMouseDragged(Vec2 mouse, Vec2 prevMouse, Vec2 dragStart, int mouseButton) {
        for (List row : this.rows) {
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                try {
                    childElement.handleMouseDragged(mouse, prevMouse, dragStart, mouseButton);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void handleMouseReleased(Vec2 mouse, Vec2 dragStart, int state) {
        for (List row : this.rows) {
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                try {
                    childElement.handleMouseReleased(mouse, dragStart, state);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean handleMouseScrolled(Vec2 mouse, double scrollAmount) {
        for (List row : this.rows) {
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                try {
                    if (!childElement.handleMouseScrolled(mouse, scrollAmount)) continue;
                    return true;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public void handleKeyPressed(int keyCode, int scanCode, int mods) {
        for (List row : this.rows) {
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                try {
                    childElement.handleKeyPressed(keyCode, scanCode, mods);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void handleCharTyped(char keyChar, int keyCode) {
        for (List row : this.rows) {
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                try {
                    childElement.handleCharTyped(keyChar, keyCode);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void handleDestroyed() {
        for (List row : this.rows) {
            for (Object child : row) {
                if (child == null) continue;
                GuiElement childElement = (GuiElement) child;
                try {
                    childElement.handleDestroyed();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
