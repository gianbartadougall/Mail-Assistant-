package RentalMS;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;

public class Controller {

    private GUI gui;
    private String host = "pop.gmail.com";
    private Task task;
    private StringProperty stringProp = new SimpleStringProperty();
    private int count;
    private boolean rootPathError = false;
    private DataController dataController = new DataController();

    public void startApp() {
        gui = new GUI();
        gui.createUserInterface(dataController);
        setActionsForCheckMailButton(gui.getCheckMailButton());
        createMailListener();
    }

    private void createMailListener() {
        stringProp.addListener((obs, oldValue, newValue) -> task.cancel(true));
    }

    private void setActionsForCheckMailButton(Button button) {
        button.setOnAction(e -> {
            // root path error is an error that occurs if the folder the user sets to save the photos in does not exist on the computer. Root path is set in settings in app
            if (rootPathError) {
                // resetting screen back to normal
                gui.getCheckMailButton().setText("Check for Mail");
                rootPathError = false;
                return;
            }

            if (!Mail.isReceivingMail()) {
                // If root path does not exist, the error will be displayed and the computer will not check for mail
                if (!Utils.RootPathIsValid(DataController.getRootFolder())) {
                    rootPathError = true;
                    // clicking resets the screen back to normal - see set on action above this one
                    gui.getCheckMailButton().setText("Invalid root path. click to continue");
                    return;
                }
                // root path is valid - computer will start loading icon and fetch mail on a new thread
                gui.startLoadingIcon();
                gui.checkMailButtonToRegular();
                fetchMail();
                new Thread(task).start();
            }
        });
    }

    // function creates fetches mail on a new thread
    private void fetchMail() {
        task = new Task<>() {
            @Override
            protected Void call() {
                // stringProp is an observable value with a change listner attached to it. When the value changes (why it has count++), it will cancel the task.
                // receive mail returns the number of new emails found
                stringProp.setValue(count++ + "," + Mail.receiveMail(host, dataController.getMainUser().getEmail(),
                                                                           dataController.getMainUser().getPassword()));
                System.out.println("task finished");
                return null;
            }
        };

        // when task is cancelled, this will set the checkMail button to alert mode and pass the number of emails found
        task.setOnCancelled(e -> {
            String[] data = stringProp.getValue().split(",");
            gui.checkMailButtonToAlert(Integer.parseInt(data[1]));
        });
    }
}
