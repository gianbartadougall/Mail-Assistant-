package RentalMS;

import javafx.scene.control.Button;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {

    public static void addHoverActionForButton(Button button) {
        button.setOnMouseEntered(e -> changeButtonStyleToHover(button));
        button.setOnMouseExited(e -> changeButtonStyleToRegular(button));
    }

    public static void changeButtonStyleToHover(Button button) {
        button.setStyle("-fx-border-color: #808080; -fx-background-color: transparent; -fx-text-fill: #808080; -fx-font-size: 16;");
    }

    public static void changeButtonStyleToRegular(Button button) {
        button.setStyle(("-fx-border-color: white; -fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16;"));
    }

    public static boolean RootPathIsValid(String rootFolder) {
        return Files.exists(Paths.get(rootFolder));
    }

    private static String loc = "C://Users/Gian/Desktop/4221/Java/Java Programs/Test Projects/RentalMangementSystem/res/";

    private void sendTestEmail() {
        User user = new User("James", "giavanniavila@gmail.com", "DK-36 82.g/9");
        Email email = new Email(user.getEmail(), user, loc + "testPhoto1.jpg", "Bega Street,Driveway;Mountain.jpg");
//        Email email1 = new Email(user.getEmail(), user, loc + "testPhoto.jpg", "Wilston Road,Yard;Water.jpg");
//        Email email2 = new Email(user.getEmail(), user, loc + "autumnField.jpg", "70 Northam Avenue,Kitchen;AutumnField.jpg");
//        Email email3 = new Email(user.getEmail(), user, loc + "road.jpg", "Smith Street,Bedroom 2;Road.jpg");
        Mail.sendMail(email);
//        Mail.sendMail(email1);
//        Mail.sendMail(email2);
//        Mail.sendMail(email3);
    }

}
