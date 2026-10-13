import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class player {
    public int x, y;
    public int width = 60;
    public int height = 80;
    public int speed = 5;
    public int health = 100;
    public int ammo = 100;
    public int invincibleTimer = 0;

    public boolean left, right, up, down;

    private BufferedImage shipImage;
    private Random rand = new Random();

    private boolean showThrust = true;
    private int thrustTimer = 0;

    public player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        try {
            shipImage = ImageIO.read(new File("images/ship.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ไม่สามารถโหลดรูปภาพยานได้!");
        }
    }

    public void update(int panelWidth, int panelHeight) {
        if (left)  x -= speed;
        if (right) x += speed;
        if (up)    y -= speed;
        if (down)  y += speed;

        if (x < 0) x = 0;
        if (x > panelWidth - width) x = panelWidth - width;
        if (y < 0) y = 0;
        if (y > panelHeight - height) y = panelHeight - height;

        if (invincibleTimer > 0) {
            invincibleTimer--;
        }

        thrustTimer++;
        if (thrustTimer > 5){
            showThrust = !showThrust;
            thrustTimer = 0;
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if ((up || down || left || right) && showThrust) {
            drawThruster(g2);
        }

        if (invincibleTimer > 0 && (invincibleTimer / 5) % 2 == 0) {
        } else {
            if (shipImage != null) {
                g2.drawImage(shipImage, x, y, width, height, null);
            } else {
                g2.setColor(Color.RED);
                g2.fillRect(x, y, width, height);
            }
        }
    }

    private void drawThruster(Graphics2D g2) {
        int thrustX = x + width / 2;
        int thrustY = y + height - 5;
        int thrustLength = 20 + rand.nextInt(15);
        int thrustWidth = 10;

        int[] xInner = {thrustX - thrustWidth/2 + 2, thrustX, thrustX + thrustWidth/2 - 2};
        int[] yInner = {thrustY, thrustY + thrustLength - 5, thrustY};
        g2.setColor(new Color(150, 200, 255, 200));
        g2.fillPolygon(xInner, yInner, 3);

        int[] xOuter = {thrustX - thrustWidth, thrustX, thrustX + thrustWidth};
        int[] yOuter = {thrustY, thrustY + thrustLength, thrustY};
        g2.setColor(new Color(50, 100, 255, 150));
        g2.fillPolygon(xOuter, yOuter, 3);
    }

    public Rectangle gethit() {
        int hitboxShrink = 10;
        return new Rectangle(x + hitboxShrink, y + hitboxShrink, width - (hitboxShrink*2), height - (hitboxShrink*2));
    }
}