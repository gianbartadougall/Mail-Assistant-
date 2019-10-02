package RentalMS;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class DataController {

    private String fileName = "res/Data/users";
    private Scanner scanner;
    private static String lastSavedDirectory;
    private static String rootFolder;
    private User mainUser;

    public HashMap<String, User> loadData() {
        System.out.println("Loading data");
        int count = 0;
        HashMap<String, User> users = new HashMap<>();
        try {
            scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                String fileInformation = scanner.nextLine();
                if (!fileInformation.isEmpty()) {
                    if (fileInformation.contains("<rootFolder>")) {
                        String[] rootInfo = fileInformation.split("==");
                        rootFolder = rootInfo[1];
                        continue;
                    }
                    String[] userInfo = fileInformation.split(",");
                    User newUser = new User(userInfo[0], userInfo[1], userInfo[2]);

                    if (count == 0) {
                        System.out.println("main user = " + newUser);
                        mainUser = newUser;
                    } else users.put(userInfo[0], newUser);

                    count++;
                }
            }

        } catch (IOException e) {
            System.out.println("could not find file");
        } finally {
            scanner.close();
        }
        return users;
    }

    public void saveData(HashMap<String, User> users, String rootFolder) {
        try {
            PrintWriter writer = new PrintWriter(fileName, StandardCharsets.UTF_8);
            // writes the main user first
            writer.println(mainUser.getName() + "," + mainUser.getEmail() + "," + mainUser.getPassword());
            // writes any other users in system in. These users currently do nothing as the application does not support multiple users
            for(HashMap.Entry<String, User> e : users.entrySet()) {
                if (!e.getValue().getName().equals("Root Folder")) {
                    writer.println(e.getValue().getName()+","+e.getValue().getEmail()+","+e.getValue().getPassword());
                }
            }
            writer.println("<rootFolder>==" + rootFolder);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving data");
        }
    }

    public boolean saveToHardDriveWasUnsuccessful(MimeBodyPart image, String[] data, String rootFolder) throws IOException, MessagingException {
        // data[0] is the file path of image, data[1] is the name of the image
        String imageDir = rootFolder + constructFilePath(data[0]);
        if (!Files.exists(Paths.get(imageDir))) { // checks that if the imageDir already exists or not. if it doesn,t then the dirs will be made
            boolean dirsCreated = new File(imageDir).mkdirs();
            if (!dirsCreated) { // if dir create failed
                System.out.println("Unable to save data. Tried to create new Directory: + " + lastSavedDirectory + " but failed");
                return true;
            }
        }
        // saves image
        lastSavedDirectory = imageDir;
        image.saveFile(lastSavedDirectory + data[1]);
        return false;
    }

    // creates a file path using the format that should be made in email. Format should be :::folderName,folderName;nameOfPhoto.jpg:::
    private static String constructFilePath(String content) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : content.split(",")) {
            stringBuilder.append(s).append("/");
        } return stringBuilder.toString();
    }

    public String getLastSavedDirectory() {
        return lastSavedDirectory;
    }

    public void setRootFolder(String rootFolder) {
        DataController.rootFolder = rootFolder;
    }

    public static String getRootFolder() {
        return rootFolder;
    }

    public User getMainUser() {
        return mainUser;
    }

}
