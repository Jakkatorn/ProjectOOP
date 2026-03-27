import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel implements KeyListener, MouseListener {
    private enum State { START, PLAYING, GAMEOVER }
    private State gameState = State.START;

    private boolean up, down, left, right;
    private WaveManager waveManager;
    private Player player;

    private Image[] playerImgs = new Image[4];
    private Image slimeImg, potionImg, fireballImg, bgImg;

    private Rectangle btnStart = new Rectangle(500, 450, 200, 60);
    private Rectangle btnRestart = new Rectangle(500, 500, 200, 60);
    private Rectangle btnExit = new Rectangle(500, 600, 200, 60);

    public GamePanel(WaveManager waveManager) {
        this.waveManager = waveManager;
        this.player = waveManager.getPlayer();

        playerImgs[0] = new ImageIcon("player_up.gif").getImage();
        playerImgs[1] = new ImageIcon("player_right.gif").getImage();
        playerImgs[2] = new ImageIcon("player_down.gif").getImage();
        playerImgs[3] = new ImageIcon("player_left.gif").getImage();
        slimeImg = new ImageIcon("slime.gif").getImage();
        potionImg = new ImageIcon("potion.gif").getImage();
        fireballImg = new ImageIcon("fireball.gif").getImage();
        bgImg = new ImageIcon("background.png").getImage();

        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        Timer timer = new Timer(1000 / 60, e -> updateGame());
        timer.start();
    }

    private void restartGame() {
        this.player = new Player();
        this.player.setX(575);
        this.player.setY(425);
        this.waveManager = new WaveManager(this.player);
        this.gameState = State.PLAYING;
        up = false; down = false; left = false; right = false;
    }

    private void updateGame() {
        if (gameState != State.PLAYING) return;

        player.updateTimer();
        if (player.getHp() <= 0) { gameState = State.GAMEOVER; return; }

        int speed = 5;
        if (up) { player.setY(player.getY() - speed); player.setDirection(0); }
        if (down) { player.setY(player.getY() + speed); player.setDirection(2); }
        if (left) { player.setX(player.getX() - speed); player.setDirection(3); }
        if (right) { player.setX(player.getX() + speed); player.setDirection(1); }

        if (player.getX() < 0) player.setX(0);
        if (player.getX() > 1150) player.setX(1150);
        if (player.getY() < 0) player.setY(0);
        if (player.getY() > 820) player.setY(820);

        Iterator<Fireball> fbIterator = player.getFireballs().iterator();
        while (fbIterator.hasNext()) {
            Fireball fb = fbIterator.next();
            fb.update();

            if (fb.isExpired()) { fbIterator.remove(); continue; }

            Rectangle fbBox = fb.getHitbox();
            Iterator<Slime> slimeIterator = waveManager.getSlimes().iterator();
            while (slimeIterator.hasNext()) {
                Slime slime = slimeIterator.next();
                Rectangle slimeBox = new Rectangle(slime.getX(), slime.getY(), 40, 40);

                if (fbBox.intersects(slimeBox)) {
                    int damage = fb.getDamage();
                    slime.takeDamage(damage);
                    slime.knockback(player);

                    waveManager.getDamageTexts().add(new DamageText(slime.getX() + 10, slime.getY() - 10, damage));
                    fb.setExpired(true);

                    if (slime.getHp() <= 0) {
                        slime.dropLoot(player, waveManager);
                        player.addScore(10);
                        slimeIterator.remove();
                    }
                    fbIterator.remove();
                    break;
                }
            }
        }

        Rectangle playerBox = new Rectangle(player.getX(), player.getY(), 50, 50);
        Iterator<HealthPotion> potionIterator = waveManager.getPotions().iterator();
        while (potionIterator.hasNext()) {
            HealthPotion potion = potionIterator.next();
            Rectangle potionBox = new Rectangle(potion.getX(), potion.getY(), 20, 20);
            if (playerBox.intersects(potionBox)) {
                player.heal(potion.getHealAmount());
                potionIterator.remove();
            }
        }

        for (Slime slime : waveManager.getSlimes()) {
            slime.moveTowards(player);
            Rectangle slimeBox = new Rectangle(slime.getX(), slime.getY(), 40, 40);
            if (playerBox.intersects(slimeBox)) { player.takeDamage(slime.getDamage()); }
        }

        Iterator<DamageText> textIterator = waveManager.getDamageTexts().iterator();
        while (textIterator.hasNext()) {
            DamageText dt = textIterator.next();
            dt.update();
            if (dt.isExpired()) { textIterator.remove(); }
        }

        repaint();
    }

    private void playerAttack() {
        if (gameState != State.PLAYING || player.getHp() <= 0) return;
        player.shootFireball();
        waveManager.checkWaveEnd();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameState == State.START) {
            g.setColor(Color.DARK_GRAY); g.fillRect(0, 0, 1200, 900);
            g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 80)); g.drawString("SLIME HUNTER", 280, 300);
            g.setColor(Color.GREEN); g.fillRect(btnStart.x, btnStart.y, btnStart.width, btnStart.height);
            g.setColor(Color.BLACK); g.setFont(new Font("Arial", Font.BOLD, 30)); g.drawString("START", btnStart.x + 50, btnStart.y + 40);
            return;
        }

        if (bgImg != null && bgImg.getWidth(null) > 0) g.drawImage(bgImg, 0, 0, 1200, 900, this);
        else { g.setColor(Color.LIGHT_GRAY); g.fillRect(0, 0, 1200, 900); }

        g.setColor(Color.BLACK); g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Wave: " + waveManager.getCurrentWave(), 20, 30);
        g.drawString("HP: " + player.getHp() + "/" + player.getMaxHp(), 20, 60);
        g.drawString("Level: " + player.getLevel() + " (EXP: " + player.getExp() + ")", 20, 90);

        int currentMinDmg = 30 + ((player.getLevel() - 1) * 10);
        int currentMaxDmg = 40 + ((player.getLevel() - 1) * 10);
        g.drawString("Magic Power: " + currentMinDmg + " - " + currentMaxDmg, 20, 120);

        g.setColor(Color.BLUE); g.drawString("Score: " + player.getScore(), 20, 150);

        if (waveManager.isCountingDown() && gameState == State.PLAYING) {
            g.setColor(Color.RED); g.setFont(new Font("Arial", Font.BOLD, 30)); g.drawString("Next wave in: " + waveManager.getCountdown() + "s", 500, 50);
        }

        for (Fireball fb : player.getFireballs()) {
            if (fireballImg.getWidth(null) > 0) {
                Graphics2D g2d = (Graphics2D) g.create();
                int centerX = fb.getX() + (fb.getSize() / 2);
                int centerY = fb.getY() + (fb.getSize() / 2);
                double angle = 0;
                if (fb.getDirection() == 0) angle = Math.toRadians(-90);
                else if (fb.getDirection() == 1) angle = Math.toRadians(0);
                else if (fb.getDirection() == 2) angle = Math.toRadians(90);
                else if (fb.getDirection() == 3) angle = Math.toRadians(180);
                g2d.rotate(angle, centerX, centerY);
                g2d.drawImage(fireballImg, fb.getX(), fb.getY(), fb.getSize(), fb.getSize(), this);
                g2d.dispose();
            } else { g.setColor(Color.ORANGE); g.fillOval(fb.getX(), fb.getY(), fb.getSize(), fb.getSize()); }
        }

        for (HealthPotion potion : waveManager.getPotions()) {
            if (potionImg.getWidth(null) > 0) g.drawImage(potionImg, potion.getX(), potion.getY(), 30, 30, this);
            else { g.setColor(Color.PINK); g.fillOval(potion.getX(), potion.getY(), 30, 30); }
        }

        if (player.getHp() > 0) {
            int dir = player.getDirection();
            Image currentImg = playerImgs[dir];
            if (currentImg != null && currentImg.getWidth(null) > 0) g.drawImage(currentImg, player.getX(), player.getY(), 70, 70, this);
            else { g.setColor(Color.BLUE); g.fillRect(player.getX(), player.getY(), 70, 70); }

            if (player.isInvincible()) { g.setColor(new Color(255, 165, 0, 128)); g.fillRect(player.getX(), player.getY(), 70, 70); }

            if (player.getLevelUpTimer() > 0) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString("LEVEL UP!", player.getX() - 15, player.getLevelUpY());
            }
        }

        List<Slime> slimesList = waveManager.getSlimes();
        for(int i = slimesList.size() - 1; i >= 0; i--) {
            Slime slime = slimesList.get(i);
            if (slimeImg.getWidth(null) > 0) g.drawImage(slimeImg, slime.getX(), slime.getY(), 60, 60, this);
            else { g.setColor(Color.GREEN); g.fillRect(slime.getX(), slime.getY(), 60, 60); }
        }

        g.setFont(new Font("Arial", Font.BOLD, 18));
        for (DamageText dt : waveManager.getDamageTexts()) {
            g.setColor(Color.RED); g.drawString(String.valueOf(dt.getDamage()), dt.getX(), dt.getY());
        }

        if (gameState == State.GAMEOVER) {
            g.setColor(new Color(0, 0, 0, 180)); g.fillRect(0, 0, 1200, 900);
            g.setColor(Color.RED); g.setFont(new Font("Arial", Font.BOLD, 80)); g.drawString("GAME OVER", 350, 250);
            g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 50)); g.drawString("Final Score: " + player.getScore(), 420, 380);

            g.setColor(Color.YELLOW); g.fillRect(btnRestart.x, btnRestart.y, btnRestart.width, btnRestart.height);
            g.setColor(Color.BLACK); g.setFont(new Font("Arial", Font.BOLD, 30)); g.drawString("RESTART", btnRestart.x + 30, btnRestart.y + 40);
            g.setColor(Color.RED); g.fillRect(btnExit.x, btnExit.y, btnExit.width, btnExit.height);
            g.setColor(Color.WHITE); g.drawString("EXIT", btnExit.x + 65, btnExit.y + 40);
        }
    }

    @Override public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        if (gameState == State.START) { if (btnStart.contains(p)) gameState = State.PLAYING;
        } else if (gameState == State.PLAYING) { playerAttack();
        } else if (gameState == State.GAMEOVER) {
            if (btnRestart.contains(p)) restartGame(); else if (btnExit.contains(p)) System.exit(0);
        }
    }

    @Override public void keyPressed(KeyEvent e) {
        if (gameState != State.PLAYING) return;
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) up = true;
        if (key == KeyEvent.VK_A) left = true;
        if (key == KeyEvent.VK_S) down = true;
        if (key == KeyEvent.VK_D) right = true;
    }

    @Override public void keyReleased(KeyEvent e) {
        if (gameState != State.PLAYING) return;
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) playerAttack();
        if (key == KeyEvent.VK_W) up = false;
        if (key == KeyEvent.VK_A) left = false;
        if (key == KeyEvent.VK_S) down = false;
        if (key == KeyEvent.VK_D) right = false;
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}