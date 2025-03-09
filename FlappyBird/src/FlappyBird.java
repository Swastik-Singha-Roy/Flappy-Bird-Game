import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList; //for the pipes
import java.util.Random; //for placing the pipes at random
import javax.swing.*;
public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //Image variables to store our image objects
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird variables
    int birdX = boardWidth/8; //X position of the bird
    int birdY = boardHeight/2; //Y position of the bird
    int birdWidth = 34; //dimensions of the bird
    int birdHeight = 24; 

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) { //constructor of class Bird
            this.img = img;
        }
    }

    //Pipe
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; //scaled down by 6 times
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false; //will keep track whether our bird has passed the pipe yet (score tracking)
        
        Pipe(Image img) {
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4; //moving pipes to the left (looks like the bird moves right)
    int velocityY = 0; //velocity for the bird to move up or down
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);
        setFocusable(true); //makes sure that JPanel takes our key press event
        addKeyListener(this); //makes sure that keypressed functions are added

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipesTimer  = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //game timer
        gameLoop = new Timer(1000/60, this); // 1000/60 = 16.6 (60fps). this refers to the FlappyBird class
        gameLoop.start(); //starts the action loop


    } 

    public void placePipes() {
        //(0-1) * pipeHeight -> (0-256) (512/2 = 256)
        // 0 - 128 - (0 - 256)

        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4; //space for the bird to pass by

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); //super keyword to invoke paintComponent() from the parent class Jpanel
        draw(g);
    }

    public void draw(Graphics g) {
        //debug statement
        //System.out.println("Draw");
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null); //displaying an image, (0,0) is the origin in the top left corner, (360,640) is the end at the bottom right corner.

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null); //displaying the bird
        
        //pipes
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int)score), 10, 35);
        }
    }

    public void move() {
        //bird movement
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); //if bird moves up beyond the screen i.e beyond the Y = 0 then bird.y = 0
        
        //pipes
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            } 

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) { //game over case
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) { //game over collision logic
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) { //action to be performed every 60 times a second
        move();
        repaint();
        if(gameOver) { // if game over then stopping the Loop
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) { //keyPressed means any key press (can be f5 or G, <space>, any key)
        if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_F || e.getKeyCode() == KeyEvent.VK_J) {
            velocityY = -10;
            if(gameOver) {
                //restart the game by resetting the conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }
    
    /*@Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
    }
    */
    
}
