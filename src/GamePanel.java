import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
    private enum GameState {
        MENU,
        HOW_TO_PLAY,
        PLAYING,
        GAMEOVER
    }
    private GameState gameState = GameState.MENU;

    private player player;
    private ArrayList<Asteroid> asteroids;
    private BulletLinkedList bullets;
    private ArrayList<Item> items;
    private Timer timer;

    private ArrayList<Point> starsNear;
    private ArrayList<Point> starsFar;
    private Random rand = new Random();

    private double distance = 0;
    private int currentLevel = 1;
    private int maxAsteroids = 4;
    private int speedMultiplier = 1;

    // --- กำหนดขอบเขตของปุ่มต่างๆ (Rectangle) สำหรับให้เมาส์คลิก ---
    private Rectangle startButton = new Rectangle(300, 230, 200, 50);
    private Rectangle howToButton = new Rectangle(300, 300, 200, 50);
    private Rectangle backButton = new Rectangle(350, 460, 100, 40);
    private Rectangle restartButton = new Rectangle(300, 320, 200, 45);
    private Rectangle menuButton = new Rectangle(300, 380, 200, 45);

    // ตัวแปรเก็บตำแหน่งเมาส์ปัจจุบัน เพื่อทำเอฟเฟกต์ Hover
    private int mouseX = 0;
    private int mouseY = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(10, 10, 25));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        initSpaceMap();

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    private void resetGame() {
        player = new player(370, 480);
        asteroids = new ArrayList<>();
        bullets = new BulletLinkedList();
        items = new ArrayList<>();
        distance = 0;
        currentLevel = 1;
        maxAsteroids = 4;
        speedMultiplier = 1;
        setBackground(new Color(10, 10, 25));

        for (int i = 0; i < maxAsteroids; i++) {
            asteroids.add(new Asteroid(800));
        }
    }

    private void initSpaceMap() {
        starsNear = new ArrayList<>();
        starsFar = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            starsFar.add(new Point(rand.nextInt(800), rand.nextInt(600)));
        }
        for (int i = 0; i < 25; i++) {
            starsNear.add(new Point(rand.nextInt(800), rand.nextInt(600)));
        }
    }

    private void updateSpaceMap() {
        for (Point p : starsFar) {
            p.y += (1 * speedMultiplier);
            if (p.y > 600) {
                p.y = 0;
                p.x = rand.nextInt(800);
            }
        }
        for (Point p : starsNear) {
            p.y += (3 * speedMultiplier);
            if (p.y > 600) {
                p.y = 0;
                p.x = rand.nextInt(800);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawSpaceMap(g);

        if (gameState == GameState.MENU) {
            drawMenu(g);
        } else if (gameState == GameState.HOW_TO_PLAY) {
            drawHowToPlay(g);
        } else if (gameState == GameState.PLAYING) {
            drawGamePlay(g);
        } else if (gameState == GameState.GAMEOVER) {
            drawGamePlay(g);
            drawGameOver(g);
        }
    }

    private void drawSpaceMap(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(150, 170, 220, 180));
        for (Point p : starsFar) {
            g2.fillRect(p.x, p.y, 2, 2);
        }
        g2.setColor(new Color(255, 255, 255, 230));
        for (Point p : starsNear) {
            g2.fillRect(p.x, p.y, 3, 3);
        }
    }

    // --- 1. หน้าจอหลัก (Main Menu) พร้อมปุ่มกด ---
    private void drawMenu(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(150, 220, 255));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 52));
        g2.drawString("SPACE SHOOTER", 210, 160);

        // ปุ่ม Start Game
        drawButton(g2, startButton, "START GAME", new Color(46, 204, 113), new Color(39, 174, 96));

        // ปุ่ม How to Play
        drawButton(g2, howToButton, "HOW TO PLAY", new Color(52, 152, 219), new Color(41, 128, 185));
    }

    // --- 2. หน้าจอวิธีเล่น (How to Play) พร้อมปุ่ม Back ---
    private void drawHowToPlay(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g2.drawString("HOW TO PLAY", 275, 100);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g2.drawString("- Move: W, A, S, D  or  Arrow Keys", 230, 180);
        g2.drawString("- Shoot: Spacebar (Uses Ammo)", 230, 230);
        g2.drawString("- Avoid or Shoot Asteroids to survive", 230, 280);
        g2.drawString("- Collect Red Crate: Heal +50 HP", 230, 330);
        g2.drawString("- Collect Orange Crate: Ammo +10", 230, 380);

        // ปุ่ม Back
        drawButton(g2, backButton, "BACK", new Color(231, 76, 60), new Color(192, 57, 43));
    }

    // --- 3. หน้าจอระหว่างเล่นเกม ---
    private void drawGamePlay(Graphics g) {
        for (Asteroid a : asteroids) {
            a.draw(g);
        }
        for (Item item : items) {
            item.draw(g);
        }
        bullets.draw(g);
        player.draw(g);
        drawHUD(g);
    }

    private void drawHUD(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
        if (currentLevel == 3) g2.setColor(new Color(255, 100, 100));
        else if (currentLevel == 2) g2.setColor(new Color(200, 150, 255));
        else g2.setColor(Color.WHITE);
        g2.drawString("LEVEL " + currentLevel, 350, 35);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        g2.drawString("Distance: " + (int) distance + " m", 20, 32);

        g2.drawString("HP: ", 20, 65);
        g2.setColor(new Color(50, 50, 60));
        g2.fillRoundRect(60, 48, 160, 22, 6, 6);

        int hpWidth = (int) (160 * (player.health / 100.0));
        if (player.health > 30) g2.setColor(new Color(46, 204, 113));
        else g2.setColor(new Color(231, 76, 60));
        if (hpWidth > 0) g2.fillRoundRect(60, 48, hpWidth, 22, 6, 6);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(60, 48, 160, 22, 6, 6);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.drawString(player.health + " / 100", 110, 64);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(new Color(241, 196, 15));
        g2.drawString("Ammo: " + player.ammo, 20, 102);
    }

    // --- 4. หน้าจอ Game Over พร้อมปุ่มกดด้วยเมาส์ ---
    private void drawGameOver(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, 800, 600);

        g2.setColor(new Color(231, 76, 60));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 48));
        g2.drawString("GAME OVER", 260, 210);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        g2.drawString("Reached Level " + currentLevel + " (Distance: " + (int) distance + " m)", 245, 270);

        // ปุ่มเล่นใหม่ / กลับเมนู
        drawButton(g2, restartButton, "PLAY AGAIN", new Color(46, 204, 113), new Color(39, 174, 96));
        drawButton(g2, menuButton, "MAIN MENU", new Color(241, 196, 15), new Color(212, 172, 13));
    }

    // --- ฟังก์ชันช่วยวาดปุ่มพร้อมเอฟเฟกต์ Hover เมื่อเมาส์ชี้ ---
    private void drawButton(Graphics2D g2, Rectangle rect, String text, Color normalColor, Color hoverColor) {
        if (rect.contains(mouseX, mouseY)) {
            g2.setColor(hoverColor); // สีตอนเอาเมาส์ชี้
        } else {
            g2.setColor(normalColor); // สีปกติ
        }
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + (rect.height + fm.getAscent()) / 2 - 4;
        g2.drawString(text, textX, textY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateSpaceMap();

        if (gameState == GameState.PLAYING) {
            checkLevel();
            distance += 0.25;

            player.update(800, 600);
            bullets.update();

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                item.update(600);
                if (!item.active) {
                    items.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < asteroids.size(); i++) {
                Asteroid a = asteroids.get(i);
                a.update(600);
                if (!a.active) {
                    asteroids.remove(i);
                    asteroids.add(new Asteroid(800));
                    i--;
                }
            }

            while (asteroids.size() < maxAsteroids) {
                asteroids.add(new Asteroid(800));
            }

            checkCollisions();

            if (player.health <= 0) {
                gameState = GameState.GAMEOVER;
            }
        }

        repaint();
    }

    private void checkLevel() {
        if (distance >= 600) {
            currentLevel = 3;
            maxAsteroids = 8;
            speedMultiplier = 3;
            setBackground(new Color(35, 10, 20));
        } else if (distance >= 300) {
            currentLevel = 2;
            maxAsteroids = 6;
            speedMultiplier = 2;
            setBackground(new Color(20, 10, 35));
        } else {
            currentLevel = 1;
            maxAsteroids = 4;
            speedMultiplier = 1;
            setBackground(new Color(10, 10, 25));
        }
    }

    private void checkCollisions() {
        BulletNode cur = bullets.head;
        while (cur != null) {
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid a = asteroids.get(j);

                if (cur.B.gethit().intersects(a.gethit())) {
                    cur.B.active = false;

                    if (Math.random() < 0.4) {
                        items.add(new Item(a.x, a.y));
                    }
                    asteroids.remove(j);
                    break;
                }
            }
            cur = cur.next;
        }

        for (int j = 0; j < asteroids.size(); j++) {
            Asteroid a = asteroids.get(j);
            if (player.gethit().intersects(a.gethit())) {
                if (player.invincibleTimer == 0) {
                    player.health -= 15;
                    if (player.health < 0) player.health = 0;
                    player.invincibleTimer = 60;

                    asteroids.remove(j);
                    break;
                }
            }
        }

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (player.gethit().intersects(item.gethit())) {
                if (item.type.equals("HEAL")) {
                    player.health = Math.min(player.health + 50, 100);
                } else if (item.type.equals("AMMO")) {
                    player.ammo += 10;
                }
                items.remove(i);
                i--;
            }
        }
    }

    // --- จัดการเหตุการณ์คลิกเมาส์ (Mouse Click) ---
    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (gameState == GameState.MENU) {
            if (startButton.contains(mx, my)) {
                resetGame();
                gameState = GameState.PLAYING;
            } else if (howToButton.contains(mx, my)) {
                gameState = GameState.HOW_TO_PLAY;
            }
        } else if (gameState == GameState.HOW_TO_PLAY) {
            if (backButton.contains(mx, my)) {
                gameState = GameState.MENU;
            }
        } else if (gameState == GameState.GAMEOVER) {
            if (restartButton.contains(mx, my)) {
                resetGame();
                gameState = GameState.PLAYING;
            } else if (menuButton.contains(mx, my)) {
                gameState = GameState.MENU;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}

    // --- ควบคุมด้วยคีย์บอร์ด (ยังคงใช้ยิงและบังคับยานได้ปกติ) ---
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameState != GameState.PLAYING || player.health <= 0) return;
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) player.up = true;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) player.down = true;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) player.left = true;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) player.right = true;

        if (key == KeyEvent.VK_SPACE) {
            if (player.ammo > 0) {
                bullets.push(new Bullet(player.x + (player.width / 2), player.y));
                player.ammo--;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameState == GameState.PLAYING && player != null) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) player.up = false;
            if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) player.down = false;
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) player.left = false;
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) player.right = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}