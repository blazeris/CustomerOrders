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

/*
This is the junction table between Orders and Products.  We would
normally use the @ManyToMany mapping if we did not have the
additional columns of quantity and unit_sale_price to contend
with.  In that case, we could have let JPA generate the junction
table for us and left it at that.  As it is, however, we have
to build it ourselves.
One question that I have is if we had a model in which the
association class was the parent to some other class, would
that mean that you would have to explicitly declare the
association class the way that we are doing here.  It seems to
me that would be the case.
 */
@Entity
@NamedNativeQuery(
        name="ReturnOrderLine",
        query = "SELECT * " +
                "FROM   ORDER_LINES " +
                "WHERE CUSTOMER_ID = ? AND ORDER_DATE = ?",
        resultClass = Order_lines.class
)
@IdClass(Order_lines_pk.class)
/** The occurrence of a single Product within a single Order */
public class Order_lines {
    @Id
    @ManyToOne
    /** The order that this line belongs to. */
    private Orders order;
    @Id
    @ManyToOne
    /** The product ordered in this line item of the order. */
    private Products product;
    @Column(nullable=false)
    /** The number of this item in this order.  If the customer
     changes their mind and wants more of this item, we come
     back to this row and update the quantity.
     */
    private int quantity;
    @Column(nullable = false)
    /** The price of this item FOR THIS ORDER.  The customer might
     have scored a discount from the unit_list_price for this
     product that is only in effect for this sale.
     */
    private double unit_sale_price;

    /**
     *Default constructor for Order lines
     */
    public Order_lines(){} // end of Order_lines()

    /**
     * Constructor to set the number and cost of a particular product from an order
     * @param order the order that calls for these products
     * @param product the product being ordered
     * @param quantity number of products
     * @param unit_sale_price cost per unit of product
     */
    public Order_lines(Orders order, Products product, int quantity, double unit_sale_price){
        this.setOrder(order);
        this.setProduct(product);
        this.setQuantity(quantity);
        this.setUnit_sale_price(unit_sale_price);
    } // end of Order_lines()

    /**
     * Function to get a particular order
     * @return the order requested
     */
    public Orders getOrder() {
        return order;
    }

    /**
     * Function to set an order instance to an order passed in
     * @param order Placed by a customer, lists the product being ordered, the quantity, and the price.
     */
    public void setOrder(Orders order) {
        this.order = order;
    }

    /**
     * Function to get a particular product.
     * @return the product requested
     */
    public Products getProduct() {
        return product;
    }

    /**
     * Function to set a product instance to a product passed in
     * @param product the product being ordered
     */
    public void setProduct(Products product) {
        this.product = product;
    }

    /**
     * Function to get the quantity of a product being ordered
     * @return the quantity of product being ordered
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Function to set the quantity of a specific product being ordered
     * @param quantity number of products
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Function to get the unit sale price of a given product
     * @return The requested unit sale price of a product
     */
    public double getUnit_sale_price() {
        return unit_sale_price;
    }

    /**
     * Function to set the unit sale price to one passed in
     * @param unit_sale_price cost per unit of product
     */
    public void setUnit_sale_price(double unit_sale_price) {
        this.unit_sale_price = unit_sale_price;
    }

    /**
     * Function to check if a particular order ID equals another
     * @param o Object which is passed in to check if it equals
     * @return The order which equals the object passed
     */
    public boolean equals (Object o) {
        boolean results = false;
        if (this == o) {
            results = true;
        } else if (o == null || getClass() != o.getClass()) {
            results = false;
        } else {
            Order_lines ol = (Order_lines) o;
            results = this.getOrder().equals (ol.getOrder()) &&
                    this.getProduct() == ol.getProduct();
        }
        return results;
    }

    /**
     *Hash function to map order and product
     * @return hash map
     */
    public int hasCode () {
        return Objects.hash(this.getOrder(), this.getProduct());
    }

    /**
     * toString function for order lines
     * @return
     */
    @Override
    public String toString(){
        return product.getUPC() + "\t\t"
                + product.getProd_name() + "\t\t$"
                + unit_sale_price + "\t\tx"
                + quantity + "\t\t= $"
                + (unit_sale_price * quantity);
    }
}