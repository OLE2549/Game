import java.awt.*;

public class Item {
    public float x, y;
    public int width = 32;
    public int height = 32;
    public float speed = 2.5f;
    public boolean active = true;
    public String type;

    public Item(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.type = Math.random() < 0.5 ? "HEAL" : "AMMO";
    }

    public void update(int panelHeight) {
        y += speed;
        if (y > panelHeight) {
            active = false;
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ix = (int) x;
        int iy = (int) y;

        if (type.equals("HEAL")) {
            g2.setColor(new Color(230, 40, 40));
            g2.fillRoundRect(ix, iy, width, height, 8, 8);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(ix, iy, width, height, 8, 8);

            int cx = ix + width / 2;
            int cy = iy + height / 2;
            g2.fillRect(cx - 3, cy - 8, 6, 16);
            g2.fillRect(cx - 8, cy - 3, 16, 6);

        } else if (type.equals("AMMO")) {
            g2.setColor(new Color(220, 130, 30));
            g2.fillRoundRect(ix, iy, width, height, 8, 8);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(ix, iy, width, height, 8, 8);

            int cx = ix + width / 2;
            int cy = iy + height / 2;

            g2.setColor(new Color(245, 215, 60));
            g2.fillRect(cx - 3, cy - 1, 6, 9);

            int[] bx = {cx - 3, cx, cx + 3};
            int[] by = {cy - 1, cy - 8, cy - 1};
            g2.setColor(new Color(220, 100, 30));
            g2.fillPolygon(bx, by, 3);

            g2.setColor(new Color(180, 130, 20));
            g2.fillRect(cx - 4, cy + 8, 8, 2);
        }
    }

    public Rectangle gethit() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}