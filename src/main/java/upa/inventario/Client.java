package upa.inventario;

public class Client {
    private String name;
    private String address;
    private String subscription;
    private Integer phone;

    // Constructor
    public Client(String name, String address, Integer phone, String subscription) {
        this.name = name;
        this.address = address;
        this.subscription = subscription;
        this.phone = phone;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }
}
