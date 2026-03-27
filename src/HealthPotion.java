public class HealthPotion {
    private int x, y;
    private int healAmount;

    public HealthPotion(int x, int y, int slimeDamage) {
        this.x = x;
        this.y = y;
        this.healAmount = 50 + (slimeDamage / 2);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealAmount() { return healAmount; }
}