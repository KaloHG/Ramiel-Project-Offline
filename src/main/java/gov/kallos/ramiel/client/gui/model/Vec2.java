package gov.kallos.ramiel.client.gui.model;

public class Vec2 {
    public static final int LARGE = 999999;
    public final int x;
    public final int y;

    public Vec2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2(double x, double y) {
        this.x = (int)x;
        this.y = (int)y;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Vec2 vec2 = (Vec2)o;
        if (this.x != vec2.x) {
            return false;
        }
        return this.y == vec2.y;
    }

    public int hashCode() {
        int result = this.x;
        result = 31 * result + this.y;
        return result;
    }

    public int getDim(Direction direction) {
        if (direction == Direction.HORIZONTAL) {
            return this.x;
        }
        return this.y;
    }

    public static Vec2 setDims(int main, int other, Direction direction) {
        if (direction == Direction.HORIZONTAL) {
            return new Vec2(main, other);
        }
        return new Vec2(other, main);
    }

    public String toString() {
        return "Vec2{" + this.x + ", " + this.y + "}";
    }

    public static enum Direction {
        HORIZONTAL,
        VERTICAL;


        public Direction other() {
            if (this == HORIZONTAL) {
                return VERTICAL;
            }
            return HORIZONTAL;
        }
    }
}
