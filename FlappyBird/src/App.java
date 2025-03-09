import javax.swing.*;
public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;
        //dimensions
        JFrame frame = new JFrame("Flappy Bird"); //window title
        //frame.setVisible(true); //visibility of the window
        frame.setSize(boardWidth, boardHeight); //setting the dimensions
        frame.setLocationRelativeTo(null); //initial location of the window at the centre of the screen
        frame.setResizable(false); //not resizable window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //closing the window
      
        FlappyBird flappyBird = new FlappyBird(); //instance of the class
        frame.add(flappyBird);
        frame.pack(); //this means that the true dimension of the app is our set dimensions, (doesnt take the dimensions of the title)
        flappyBird.requestFocus(); // focuses on a component of the GUI
        frame.setVisible(true); //best practice set visibility after setting up

    }
}
