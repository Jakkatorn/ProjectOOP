public class DamageText {
    private int x, y;
    private int damage;
    private int lifetime = 30;

    public DamageText(int x, int y, int damage) {
        this.x = x;
        this.y = y;
        this.damage = damage;
    }

    public void update() {
        y -= 2;
        lifetime--;
    }

    public boolean isExpired() { return lifetime <= 0; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getDamage() { return damage; }
}