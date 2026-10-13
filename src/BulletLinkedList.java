import java.awt.*;

class BulletNode {
    public Bullet B;
    public BulletNode next;
    BulletNode(Bullet b) {
        this.B = b;
        next = null;
    }
}

class Bullet {
    public float x, y;
    public int width = 6;
    public int height = 16;
    public boolean active = true;

    public Bullet(float x, float y){
        this.x = x - (width / 2);
        this.y = y;
    }

    public void shot(){
        y -= 10;
        if(y < -height){
            active = false;
        }
    }

    public void draw(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(150, 220, 255));
        g2.fillRect((int)x, (int)y, width, height);

        g2.setColor(Color.WHITE);
        g2.drawRect((int)x, (int)y, width, height);
    }

    public Rectangle gethit() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}

public class BulletLinkedList {
    public BulletNode head;
    public BulletNode tail;
    public int size;

    public BulletLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void push(Bullet b) {
        BulletNode node = new BulletNode(b);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public void update(){
        BulletNode cur = head;
        BulletNode prev = null;
        while(cur != null){
            cur.B.shot();
            if(!cur.B.active){
                if(prev == null){
                    head = cur.next;
                }else{
                    prev.next = cur.next;
                }
                if(cur == tail){
                    tail = prev;
                }
                size--;
            }else{
                prev = cur;
            }
            cur = cur.next;
        }
    }

    public void draw(Graphics g2){
        BulletNode cur = head;
        while(cur != null){
            cur.B.draw(g2);
            cur = cur.next;
        }
    }
}