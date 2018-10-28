package view;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import view.fields.DurationField;


import java.util.*;

/**
 * @author Orgil Batzaya
 */

public class TurtleDisplay extends StackPane {
    private static final int FRAMES_PER_SECOND = 60;
    private static final double GRAPHICS_CONTENT_WIDTH = 10;
    private static final Color PEN_COLOR = Color.RED;
    private static final double MOUSE_SIZE = 10;
    private static final int NUM_STARTING_TURTLES = 3;
    private static final String TURTLE_IMAGE_PATH = "/resources/TurtleImage";
    private static final String COLOR_PATH = "/resources/Color";

    //parameters for display actions
    private static final Color[] COLORS = {Color.GRAY,Color.PURPLE, Color.AZURE,Color.BEIGE,Color.BLUE,Color.VIOLET, Color.GREEN,Color.PALEGOLDENROD};

    private Canvas myCanvas;
    private GraphicsContext myGC;
    private SLogoPen myPen;
    private Map<Integer,TurtleView> myTurtles;
    private TurtleView myTurtle;
    private TurtleView myCurrentTurtle;
    private Point2D myPos;
    private SequentialTransition myCurrentAnimation;
    private Rectangle myBackground;
    private Point2D zeroPos;
    private DurationField myDuration;
    private double returnValue;
    private VBox myBox; //May or may not use
    private Pane displayPane;
    private Queue<String> myAnimations;
    private ResourceBundle myImages;
    private ResourceBundle myColors;

    //private StatusView statusView;

    public TurtleDisplay(double width, double height) {
        myDuration = new DurationField("Duration of Animation: ");
        myBackground = new Rectangle(width, height);
        myBackground.setFill(Color.WHITE);
        myCanvas = new Canvas(width, height);
        zeroPos = new Point2D(myCanvas.getWidth() / 2, myCanvas.getHeight() / 2);
        myPen = new SLogoPen(PEN_COLOR);
        myGC = myCanvas.getGraphicsContext2D();
        myGC.setLineWidth(GRAPHICS_CONTENT_WIDTH);
        myCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, handler);
        myTurtles = new HashMap<>();
        myTurtle = new TurtleView();
        this.getChildren().add(myBackground);
        this.getChildren().add(myCanvas);
        //displayPane = new Pane(myTurtle.getView());
        myTurtle.getView().setX(zeroPos.getX());
        myTurtle.getView().setY(zeroPos.getY());
        myCurrentAnimation = new SequentialTransition();
        this.getChildren().add(myTurtle.getView());
        //makeTurtles(displayPane);
        //this.getChildren().add(displayPane);
        myImages = ResourceBundle.getBundle(TURTLE_IMAGE_PATH);
        myColors = ResourceBundle.getBundle(COLOR_PATH);
    }

    public Canvas getCanvas() {
        return myCanvas;
    }

    public VBox getDurationDisplay() {
        return myDuration.getDisplay();
    }


    EventHandler<MouseEvent> handler = new EventHandler<>() {
        public void handle(MouseEvent e) {
            double size = MOUSE_SIZE;
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;
            myGC.setFill(myPen.getPenColor());
            myGC.setEffect(new DropShadow());
            myGC.fillOval(x, y, size, size);
        }
    };

    private void makeTurtles(Pane displayPane){
        for(int i = 0; i < NUM_STARTING_TURTLES; i++){
            TurtleView t = new TurtleView();
            t.getView().setX(zeroPos.getX() + i*30);
            t.getView().setY(zeroPos.getY());
            displayPane.getChildren().add(t.getView());
            myTurtles.put(i,t);
        }
    }

    public Map<Integer, TurtleView> getTurtles(){
        return myTurtles;
    }

    private void addTurtle(){

    }


    public GraphicsContext getGraphicsContext() {
        return myGC;
    }

    public void setBgColor(Color c) {
        myBackground.setFill(c);
    }

    public void setBgColor(Integer index) {
        Color c = Color.valueOf(myColors.getString(index.toString()));
        setBgColor(c);
    }


    public Animation getCurrentAnimation() {
        return myCurrentAnimation;
    }

    public TurtleView getMyTurtle() {
        return myTurtle;
    }

    public void clearScreen() {
        myGC.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        setToNewPosition( 0, 0);
        myTurtle.getView().setRotate(0);
    }

    public void setToNewPosition(double x, double y) {
        returnValue = myTurtle.setNewCoordinates(x, y);
        myTurtle.getView().setX(zeroPos.getX() + x);
        myTurtle.getView().setY(zeroPos.getY() + y);
        if(x == 0 && y == 0) {
            myTurtle.getView().setRotate(0);
        }
    }

    public void createNewAnimation(Point2D next) {
        Animate animation = new Animate(myCanvas, myGC, myPen, Duration.seconds(myDuration.getDuration()), myTurtle);
        //System.out.println(next.getX());
        //System.out.println(next.getY());
        myCurrentAnimation.getChildren().add(animation.move(next));
        System.out.println(myCurrentAnimation.getChildren().size());

        myCurrentAnimation.playFromStart();
        myCurrentAnimation.setOnFinished(e -> resetAnimation());
        returnValue = myTurtle.setNewCoordinates(next.getX(), next.getY());
        myTurtle.getView().setX(zeroPos.getX() + next.getX());
        myTurtle.getView().setY(zeroPos.getY() + next.getY());
    }

    public void updatePen(double bool) {
        if((!myPen.isVisible() && bool == 1) || (myPen.isVisible() && bool == 0)) {
            myPen.changePenVisibilty();
        }
        returnValue = bool;
        //System.out.println(returnValue);
    }

    public SLogoPen getPen() {
        return myPen;
    }

    public Pane getDisplayPane() {
        return displayPane;
    }

    //Delete this method most likely
    public void playAnimations() {
        if(myAnimations.size() > 1) {
            String animation = myAnimations.peek();
            myCurrentAnimation.play();
        }
        myCurrentAnimation.setOnFinished(e -> playAnimations());
    }

    public void resetAnimation() {
        myCurrentAnimation.getChildren().clear();
    }

    public void changeTurtleImage(int i) {
        for(TurtleView turtle : myTurtles.values()) {
            turtle.setView(myImages.getString(Integer.toString(i)));
        }
    }

    public void changePenColor(Integer index) {
        Color c = Color.valueOf(myColors.getString(index.toString()));
        myPen.setPenColor(c);
    }
}