package RentalMS;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LoadingIcon {

    private Circle blue, green;
    private TranslateTransition blueTrans, greenTrans;

    public LoadingIcon(int startX, int startY) {
        blue = createCircle(Color.AQUAMARINE, startX - 20, startY + 45, 3);
        green = createCircle(Color.GREENYELLOW, startX + 20, startY + 45, 3);
        blueTrans = createXAxisTransition(blue, 1600, 40);
        greenTrans = createXAxisTransition(green, 1600, -40);
    }

    public static Circle createCircle(Color color, int startX, int startY, int radius) {
        Circle cir = new Circle();
        cir.setFill(color);
        cir.setRadius(radius);
        cir.setLayoutY(startY);
        cir.setLayoutX(startX);
        return cir;
    }

    public static TranslateTransition createXAxisTransition(Circle circle, int cycleLength, int translationDistance) {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.millis(cycleLength));
        transition.setAutoReverse(true);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setToX(translationDistance);
        transition.setNode(circle);
        return transition;
    }

    public void startLoadingIcon() {
        blueTrans.play();
        greenTrans.play();
    }

    public void stopLoadingIcon() {
        blueTrans.stop();
        greenTrans.stop();
    }

    public Circle getBlue() {
        return blue;
    }

    public Circle getGreen() {
        return green;
    }
}
