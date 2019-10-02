package RentalMS;

import javafx.scene.control.Button;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.EOFException;
import java.io.IOException;
import java.util.Properties;

import static RentalMS.Enums.DIRECTORY_FAILURE;
import static RentalMS.Enums.EMAIL_DESCRIPTION_FAILURE;

public class Mail {

    public static int G_MAIL = 0;
    private static boolean receivingMail = false;
    //private static String lastSavedDirectory;

    public static void sendMail(Email email) {
        System.out.println("preparing to send mail");
        // createSenderProperties creates the connection to server
        Properties props = createSenderProperties(G_MAIL);

        // if props is null then there was an error creating the connection
        if (props == null) {
            return;
        }

        // Get the Session object.
        // Session is the code that logins in to the specified email account with password authentication
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email.getSender().getEmail(), email.getSender().getPassword());
            }
        });

        Message message = prepareMessage(session, email);

        // if message == null there was an error in creating the message
        if (message == null) {
            return;
        }

        try {
            System.out.println("sending Email");
            // this line of code sends the email
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println("message send was successful");
    }

    private static Properties createSenderProperties(int provider) {
        if (provider == G_MAIL) {
            Properties p = new Properties();
            p.put("mail.smtp.auth", "true");
            p.put("mail.smtp.starttls.enable", "true");
            p.put("mail.smtp.host", "smtp.gmail.com");
            p.put("mail.smtp.port", "587");
            return p;
        } else return null;
    }

    private static Properties createReceiverProperties(int provider, String host) {
        if (provider == G_MAIL) {
            Properties p = new Properties();
            p.put("mail.pop3.host", host);
            p.put("mail.pop3.port", "995");
            p.put("mail.pop3.starttls.enable", "true");
            return p;
        } else return null;
    }

    private static Message prepareMessage(Session session, Email email) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email.getSender().getEmail()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipient()));
            message.setSubject("a");

            // attaches text to Mime message
            MimeMultipart content = new MimeMultipart();
            MimeBodyPart mainPart = new MimeBodyPart();
            mainPart.setText(email.getDescription());
            content.addBodyPart(mainPart);

            // ataches email to mime message
            MimeBodyPart image = new MimeBodyPart();
            image.attachFile(email.getImage());
            content.addBodyPart(image);

            message.setContent(content);

            return message;
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // this deconstructs the email and gets the text version of it. This returns a html version of the whole email.
    private static String getText(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            System.out.println("is text");
            return (String)p.getContent();
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }

    public static int receiveMail(String host, String user, String password) {
        receivingMail = true;
        int numMessages = 0;
        try {

            System.out.println("connecting to servers");
            // connects to mail server and logins in with username and password
            Store store = connectToMailServer(host, user, password);

            if (store == null) {
                receivingMail = false;
                return -1;
            }

            // goes to the inbox of the email account and sets the abilities to read only
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array
            Message[] messages = emailFolder.getMessages();
            System.out.println("Num messages in inbox: " + messages.length);
            for (Message message : messages) {

                if (message.getSubject().equals("Photo")) {

                    Multipart mp = (Multipart) message.getContent(); // gets data of whole email
                    String[] data = extractTextFromEmail(mp); // gets text from email in form of filePathInfo (data[0]) and imageName (data[1])
                    if (data == null) {
                        receivingMail = false;
                        return EMAIL_DESCRIPTION_FAILURE;
                    }
                    System.out.println("extracted data: " + data[0] + ", " + data[1]);
                    MimeBodyPart image = extractImageFromEmail(mp);
                    if (new DataController().saveToHardDriveWasUnsuccessful(image, data, DataController.getRootFolder())) {
                        receivingMail = false;
                        return DIRECTORY_FAILURE;
                    }
                    numMessages += 1;
                }
            }
            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            receivingMail = false;
            return -1;
        }

        receivingMail = false;
        return numMessages;
    }

    private static Store connectToMailServer(String host, String user, String password) throws MessagingException {
        //create properties field
        Properties props = createReceiverProperties(G_MAIL, host);

        if (props == null) {
            return null;
        }

        Session emailSession = Session.getDefaultInstance(props);
        //create the POP3 store object and connect with the pop server
        Store store = emailSession.getStore("pop3s");
        System.out.println("connecting as " + user + " " + password);
        store.connect(host, user, password);
        return store;
    }

    private static String[] extractTextFromEmail(Multipart mp) throws IOException, MessagingException {
        String extractedHTML = getText(mp.getParent());
        // error if html is null
        if (extractedHTML == null) {
            return null;
        }
        // splits with ::: as email description should start with ::: and end with ::: so this part and only this part is taken from the extracted html text
        String[] requiredInfo = extractedHTML.split(":::");
        return requiredInfo[1].split(";"); // split with ; to seperate filePath info from image title info. description in email should be formatted :::FolderName,FolderName;imageTitle.jpg:::
    }


    private static MimeBodyPart extractImageFromEmail(Multipart mp) throws MessagingException {
        // this gets the image part of the email
        return (MimeBodyPart) mp.getBodyPart(1);
    }

    public static boolean isReceivingMail() {
        return receivingMail;
    }
}
