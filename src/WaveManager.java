import java.util.ArrayList;
import java.util.List;

public class WaveManager {
    private int currentWave = 1;
    private List<Slime> slimes = new ArrayList<>();
    private List<HealthPotion> potions = new ArrayList<>();
    private List<DamageText> damageTexts = new ArrayList<>();
    private Player player;
    private int countdown = 0;

    public WaveManager(Player player) {
        this.player = player;
        startWave();
    }

    public void startWave() {
        slimes.clear();
        int slimeCount = 3 + (currentWave * 2);

        for (int i = 0; i < slimeCount; i++) {
            Slime s = new Slime(currentWave);
            if (Math.random() < 0.5) {
                s.setX(Math.random() < 0.5 ? -50 : 1250);
                s.setY((int)(Math.random() * 900));
            } else {
                s.setX((int)(Math.random() * 1200));
                s.setY(Math.random() < 0.5 ? -50 : 950);
            }
            slimes.add(s);
        }
        countdown = 0;
    }

    public void checkWaveEnd() {
        if (slimes.isEmpty() && countdown == 0) {
            countdown = 3;
            new Thread(() -> {
                try {
                    while(countdown > 0) {
                        Thread.sleep(1000);
                        countdown--;
                    }
                    currentWave++;
                    startWave();
                } catch(Exception e){}
            }).start();
        }
    }

    public int getCurrentWave() { return currentWave; }
    public List<Slime> getSlimes() { return slimes; }
    public List<HealthPotion> getPotions() { return potions; }
    public List<DamageText> getDamageTexts() { return damageTexts; }
    public Player getPlayer() { return player; }
    public boolean isCountingDown() { return countdown > 0; }
    public int getCountdown() { return countdown; }
}