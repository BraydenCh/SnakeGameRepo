import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 100; //how big are items
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 100;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten = 0;
    int applesX;
    int applesY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    JButton restartButton = new JButton();

    GamePanel() {

        this.random = new Random();
        //set size of screen
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        //background color
        this.setBackground(Color.black);

        this.setFocusable(true);
        //creates key listener
        this.addKeyListener(new MyKeyAdapter());
        //starts game
        this.mainMenu();

    }

    public void mainMenu() {

        this.addRestartButton();
        this.restartButton.setVisible(false);
        this.bodyParts = 6;
        this.applesEaten = 0;
        this.direction = 'R';
        for (int i = 0; i < this.x.length; i++) {
            this.x[i] = -i;
            this.y[i] = -i;
        }
        this.startGame();

    }

    public void startGame() {
        //places apple

        this.placeApple();
        //starts running the game
        this.running = true;
        this.repaint();

        //starts a timer
        this.timer = new Timer(DELAY, this);
        this.timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.draw(g);
    }

    public void draw(Graphics g) {
        if (this.running) {
            //draws the grid
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                //vertical lines
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                //horizontal lines
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            //creates the apple
            g.setColor(Color.red);
            g.fillOval(this.applesX, this.applesY, UNIT_SIZE, UNIT_SIZE);
            for (int i = 0; i < this.bodyParts; i++) {
                if (i % 2 == 0) {
                    g.setColor(Color.green);
                    g.fillRect(this.x[i], this.y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(this.x[i], this.y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.red);
            g.setFont(new Font("Calibri", Font.BOLD, 20));

            //Places Game Over at Middle x and middle y
            g.drawString("Score: " + this.applesEaten, 15, SCREEN_HEIGHT - 15);

        } else if (this.bodyParts >= Math.pow(SCREEN_HEIGHT / UNIT_SIZE, 2)) {
            this.gameWin(g);
        } else {
            this.gameOver(g);
        }
    }

    public void placeApple() {
        //creates random coordinate for the apple

        this.applesX = this.random.nextInt(SCREEN_WIDTH / UNIT_SIZE)
                * UNIT_SIZE;

        this.applesY = this.random.nextInt(SCREEN_HEIGHT / UNIT_SIZE)
                * UNIT_SIZE;

    }

    public void move() {
        for (int i = this.bodyParts; i > 0; i--) {
            //moves x and y values by 1
            this.x[i] = this.x[i - 1];
            this.y[i] = this.y[i - 1];
        }
        switch (this.direction) {
            case 'R':
                this.x[0] = this.x[0] + UNIT_SIZE;
                break;
            case 'L':
                this.x[0] = this.x[0] - UNIT_SIZE;
                break;
            case 'U':
                this.y[0] = this.y[0] + UNIT_SIZE;
                break;
            case 'D':
                this.y[0] = this.y[0] - UNIT_SIZE;
                break;

        }

    }

    public void checkFood() {
        //if head is apples coords
        if (this.x[0] == this.applesX && this.y[0] == this.applesY) {
            //increase body size
            this.bodyParts++;
            this.applesEaten++;
            this.placeApple();
        }

    }

    public void checkCollisions() {
        if (this.bodyParts >= Math.pow(SCREEN_HEIGHT / UNIT_SIZE, 2)) {
            this.running = false;
        }
        //checks if head ran into with any body part
        for (int i = this.bodyParts; i > 0; i--) {
            if (this.x[0] == this.x[i] && this.y[0] == this.y[i]) {
                this.running = false;
            }
        }
        //check if head runs into left wall
        if (this.x[0] < 0) {
            this.running = false;
        }
        //check if head runs into right wall
        if (this.x[0] > SCREEN_WIDTH) {
            this.running = false;
        }
        //check if head runs into top wall
        if (this.y[0] < 0) {
            this.running = false;
        }
        //check if head runs into bottom wall
        if (this.y[0] > SCREEN_HEIGHT) {
            this.running = false;
        }

        if (!this.running) {
            this.timer.stop();
        }

    }

    public void gameOver(Graphics g) {
        //Game over text
        g.setColor(Color.red);
        g.setFont(new Font("Calibri", Font.BOLD, 40));
        FontMetrics metrics = this.getFontMetrics(g.getFont());
        //Places Game Over at Middle x and middle y
        g.drawString("Game Over",
                (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2,
                SCREEN_HEIGHT / 2 - 40);
        g.drawString("Score: " + this.applesEaten,
                (SCREEN_WIDTH
                        - metrics.stringWidth("Score: " + this.applesEaten))
                        / 2,
                (SCREEN_HEIGHT / 2));

        this.restartButton.setVisible(true);

    }

    public void gameWin(Graphics g) {
        //Game over text
        g.setColor(Color.red);
        g.setFont(new Font("Calibri", Font.BOLD, 40));
        FontMetrics metrics = this.getFontMetrics(g.getFont());
        //Places Game Over at Middle x and middle y
        g.drawString("You Win",
                (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2,
                SCREEN_HEIGHT / 2 - 40);
        g.drawString("Score: " + this.applesEaten,
                (SCREEN_WIDTH
                        - metrics.stringWidth("Score: " + this.applesEaten))
                        / 2,
                (SCREEN_HEIGHT / 2));
        this.restartButton.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.running) {
            this.move();
            this.checkFood();
            this.checkCollisions();
        }
        this.repaint();

    }

    private void addRestartButton() {
        String buttonText = "New Game?";
        this.restartButton = new JButton(buttonText);
        Font font = new Font("Helvetica", Font.BOLD, 40);
        this.restartButton.setFont(font);

        this.setLayout(null);
        this.restartButton.setBounds(SCREEN_WIDTH / 2 - 110,
                SCREEN_HEIGHT / 2 + 10, SCREEN_WIDTH / 2 - 70,
                SCREEN_HEIGHT / 2 + 30);
        this.add(this.restartButton);

        this.restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GamePanel.this.restartButton.setVisible(false);
                GamePanel.this.mainMenu();

            }
        });

    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (GamePanel.this.direction != 'R') {
                        GamePanel.this.direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (GamePanel.this.direction != 'L') {
                        GamePanel.this.direction = 'R';
                    }
                    break;
                //backwards down-up for some reason
                case KeyEvent.VK_DOWN:
                    if (GamePanel.this.direction != 'D') {
                        GamePanel.this.direction = 'U';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (GamePanel.this.direction != 'U') {
                        GamePanel.this.direction = 'D';
                    }
                    break;

            }
        }
    }

}
