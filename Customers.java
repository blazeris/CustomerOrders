package csulb.cecs323.model;

import javax.persistence.*;
import java.util.Objects;
/*
 * Licensed under the Academic Free License (AFL 3.0).
 *     http://opensource.org/licenses/AFL-3.0
 *
 *  This code is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, other than educational.
 *
 *  2021 David Brown <david.brown@csulb.edu>
 *
 */

@Entity
@NamedNativeQuery(
        name = "ReturnCustomers",
        query = "SELECT * " +
                "FROM   CUSTOMERS",
        resultClass = Customers.class
)
@NamedNativeQuery(
        name = "ReturnCustomer",
        query = "SELECT * " +
                "FROM   CUSTOMERS " +
                "WHERE CUSTOMER_ID = ? ",
        resultClass = Customers.class
)

@Table(uniqueConstraints = {@UniqueConstraint(columnNames =
        {"first_name", "last_name", "phone"})})

/** A person, who has, or might, order products from us. */
public class Customers {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    /** Surrogate key for customer.  We don't want to
     migrate last name, first name, & phone all over the place.
     */
    private long customer_id;
    @Column (nullable = false, length = 64)
    /** Customer surname */
    private String last_name;
    @Column (nullable = false, length = 64)
    /** Customer given name */
    private String first_name;
    @Column (nullable = false, length = 64)
    /** Street address, minus the zipcode */
    private String street;
    @Column (nullable = false, length = 10)
    /** Zip code for the customer */
    private String zip;
    @Column (nullable = false, length = 20)
    /** Their phone number, with no particular validation */
    private String phone;

    /**
     *Default constructor for a customer.
     */
    public Customers() {} // end of Customers()

    /**
     *Parameterized constructor for a customer.
     * @param last_name Customer's last name
     * @param first_name Customer's first name
     * @param street Customer's street
     * @param zip Customer's zip code
     * @param phone Customer's phone number
     */
    public Customers (String last_name, String first_name, String street,
                      String zip, String phone) {
        this.last_name = last_name;
        this.first_name = first_name;
        this.street = street;
        this.zip = zip;
        this.phone = phone;
    }

    /**
     * Function to get the customer's ID number.
     * @return the customer's ID number
     */
    public long getCustomer_id() {
        return customer_id;
    }

    /**
     * Function to set the customer's ID to the value passed in.
     * @param customer_id The customer's ID number
     */
    public void setCustomer_id(long customer_id) {
        this.customer_id = customer_id;
    }

    /**
     * Function to get the customer's last name.
     * @return customer's last name
     */
    public String getLast_name() {
        return last_name;
    }

    /**
     * Function to set the customer's last name to the value passed.
     * @param last_name The customer's last name
     */
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    /**
     * Function to get the customer's first name
     * @return customer's first name
     */
    public String getFirst_name() {
        return first_name;
    }

    /**
     * Function to set customer's first name to the value passed.
     * @param first_name The customer's first name
     */
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    /**
     * Function to get the customer's street
     * @return The customer's street
     */
    public String getStreet() {
        return street;
    }

    /**
     * Function to set the customer's street name to the value passed
     * @param street The customer's street name
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Function to get the customer's zip code
     * @return The customer's zip code
     */
    public String getZip() {
        return zip;
    }

    /**
     * Function to set the customer's zip code to the value passed
     * @param zip The customer's zip code
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * Function to get the customer's phone number
     * @return The customer's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Function to set the customer's phone number to the value passed
     * @param phone The customer's phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * toString function for customers
     * @return The customer's ID, first name, and last name
     */
    @Override
    public String toString () {
        return "Customer-ID: " + this.customer_id + "\tName: " + this.last_name +
                ", " + this.first_name;
    }

    /**
     * Function to check if customer ID equals the object passed
     * @param o Object which is passed in to check if it equals
     * @return The customer ID which equals the object passed
     */
    @Override
    public boolean equals (Object o) {
        Customers customer = (Customers) o;
        return this.getCustomer_id() == customer.getCustomer_id();
    }

    /**
     * Hash function to map customer ID, last name, and first name
     * @return hash map
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getCustomer_id(), this.getLast_name(), this.getFirst_name());
    }
}