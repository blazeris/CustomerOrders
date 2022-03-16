package csulb.cecs323.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
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

/** The primary key class for Orders */
public class Orders_pk implements Serializable {
    @Id
    @Column(nullable = false)
    /*
    Note carefully the name of this attribute.  It MUST be the
    same as the name of the object in the child that has the
    migrating foreign key from the parent.  Even though it's
    just a surrogate.
     */
    private long customer;
    @Id
    @Column(nullable = false)
    private LocalDateTime order_date;

    public Orders_pk () {}
    /**
     * A function to create an order.
     * @param customer_id the customer's id
     * @param order_date the date the order was placed
     */

    public Orders_pk (long customer_id, LocalDateTime order_date) {
        this.customer = customer_id;
        this.order_date = order_date;
    }
    /**
     * @return customer
     */
    public long getCustomer() {
        return customer;
    }

    public void setCustomer(long customer) {
        this.customer = customer;
    }
    /**
     * @return order_date
     */
    public LocalDateTime getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDateTime order_date) {
        this.order_date = order_date;
    }

    @Override
    /**
     * A function to create an order.
     * @param Object a
     * @return results
     */
    public boolean equals (Object o) {
        boolean results = false;
        if (this == o) {
            results = true;
        } else if (o == null || getClass() != o.getClass()) {
            results = false;
        } else {
            Orders_pk orders_pk = (Orders_pk) o;
            results =   this.getCustomer() == orders_pk.getCustomer() &&
                        this.getOrder_date() == orders_pk.getOrder_date();
        }
        return results;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getCustomer(), this.getOrder_date());
    }
}
