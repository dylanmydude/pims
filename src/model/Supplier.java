package model;

public class Supplier {

    private int supplier_id;
    private String name;
    private String contact_person;
    private String phone;
    private String email;
    private String address;

    public Supplier() {
        // Default constructor.
    }

    public Supplier(
            int supplier_id,
            String name,
            String contact_person,
            String phone,
            String email,
            String address
    ) {
        this.supplier_id = supplier_id;
        this.name = name;
        this.contact_person = contact_person;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Supplier{"
                + "supplier_id=" + supplier_id
                + ", name='" + name + '\''
                + ", contact_person='" + contact_person + '\''
                + ", phone='" + phone + '\''
                + ", email='" + email + '\''
                + ", address='" + address + '\''
                + '}';
    }
}
