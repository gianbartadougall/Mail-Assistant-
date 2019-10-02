package RentalMS;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import kradle.Display;
import kradle.Nodes.ButtonNode;
import kradle.Nodes.ImageNode;
import kradle.Nodes.LabelNode;
import kradle.Nodes.TextFieldNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import static RentalMS.Utils.*;
import static kradle.Enums.CENTER;
import static kradle.Layouts.LayoutBorderPane.createBorderPane;
import static kradle.Layouts.LayoutGridPane.createGridPane;
import static kradle.Layouts.LayoutGridPane.setGridPaneConstraints;
import static kradle.Layouts.LayoutStackPane.createStackPaneWithImageBackground;

public class GUI {

    private User mainUser;
    private Display display;
    private GridPane checkMailGrid;
    private BorderPane checkMailScreen, settingsScreen;
    private LoadingIcon loadingIcon;
    private ImageView background;
    private Button checkMailButton, locationButton, doneButton;
    private TextField nameTF, emailTF, passwordTF, rootFolderTF;
    private Label nameL, emailL, passwordL, rootFolderL;
    private boolean noAlerts = true;
    private DataController dataController;
    private HashMap<String, User> users;
    private StackPane stackPane;

    public void createUserInterface(DataController dataController) {
        this.dataController = dataController;
        users = dataController.loadData();
        mainUser = dataController.getMainUser();

        createCheckMailScreen();
        createSettingsScreen();

        display = new Display(667, 1000, "Rental Management System");
        loadingIcon = new LoadingIcon(display.getWidth() / 2, display.getHeight() / 2);

        try {
            background = ImageNode.createImage("res/road.jpg", display.getHeight());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        stackPane = createStackPaneWithImageBackground(background, checkMailScreen, null, null);
        display.createDisplay(stackPane, false);
    }

    private void createSettingsScreen() {
        GridPane settingsGrid = createGridPane(Pos.CENTER);
        settingsScreen = createBorderPane(settingsGrid, CENTER);
        createTextFields();
        createLabels();
        createToMainScreenButton();
        settingsGrid.getChildren().addAll(rootFolderL, rootFolderTF, nameL, nameTF, emailL, emailTF, passwordL, passwordTF);
    }

    private void createTextFields() {
        rootFolderTF = styledTextField(0, 1);
        rootFolderTF.textProperty().addListener(e -> {
            dataController.setRootFolder(rootFolderTF.getText());
            dataController.saveData(users, rootFolderTF.getText());
        });

        nameTF = styledTextField(1, 1);
        nameTF.textProperty().addListener(e -> {
            mainUser.setName(nameTF.getText());
            dataController.saveData(users, rootFolderTF.getText());
        });

        emailTF = styledTextField(2, 1);
        emailTF.textProperty().addListener(e -> {
            mainUser.setEmail(emailTF.getText());
            dataController.saveData(users, rootFolderTF.getText());
        });

        passwordTF = styledTextField(3, 1);
        passwordTF.focusedProperty().addListener((obs, oldVal, newVal) -> {
            String newPw = newVal ? mainUser.getPassword() : pwToAstrix(passwordTF.getText());
            mainUser.setPassword(passwordTF.getText());
            passwordTF.setText(newPw);
            dataController.saveData(users, rootFolderTF.getText());
        });
    }

    private void createLabels() {
        rootFolderL = styledLabel("Root Folder: ", 0, 0);
        nameL = styledLabel("Name: ", 1, 0);
        emailL = styledLabel("Email: ", 2, 0);
        passwordL = styledLabel("Password: ", 3, 0);
    }

    private Label styledLabel(String title, int row, int col) {
        Label label = LabelNode.createLabel(title, "transparent", "transparent", "white");
        setGridPaneConstraints(label, 1, row, col, null);
        return label;
    }

    private TextField styledTextField(int row, int col) {
        TextField textField = TextFieldNode.createTextField("", "transparent", "transparent", "white");
        setGridPaneConstraints(textField, 1, row, col, new Insets(0, 0, 0, 50));
        return textField;
    }

    private void createCheckMailScreen() {
        checkMailGrid = createGridPane(Pos.CENTER);
        checkMailScreen = createBorderPane(checkMailGrid, CENTER);
        createCheckMailButton();
        createLocationButton();
        createDoneButton();
        createSettingsButton();
    }

    // FUNCTIONS FOR CREATING BUTTONS
    private void createCheckMailButton() {
        checkMailButton = createButton("check for mail", 300, 40);
        setGridPaneConstraints(checkMailButton, 2, 0, 0, new Insets(0, 0, 15, 0));
        checkMailGrid.getChildren().add(checkMailButton);
        setActionsForCheckMailButton(checkMailButton);
    }

    private void setActionsForCheckMailButton(Button button) {
        button.setOnMouseEntered(e -> {
            if (!Mail.isReceivingMail() && noAlerts)
                Utils.changeButtonStyleToHover(button);
        });
        button.setOnMouseExited(e -> {
            if (noAlerts)
                checkMailButtonToRegular();
        });
    }

    private Button createButton(String title, int width, int height) {
        Button button = ButtonNode.createButton(title, width, height, Pos.CENTER, TextAlignment.CENTER, null);
        button.setStyle("-fx-border-color: white; -fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16;");
        return button;
    }

    private void createLocationButton() {
        locationButton = createButton("Show location", 140, 40);
        setGridPaneConstraints(locationButton, 1, 1, 0, new Insets(0, 20, 0, 0));
        locationButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Last Saved Photo");
            fileChooser.setInitialDirectory(new File(dataController.getLastSavedDirectory()));
            fileChooser.showOpenDialog(display.getStage());
            returnToHome();
        });
        Utils.addHoverActionForButton(locationButton);
    }

    private void createDoneButton() {
        doneButton = createButton("Done", 140, 40);
        setGridPaneConstraints(doneButton, 1, 1, 1, null, HPos.CENTER, null);
        doneButton.setOnAction(e -> returnToHome());
        Utils.addHoverActionForButton(doneButton);
    }

    private void createSettingsButton() {
        Button settingsButton = createButton("Settings", 100, 40);
        BorderPane.setMargin(settingsButton, new Insets(10, 0, 0, 10));
        settingsButton.setOnAction(e -> {
            if (!Mail.isReceivingMail())
                changeToSettingsScreen();
        });
        addHoverActionForButton(settingsButton);
        checkMailScreen.setTop(settingsButton);
    }

    private void createToMainScreenButton() {
        Button toMainScreenButton = createButton("Back", 100, 40);
        BorderPane.setMargin(toMainScreenButton, new Insets(10, 0, 0, 10));
        toMainScreenButton.setOnAction(e -> changeToCheckMailScreen());
        addHoverActionForButton(toMainScreenButton);
        settingsScreen.setTop(toMainScreenButton);
    }

    // FUNCTIONS FOR SETTINGS SCREEN

    private void changeToSettingsScreen() {
        stackPane.getChildren().removeAll(checkMailScreen);
        stackPane.getChildren().add(settingsScreen);
        loadSettingsScreenInfo();
        nameL.requestFocus();
    }

    private void loadSettingsScreenInfo() {
        rootFolderTF.setText(dataController.getRootFolder());
        nameTF.setText(mainUser.getName());
        emailTF.setText(mainUser.getEmail());
        passwordTF.setText(pwToAstrix(mainUser.getPassword()));
    }

    // changes the text to asterix's
    private String pwToAstrix(String password) {
        StringBuilder pw = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            pw.append('*');
        } return pw.toString();
    }

    private void changeToCheckMailScreen() {
        stackPane.getChildren().remove(settingsScreen);
        stackPane.getChildren().add(checkMailScreen);
    }

    // FUNCTIONS FOR MANIPULATING STATE OF BUTTONS
    public void checkMailButtonToRegular() {
        background.setOpacity(1);
        checkMailButtonSize(false);
        changeButtonStyleToRegular(checkMailButton);
    }

    public void checkMailButtonToAlert(int numEmails) {
        noAlerts = false;
        stopLoadingIcon();
        checkMailButtonSize(true);
        background.setOpacity(0.9);
        checkMailButton.setStyle("-fx-border-color: black; -fx-background-color: transparent; -fx-text-fill: black; " +
                "-fx-font-size: 16; -fx-font-weight: BOLD;");
        setAlertText(numEmails);
    }

    private void setAlertText(int numEmails) {
        // if num emails < 0 then that means there was an error in fetching the mail
        if (numEmails < 0) {
            checkMailButton.setText(numEmails < -1 ? "Error saving images. Check \ndescription of email and root folder " : "There was an error. Check internet \nconnection and login details");
        } else {
            checkMailButton.setText(numEmails + " New " + (numEmails == 1 ? "email" : "emails") + " found. " +
                    (numEmails == 0 ? "" : " \n " + (numEmails == 1 ? "Photo" : "Photos") + " successfully saved"));
        }
        if (numEmails <= 0) {
            // if there was 0 mail or an error, only the done button is displayed
            checkMailGrid.getChildren().add(doneButton);
        } else addSecondaryButtons(); // this shows the done and show location buttons
    }

    private void checkMailButtonSize(boolean large) {
        int height = large ? 80 : 40;
        checkMailButton.setMinSize(300, height);
        checkMailButton.setMaxSize(300, height);
    }

    // FUNCTIONS FOR CHANGING STATES
    public void returnToHome() {
        removeSecondaryButtons();
        getCheckMailButton().setText("check for mail");
        checkMailButtonToRegular();
        noAlerts = true;
    }

    public void addSecondaryButtons() {
        checkMailGrid.getChildren().add(locationButton);
        checkMailGrid.getChildren().add(doneButton);
    }

    public void removeSecondaryButtons() {
        checkMailGrid.getChildren().remove(locationButton);
        checkMailGrid.getChildren().remove(doneButton);
    }

    public void startLoadingIcon() {
        checkMailButton.setText("Checking Mail...");
        loadingIcon.startLoadingIcon();
        checkMailScreen.getChildren().addAll(loadingIcon.getBlue(), loadingIcon.getGreen());
    }

    public void stopLoadingIcon() {
        loadingIcon.stopLoadingIcon();
        System.out.println("removing loading icon");
        checkMailScreen.getChildren().removeAll(loadingIcon.getBlue(), loadingIcon.getGreen());
    }

    // GETTERS AND SETTERS

    public Button getCheckMailButton() {
        return checkMailButton;
    }

}
