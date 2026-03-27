public class Slime extends Character implements Lootable {
    private int wave;

    public Slime(int currentWave) {
        super(50 + (currentWave * 20), 15 + (currentWave * 5));
        this.wave = currentWave;
    }

    public void moveTowards(Player player) {
        int speed = 2 + (this.wave / 4);
        if (speed > 4) speed = 4;

        if (this.getX() < player.getX()) this.setX(this.getX() + speed);
        if (this.getX() > player.getX()) this.setX(this.getX() - speed);
        if (this.getY() < player.getY()) this.setY(this.getY() + speed);
        if (this.getY() > player.getY()) this.setY(this.getY() - speed);
    }

    @Override
    public void die() {}

    public void knockback(Player player) {
        int knockbackPower = 60;
        int deltaX = this.getX() - player.getX();
        int deltaY = this.getY() - player.getY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > 0) {
            this.setX((int)(this.getX() + (deltaX / distance) * knockbackPower));
            this.setY((int)(this.getY() + (deltaY / distance) * knockbackPower));
        }
    }

    @Override
    public void dropLoot(Player player, WaveManager waveManager) {
        int randomExp = (int)(Math.random() * 16) + 35;
        player.gainExp(randomExp);

        int randomChance = (int)(Math.random() * 100);
        if(randomChance < 30) {
            HealthPotion potion = new HealthPotion(this.getX(), this.getY(), this.getDamage());
            waveManager.getPotions().add(potion);
        }
    }
}