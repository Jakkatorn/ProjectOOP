public abstract class Character {
    private int x, y;
    private int hp, maxHp;
    private int damage;

    public Character(int maxHp, int damage) {
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.damage = damage;
    }

    public void takeDamage(int dmg) {
        this.hp -= dmg;
        if (this.hp <= 0) die();
    }

    public abstract void die();

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
}