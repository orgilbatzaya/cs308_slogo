package view;

import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;


public class TurtleDisplay extends StackPane{
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

    private Canvas myCanvas;
    private GraphicsContext myGC;
    private Color penColor;
    private Color bgColor;
    private TurtleView myTurtle;
    private Point2D myPos;
    private ParallelTransition myCurrentAnimation;
    private Rectangle myBackground;

    public TurtleDisplay() {
        myBackground = new Rectangle(400, 400);
        myBackground.setFill(Color.WHITE);
        myCanvas = new Canvas(400,400);
        myGC = myCanvas.getGraphicsContext2D();
        myGC.setLineWidth(10);
        myCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,handler);
        penColor = Color.RED;
        bgColor = Color.WHITE;
        myTurtle = new TurtleView();
        myTurtle.getView().setVisible(true);
        this.getChildren().add(myBackground);
        this.getChildren().add(myCanvas);
        this.getChildren().add(myTurtle.getView());
        this.setAlignment(myTurtle.getView(), Pos.CENTER);
        Circle pen = new Circle(0, 0, 3);
        myPos = new Point2D(50, 340);
        Animation animation1 = move(myTurtle.getView(), Duration.seconds(10));
        myTurtle.move(100,0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(10000), myTurtle.getView());
        tt.setByX(100f);
        myCurrentAnimation = new ParallelTransition(animation1, tt);
        myCurrentAnimation.play();
        //tt.setCycleCount(4);
        //tt.setAutoReverse(true);
        System.out.println(myTurtle.getView().getX());
        System.out.println(myTurtle.getView().getY());
    }

    public Canvas getCanvas(){
        return myCanvas;
    }

    public EventHandler<MouseEvent> handler = new EventHandler<>() {

        public void handle(MouseEvent e) {
            double size = 10.0;
            double x = e.getX() - size/2;
            double y = e.getY() - size/2;
            myGC.setFill(penColor);
            myGC.setEffect(new DropShadow());
            myGC.fillOval(x,y,size,size);
        }
    };


    public GraphicsContext getGraphicsContext(){
        return myGC;
    }

    public void setPenColor(Color c){
        penColor = c;
    }
    public void setBgColor(Color c){
        myBackground.setFill(c);
    }

    public Animation move(ImageView v, Duration duration)
    {
        Path myPath = new Path();
        MoveTo initialPosition = new MoveTo(v.getX(), v.getY());
        LineTo lineTo = new LineTo();
        lineTo.setX(v.getX()+100);
        lineTo.setY(v.getY());
        myPath.getElements().add(initialPosition);
        myPath.getElements().add(lineTo);

        Circle pen = new Circle(0, 0, 3);
        TurtleView turt = new TurtleView();

        // create path transition
        PathTransition pathTransition = new PathTransition(duration, myPath, pen);
        pathTransition.currentTimeProperty().addListener(new ChangeListener<Duration>()
        {

            Point2D oldLocation = null;

            /**
             * Draw a line from the old location to the new location
             */
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {

                // skip starting at 0/0
                if( oldValue == Duration.ZERO)
                    return;

                // get current location
                double x = pen.getTranslateX();
                double y = pen.getTranslateY();

                // initialize the location
                if( oldLocation == null) {
                    oldLocation = new Point2D(x+myCanvas.getWidth() / 2, y+myCanvas.getHeight()/2);
                    return;
                }

                // draw line
                myGC.setStroke(Color.BLUE);
                myGC.setFill(Color.YELLOW);
                myGC.setLineWidth(4);
                myGC.strokeLine(oldLocation.getX(), oldLocation.getY(), x+ myCanvas.getWidth() / 2, y+myCanvas.getHeight()/2);

                // update old location with current one
                oldLocation = new Point2D(x+ myCanvas.getWidth() / 2, y+myCanvas.getHeight()/2);
            }
        });

        return pathTransition;
    }

    public ParallelTransition getCurrentAnimation() {
        return myCurrentAnimation;
    }

    public TurtleView getMyTurtle() {
        return myTurtle;
    }
}
