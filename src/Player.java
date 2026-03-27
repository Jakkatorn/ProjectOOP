import java.util.ArrayList;
import java.util.List;

public class Player extends Character {
    private int level;
    private int exp;
    private int invincibilityTimer = 0;
    private int minDamage;
    private int maxDamage;
    private int score;
    private int direction = 1;
    private List<Fireball> fireballs = new ArrayList<>();
    private int levelUpTimer = 0;
    private int levelUpY = 0;

    public Player() {
        super(300, 30);
        this.level = 1;
        this.exp = 0;
        this.score = 0;
        this.minDamage = 30;
        this.maxDamage = 40;
    }

    public void updateTimer() {
        if (invincibilityTimer > 0) invincibilityTimer--;
        if (levelUpTimer > 0) {
            levelUpTimer--;
            levelUpY -= 2;
        }
    }

    @Override
    public void takeDamage(int dmg) {
        if (invincibilityTimer == 0) {
            super.takeDamage(dmg);
            invincibilityTimer = 40;
        }
    }

    @Override
    public void die() {}

    public void heal(int amount) {
        int newHp = getHp() + amount;
        setHp(Math.min(newHp, getMaxHp()));
    }

    public void heal() {
        setHp(getMaxHp());
    }

    public void gainExp(int amount) {
        this.exp += amount;
        int requiredExp = (this.level + 1) * 80;

        while (this.exp >= requiredExp) {
            this.exp -= requiredExp;
            this.level++;
            int hpGain = (this.level - 1) * 10;
            setMaxHp(getMaxHp() + hpGain);
            heal();
            this.minDamage += 10;
            this.maxDamage += 10;
            this.levelUpTimer = 60;
            this.levelUpY = this.getY() - 10;
            requiredExp = (this.level + 1) * 80;
        }
    }

    public void shootFireball() {
        int fireballDamage = calculateHitDamage();
        Fireball fb = new Fireball(getX() + 25, getY() + 25, direction, fireballDamage);
        fireballs.add(fb);
    }

    public int calculateHitDamage() {
        int dmg = (int)(Math.random() * ((maxDamage - minDamage) + 1)) + minDamage;
        int critChance = (int)(Math.random() * 100);
        if (critChance < 10) return dmg * 2;
        return dmg;
    }

    public void addScore(int amount) { this.score += amount; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public boolean isInvincible() { return invincibilityTimer > 0; }
    public int getDirection() { return direction; }
    public void setDirection(int direction) { this.direction = direction; }
    public List<Fireball> getFireballs() { return fireballs; }
    public int getLevelUpTimer() { return levelUpTimer; }
    public int getLevelUpY() { return levelUpY; }
}