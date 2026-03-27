import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Slime Hunter");

        Player player = new Player();
        player.setX(575);
        player.setY(425);

        WaveManager wm = new WaveManager(player);
        GamePanel panel = new GamePanel(wm);

        frame.add(panel);
        frame.setSize(1200, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
