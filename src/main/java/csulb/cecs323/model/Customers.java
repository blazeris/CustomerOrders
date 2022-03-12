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
// I could have avoided uniqueConstraints and just done
// one constraint, but this was more fun.
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
    @Column(nullable=false, length = 64)
    /** Customer surname */
    private String last_name;
    @Column(nullable=false, length = 64)
    /** Customer given name */
    private String first_name;
    @Column(nullable = false, length = 64)
    /** Street address, minus the zipcode */
    private String street;
    @Column(nullable = false, length = 10)
    /** Zip code for the customer */
    private String zip;
    @Column(nullable = false, length = 20)
    /** Their phone number, with no particular validation */
    private String phone;

    /**
     *
     */
    public Customers() {

    } // end of Customers()

    /**
     *
     * @param last_name
     * @param first_name
     * @param street
     * @param zip
     * @param phone
     */
    public Customers (String last_name, String first_name, String street,
                      String zip, String phone) {
        this.last_name = last_name;
        this.first_name = first_name;
        this.street = street;
        this.zip = zip;
        this.phone = phone;
    }

    public long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(long customer_id) {
        this.customer_id = customer_id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString () {
        return "Customer- ID: " + this.customer_id + ", Name: " + this.last_name +
                ", " + this.first_name;
    }
    @Override
    public boolean equals (Object o) {
        Customers customer = (Customers) o;
        return this.getCustomer_id() == customer.getCustomer_id();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getCustomer_id(), this.getLast_name(), this.getFirst_name());
    }
}
