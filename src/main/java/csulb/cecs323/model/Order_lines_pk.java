package csulb.cecs323.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.criteria.Order;
import java.io.Serializable;
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

/**
 * The primary key class for the Order_lines class.
 */
public class Order_lines_pk implements Serializable {
    @Id
    @Column(nullable = false)
    /*
    This makes perfect sense when you think about it, but it
    takes getting used to.  The parent Orders class has an
    Orders_pk class as its primary key, so that's what migrates
    in to this PK class.  We do not have to worry about the
    customer_id that comes down from Customers since that is
    encapsulated in the Orders_pk object.
     */
    /** The order containing the given product */
    private Orders_pk order;
    @Id
    @Column(nullable = false, length = 30)
    /** One product within this one order */
    private String product;

    /**
     * Default constructor for order_lines_pk
     */
    public Order_lines_pk() {}

    /**
     * Parameterized constructor for order_lines_pk
     * @param order The order containing the given product
     * @param product One product within this one order
     */
    public Order_lines_pk(Orders_pk order, String product) {
        this.order = order;
        this.product = product;
    }

    /**
     * Function to get a specific order
     * @return the requested order
     */
    public Orders_pk getOrder() {
        return order;
    }

    /**
     * Function to set a given order instance to the order passed in
     * @param order The order containing the given product
     */
    public void setOrder(Orders_pk order) {
        this.order = order;
    }

    /**
     * Function to get a given product
     * @return The requested order
     */
    public String getProduct() {
        return product;
    }

    /**
     * Function to set a product instance to the product passed in
     * @param product
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * Function to check if equal
     * @param o Object which is passed through to see if it's equal
     * @return The results
     */
    @Override
    public boolean equals(Object o) {
        boolean results = false;
        if (this == o) {
            results = true;
        } else if (o == null || getClass() != o.getClass()) {
            results = false;
        } else {
            Order_lines_pk olpk = (Order_lines_pk) o;
            results = this.getOrder().equals(olpk.getOrder()) &&
                    this.getProduct() == olpk.getProduct();
        }
        return results;
    }

    /**
     * Hash function to map order and product
     * @return hash map
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getOrder(), this.getProduct());
    }
}