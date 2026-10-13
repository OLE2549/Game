import java.awt.*;

import java.util.Random;



public class Asteroid {

    public float x, y;

    public float speed;

    public int size;

    public boolean active = true;



    private Random rand = new Random();



    public Asteroid(int panelWidth) {

        this.size = 30 + rand.nextInt(20);

        this.x = rand.nextInt(panelWidth - size);

        this.y = -size;

        this.speed = 2.0f + rand.nextFloat() * 3.0f;

    }



    public void update(int panelHeight) {

        y += speed;



        if(y> panelHeight) {

            active = false;

        }

    }



    public void draw(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);



        Color iceHighlight = new Color(200, 240, 255);

        Color iceShadow = new Color(20, 80, 140);

        GradientPaint gp = new GradientPaint(x, y, iceHighlight, x + size, y + size, iceShadow);

        g2.setPaint(gp);

        g2.fillOval((int)x, (int)y, size, size);



        g2.setColor(new Color(255, 255, 255, 180));

        g2.fillOval((int)(x + size * 0.15), (int)(y + size * 0.15), (int)(size * 0.3), (int)(size * 0.2));



        g2.setColor(new Color(150, 220, 255, 200));

        g2.setStroke(new BasicStroke(1.5f));

        g2.drawLine((int)(x + size * 0.3), (int)(y + size * 0.2), (int)(x + size * 0.7), (int)(y + size * 0.8));

        g2.drawLine((int)(x + size * 0.2), (int)(y + size * 0.6), (int)(x + size * 0.8), (int)(y + size * 0.3));



        g2.setColor(new Color(10, 50, 100));

        g2.setStroke(new BasicStroke(2));

        g2.drawOval((int)x, (int)y, size, size);

    }



    public Rectangle gethit() {

        return new Rectangle((int)x, (int)y, size, size);

    }

}