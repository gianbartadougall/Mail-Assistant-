package RentalMS;

public class Email {

    private String recipient;
    private User sender;
    private String image;
    private String description;

    public Email(String recipient, User sender, String image, String description) {
        this.recipient = recipient;
        this.sender = sender;
        this.image = image;
        this.description = description;
    }

    public String getRecipient() {
        return recipient;
    }

    public User getSender() {
        return sender;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
}
