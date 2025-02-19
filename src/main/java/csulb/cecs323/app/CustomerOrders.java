/*
 * Licensed under the Academic Free License (AFL 3.0).
 *     http://opensource.org/licenses/AFL-3.0
 *
 *  This code is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, other than educational.
 *
 *  2018 Alvaro Monge <alvaro.monge@csulb.edu>
 *
 */

package csulb.cecs323.app;

// Import all of the entity classes that we have written for this application.
import csulb.cecs323.model.*;
import org.eclipse.persistence.exceptions.DatabaseException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * A simple application to demonstrate how to persist an object in JPA.
 * <p>
 * This is for demonstration and educational purposes only.
 * </p>
 * <p>
 *     Originally provided by Dr. Alvaro Monge of CSULB, and subsequently modified by Dave Brown.
 * </p>
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
public class CustomerOrders {
   /**
    * You will likely need the entityManager in a great many functions throughout your application.
    * Rather than make this a global variable, we will make it an instance variable within the CustomerOrders
    * class, and create an instance of CustomerOrders in the main.
    */
   private EntityManager entityManager;

   /**
    * The Logger can easily be configured to log to a file, rather than, or in addition to, the console.
    * We use it because it is easy to control how much or how little logging gets done without having to
    * go through the application and comment out/uncomment code and run the risk of introducing a bug.
    * Here also, we want to make sure that the one Logger instance is readily available throughout the
    * application, without resorting to creating a global variable.
    */
   private static final Logger LOGGER = Logger.getLogger(CustomerOrders.class.getName());

   /**
    * The constructor for the CustomerOrders class.  All that it does is stash the provided EntityManager
    * for use later in the application.
    * @param manager    The EntityManager that we will use.
    */
   public CustomerOrders(EntityManager manager) {
      this.entityManager = manager;
   }

   public static void main(String[] args) {
      LOGGER.fine("Creating EntityManagerFactory and EntityManager");
      EntityManagerFactory factory = Persistence.createEntityManagerFactory("CustomerOrders");
      EntityManager manager = factory.createEntityManager();
      // Create an instance of CustomerOrders and store our new EntityManager as an instance variable.
      CustomerOrders customerOrders = new CustomerOrders(manager);

      // PROCEDURE PART 1
      customerOrders.promptCustomer();

      // PROCEDURE PART 2
      customerOrders.promptProduct();

      // PROCEDURE PART 3
       customerOrders.promptOrder();

       System.out.println("Completed Satisfactorily");
   } // End of the main method


    /**
     * Prompts the user for to input an order. They are able to enter as a new or existing
     * customer. Then they select a product that is available and how much of that they want.
     * They'll be served a bill and then they can choose to accept it or not.
     */
    private void promptOrder(){
        Customers targetCustomer = completePromptCustomer(); // Customer requesting order

        LocalDateTime targetDateTime = promptDateTime(); // Date and time customer requested/requests order

        String seller = promptSalesPerson(); // Name of customer's salesperson

        Orders createdOrder = new Orders(targetCustomer, targetDateTime, seller); // Order instance

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        this.entityManager.persist(createdOrder);

        promptOrderLines(createdOrder, tx);

        printOrder(createdOrder);

        confirmOrder(tx);

    } //end of promptOrders

    /**
     * Prints an order, line by line through order_lines
     * @param order Order to be printed
     */
    private void printOrder(Orders order){
        List<Order_lines> orderLines = getOrderLines(order); // List of order_lines that make up an order
        System.out.println("\nUPC\t\t\t\tName\t\tUnit Cost\tQuantity\tSubtotal");
        double totalCost = 0; // Total cost summed from each order line
        if(orderLines != null){
            for(Order_lines line: orderLines){
                System.out.println(line);
                totalCost += line.getQuantity() * line.getUnit_sale_price();
            } // end of for loop
        } // end of if statement
        System.out.println("TOTAL\t\t\t\t\t\t\t\t\t\t\t\t\t$" + totalCost);
    } // end of printOrder method

    /**
     * Prompts the user to either confirm or cancel their order, which will be done as they request.
     * @param tx EntityTransaction to commit or rollback to
     */
    private void confirmOrder(EntityTransaction tx){
        Scanner in = new Scanner(System.in); // Scanner for input

        System.out.println("\nAre you satisfied with this? Y/N");
        boolean foundSatisfaction = false; // Whether or not customer wants to accept order
        while(!foundSatisfaction){
            String satisfaction = in.nextLine(); // User's input
            switch(satisfaction.toUpperCase()){
                case "Y":
                    System.out.println("Order has been made, and you have been billed. Have a good day.");
                    foundSatisfaction = true;
                    tx.commit();
                    break;
                case "N":
                    System.out.println("Order cancelled.");
                    foundSatisfaction = true;
                    tx.rollback();
                    break;
            } // end of switch statement
        } // end of while loop
    } // end of confirmOrder method

    /**
     * For a given order, it'll allow the customer to input lines of
     * products they want.
     * @param createdOrder The order the orderlines will be created for
     * @param tx The entity transaction instance to persist into
     */
    private void promptOrderLines(Orders createdOrder, EntityTransaction tx){
        Scanner in = new Scanner(System.in); // Scanner for input
        boolean orderDone = false; // Whether or not customer wants to finish order
        while(!orderDone){
            Products targetProduct = promptProduct(); // Product desired to add as orderLine
            if(targetProduct == null){
                orderDone = true;
            } // end of if statement
            else {
                int quantityInStock = targetProduct.getUnits_in_stock(); // Quantity of product available
                System.out.println("\nPlease enter the quantity desired: ");
                int quantityDesired = in.nextInt(); // Quantity that customer wants
                in.nextLine();
                if(quantityDesired > 0){
                    boolean cancelProduct = false;
                    if(quantityDesired > quantityInStock) {
                        boolean optionSelected = false;
                        while(!optionSelected){
                            System.out.println("\nQuantity entered is greater than amount left in stock. Your options are:");
                            System.out.println("\t1. Buy whatever remaining stock is left. " +
                                    "\n\t2. Remove this product from the order." +
                                    "\n\t3. Cancel this order.");
                            System.out.println("Please choose an option: ");
                            String choice = in.nextLine(); // User's input
                            switch (choice) {
                                case "1": //buy whatever is left;
                                    quantityDesired = quantityInStock;
                                    optionSelected = true;
                                    break;
                                case "2": //remove this product from order;
                                    cancelProduct = true;
                                    optionSelected = true;
                                    break;
                                case "3": //cancel this order;
                                    tx.rollback();
                                    orderDone = true;
                                    optionSelected = true;
                                    System.out.println("Order cancelled.");
                                    break;
                                default:
                                    System.out.println("Error. Please enter 1, 2, or 3.");
                            } // end of switch statement
                        } // end of while loop
                    } // end of if(quantityDesired > quantityInStock)
                    if(!orderDone && !cancelProduct){
                        Order_lines createdOrderLine = new Order_lines(createdOrder, targetProduct, quantityDesired, targetProduct.getUnit_list_price());
                        this.entityManager.persist(createdOrderLine);
                        targetProduct.setUnits_in_stock(targetProduct.getUnits_in_stock() - createdOrderLine.getQuantity());
                    } // end of if statement
                } // end of if(quantityDesired > 0)
                else {
                    System.out.println("Invalid quantity, has to be greater than 0");
                } // end of else statement
            } // end of else statement
        } // end of while loop
    } // end of promptOrderLines method

    /**
     * Doesn't know whether or not the customer is new, so it asks which directs
     * it to the proper prompt.
     * @return Customer instance that is either found or created
     */
    private Customers completePromptCustomer(){
        Scanner in = new Scanner(System.in); // Scanner for input

        Customers targetCustomer = null; // Customer instance to be found
        boolean foundCustomer = false; // Whether or not Customer instance found
        while(!foundCustomer){
            System.out.println("Are you a new customer? Y/N");
            String inpNewCustomer = in.nextLine().toUpperCase(); // User's input
            if(inpNewCustomer.equals("Y")){
                targetCustomer = promptNewCustomer();
                if(targetCustomer != null){
                    foundCustomer = true;
                } // end of if statement
            } // end of if statement
            else if(inpNewCustomer.equals("N")){
                targetCustomer = promptCustomer();
                if(targetCustomer != null){
                    foundCustomer = true;
                } // end of if statement
            } // end of else if statement
            else {
                System.out.println("Input a Y or N, try again.");
            } // end of else statement
        } // end of while loop
        return targetCustomer;
    } // end of completePromptCustomer method

    /**
     * Prompts the user to select a customer from a list of customers stored in the database
     * @return The user's desired customer, or null if desired to skip prompt (usually just repeats prompt from completePromptCustomer)
     */
    private Customers promptCustomer(){
        Scanner in = new Scanner(System.in); // Scanner for input
        boolean foundID = false;
        Customers targetCustomer = null;
        while(!foundID){
            System.out.println("\nWhich customer are you? Select your customer ID from the following customers:");
            List<Customers> customers = getCustomers();
            if(customers != null){
                for(Customers customer: getCustomers()){
                    System.out.println("\t" + customer);
                } // end of for loop
                System.out.println("Type your customer id here (leave blank to skip): ");
                String id = in.nextLine();
                if(id.equals("")){
                    foundID = true;
                } // end of if statement
                else{
                    targetCustomer = getCustomer(id);
                    if(targetCustomer != null){
                        foundID = true;
                    } // end of if statement
                } // end of else statement
                if(!foundID){
                    System.out.println("Invalid customer ID! Try again.");
                } // end of if statement
            } // end of if statement
            else {
                System.out.println("No previously existing customers, please indicate as new customer");
                foundID = true;
            } // end of if statement
        } // end of while loop
        if(targetCustomer != null){
            System.out.println("You have selected: \n\t" + targetCustomer);
        } // end of if statement
        return targetCustomer;
    } // end of promptCustomer method

    /**
     * Prompts the user to enter the information for a new customer, which will also add the
     * information to persist in the database
     * @return The created custoemr object, or null if unable to be created
     */
    private Customers promptNewCustomer(){
        Scanner input = new Scanner(System.in);
        System.out.println("\nHello customer, can you please enter your first name:");
        String firstName = input.nextLine();
        System.out.println("Please enter your last name:");
        String lastName = input.nextLine();
        System.out.println("Please enter your phone number:");
        String phone = input.nextLine();
        System.out.println("Please enter your street:" );
        String street = input.nextLine();
        System.out.println("and last, your zip code:");
        String zip = input.nextLine();

        Customers targetCustomer = new Customers(lastName, firstName, street, zip, phone); // Customer instance created by user

        try{
            EntityTransaction tx = this.entityManager.getTransaction();
            tx.begin();
            this.entityManager.persist(targetCustomer);
            tx.commit();
            System.out.println("\nYou are: " + targetCustomer);
        } // end of try
        catch(DatabaseException e){
            //System.out.println(e);
            System.out.println("You're not a new customer!");
            targetCustomer = null;
        } // end of catch

        return targetCustomer;
    } //end of promptNewCustomer method

    /**
     * Prompts the user to input either a past date time when the order was placed, or to select the current date time
     * @return User's desired and valid date time
     */
    private LocalDateTime promptDateTime(){
        Scanner in = new Scanner(System.in);
        boolean foundDateTime = false;
        LocalDateTime targetDateTime = null;
        while(!foundDateTime){
            System.out.println("\nWhat date was the order placed (year-month-date)? Leave blank if you want to place it right now:\nEnsure single digits have a 0 in front i.e 2022-04-07");
            String inputDate = in.nextLine();
            LocalDateTime currentTime = LocalDateTime.now();
            if(inputDate.equals("")){
                foundDateTime = true;
                targetDateTime = currentTime;
            } // end of if statement
            else {
                System.out.println("\nWhat time was the order placed (hour:minutes)?:\nEnsure single digits have a 0 in front i.e 07:03");
                String inputTime = in.nextLine();
                // We'll ignore seconds
                String inputDateTime = inputDate + "T" + inputTime + ":00";
                try{
                    targetDateTime = LocalDateTime.parse(inputDateTime);
                    if(targetDateTime.isBefore(currentTime)){
                        foundDateTime = true;
                    }
                } // end of try
                catch (Exception e) {
                    System.out.println("Invalid format.");
                } // end of catch
            } // end of else statement
            if(!foundDateTime){
                System.out.println("Invalid date time! Ensure your selected date and time is before the present. Try again.");
            } // end of if statement
        } // end of while loop
        System.out.println("You have selected: \n\t" + targetDateTime.toLocalDate() + "\t" + targetDateTime.toLocalTime());
        return targetDateTime;
    } // end of promptDateTime method

    /**
     * Prompts the user to input the name of the sales person. This is a simple string.
     * @return Name of the sales person
     */
    private String promptSalesPerson(){
        Scanner in = new Scanner(System.in);
        System.out.println("\nWhat is the name of the salesperson?");
        return in.nextLine();
    } // end of promptSalesPerson

    /**
     * Prompts the user to select a product from a list of products stored in the database
     * @return The user's desired product, or null if user wants to skip prompt
     */
    private Products promptProduct(){
        Scanner in = new Scanner(System.in);
        boolean foundUPC = false;
        Products targetProduct = null;
        while(!foundUPC){
            System.out.println("\nWhich product would you like? Select the desired from the following products:");
            for(Products product: getProducts()){
                System.out.println("\t" + product);
            }
            System.out.println("Type your product UPC here (leave blank to end order): ");
            String upc = in.nextLine();
            if(upc.equals("")){
                foundUPC = true;
            } // end of if statement
            else {
                targetProduct = getProduct(upc);
                if(targetProduct != null){
                    foundUPC = true;
                }
            }
            if(!foundUPC){
                System.out.println("Invalid product UPC! Try again.");
            } // end of if statement
        } // end of while loop
        if(targetProduct != null){
            System.out.println("You have selected: \n\t" + targetProduct);
        } // end of if statement
        return targetProduct;
    } // end of promptProduct method

   /**
    * Create and persist a list of objects to the database.
    * @param entities   The list of entities to persist.  These can be any object that has been
    *                   properly annotated in JPA and marked as "persistable."  I specifically
    *                   used a Java generic so that I did not have to write this over and over.
    */
   public <E> void createEntity(List <E> entities) {
      for (E next : entities) {
         LOGGER.info("Persisting: " + next);
         // Use the CustomerOrders entityManager instance variable to get our EntityManager.
         this.entityManager.persist(next);
      } // end of for loop

      // The auto generated ID (if present) is not passed in to the constructor since JPA will
      // generate a value.  So the previous for loop will not show a value for the ID.  But
      // now that the Entity has been persisted, JPA has generated the ID and filled that in.
      for (E next : entities) {
         LOGGER.info("Persisted object after flush (non-null id): " + next);
      } // end of for loop
   } // End of createEntity member method

   /**
    * Think of this as a simple map from a String to an instance of Products that has the
    * same name, as the string that you pass in.  To create a new Cars instance, you need to pass
    * in an instance of Products to satisfy the foreign key constraint, not just a string
    * representing the name of the style.
    * @param UPC        The name of the product that you are looking for.
    * @return           The Products instance corresponding to that UPC.
    */
   public Products getProduct (String UPC) {
      // Run the native query that we defined in the Products entity to find the right style.
      List<Products> products = this.entityManager.createNamedQuery("ReturnProduct",
              Products.class).setParameter(1, UPC).getResultList();
      if (products.size() == 0) {
         // Invalid style name passed in.
         return null;
      } else {
         // Return the style object that they asked for.
         return products.get(0);
      }
   }// End of the getProduct method

    /**
     * Acquires a list of all products acquired from the database
     * @return The list of all Products
     */
   public List<Products> getProducts () {
      // Run the native query that we defined in the Products entity to find the right style.
      List<Products> products = this.entityManager.createNamedQuery("ReturnProducts",
              Products.class).getResultList();
      if (products.size() == 0) {
         // Invalid style name passed in.
         return null;
      } else {
         // Return the style object that they asked for.
         return products;
      }
   } // End of the getProduct method

    /**
     * Acquires a specific orderLine corresponding to an order
     * @param targetOrder The order that the orderLine belongs to
     * @return The list of all orderLines that correspond to an order
     */
    public List<Order_lines> getOrderLines (Orders targetOrder) {
        // Run the native query that we defined in the Products entity to find the right style.
        List<Order_lines> orderLines = this.entityManager
                .createNamedQuery("ReturnOrderLine", Order_lines.class)
                .setParameter(1, targetOrder.getCustomer().getCustomer_id())
                .setParameter(2, targetOrder.getOrder_date())
                .getResultList();
        if (orderLines.size() == 0) {
            // Invalid style name passed in.
            return null;
        } else {
            // Return the style object that they asked for.
            return orderLines;
        }
    } // End of the getProduct method

   /**
    * Acquires a Customer object corresponding to a customer_id from the database
    * @param customer_ID        The name of the product that you are looking for.
    * @return           The Customers instance corresponding to that customer_ID.
    */
   public Customers getCustomer (String customer_ID) {
      // Run the native query that we defined in the Products entity to find the right style.
      List<Customers> customers = this.entityManager.createNamedQuery("ReturnCustomer",
              Customers.class).setParameter(1, customer_ID).getResultList();
      if (customers.size() == 0) {
         // Invalid style name passed in.
         return null;
      } else {
         // Return the style object that they asked for.
         return customers.get(0);
      }
   }// End of the getCustomer method

    /**
     * Acquires all customer objects stored in the database
     * @return List of all Customer objects
     */
   public List<Customers> getCustomers() {
      // Run the native query that we defined in the Products entity to find the right style.
      List<Customers> customers = this.entityManager.createNamedQuery("ReturnCustomers",
              Customers.class).getResultList();
      if (customers.size() == 0) {
         // Invalid style name passed in.
         return null;
      } else {
         // Return the style object that they asked for.
         return customers;
      }
   }// End of the getCustomer method
} // End of CustomerOrders class
