import java.awt.Rectangle;

public class Fireball {
    private int x, y;
    private int direction;
    private int speed;
    private int damage;
    private int size = 50;
    private boolean isExpired = false;

    public Fireball(int x, int y, int direction, int damage) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.damage = damage;
        this.speed = 10;
    }

    public void update() {
        if (direction == 0) y -= speed;
        if (direction == 1) x += speed;
        if (direction == 2) y += speed;
        if (direction == 3) x -= speed;

        if (x < 0 || x > 1200 || y < 0 || y > 900) {
            isExpired = true;
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, size, size);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
    public int getDamage() { return damage; }
    public int getDirection() { return direction; }
    public boolean isExpired() { return isExpired; }
    public void setExpired(boolean expired) { this.isExpired = expired; }
}