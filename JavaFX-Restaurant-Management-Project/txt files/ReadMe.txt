========================================================================================================================
=== I. BRIEF INTRODUCTION ==============================================================================================
========================================================================================================================
This is a restaurant software that supervises the operation of Cafe Bellissimo. The restaurant has three types of
employees: Server, Chef and Manager. There are eight tables in the restaurant.

------------------------------------------------------------------------------------------------------------------------
1.EVENTS that happens during the running time:
------------------------------------------------------------------------------------------------------------------------

    1. There can be multiple tables ordering different dishes at anytime。

    2. The table could only process an order util the customer has checked out or the table is empty.

    3. After the customer confirms that he/she finishes ordering, the orders with the correct additions or subtractions
    for each dish, will be sent to the Server to confirm. After the Server confirms to place orders for the Table, these
    orders will be sent to Chefs. The Chefs will confirm that each dish of the orders has been received and then cook the
    food. After cooking, the Chefs confirm that the food has been prepared and is waiting for the Server to deliver it
    to the Table. The Sever will see a message that shows the dish of some Table is ready to be delivered. For each dish
    that the Server delivers to the table, the server either confirms that it is delivered, or the customer gives some
    reasons to request back orders. The dish will be put back into the system (which means the Chef will recook the dish)
    if the Server thinks the reason is valid. Otherwise, the Server will confirm the dish was delivered. Finally, after
    the customer pay the bill (we have separate bill option), the entire procedure of the current Table's ordering is done.

    4. The new shipment has been recorded in the Events.txt, the relative message will be deleted after it is loaded
    into the inventory by some employees.

    5. Ingredients.txt records the ingredients of the inventory and the amount of the ingredient keeps updating since the
    Chef keeps making food (using ingredients). Inventory can automatically check shortage at anytime and write
    request.txt when some ingredient is under threshold. The Manager could copy paste the relative request and manually
    change the amount the restaurant required and then send e-mails to the supplier.

    6. Employees.txt records all the employee ID and their passwords for login into the restaurant system.

    7. Menu.txt records the name and prices of all the dishes of the restaurant and Recipes.txt records the recipes of
    all the dishes. dishIngredients.txt records the ingredients that can be added or subtracted for each dish.

    8. All the activities are written into the log.txt automatically. The Manager could check inventory, logging files,
    undelivered dishes and the all payments at any time.

------------------------------------------------------------------------------------------------------------------------
2. FEATURES we have:
------------------------------------------------------------------------------------------------------------------------
    1. Aim for a higher functional and more realistic design of the restaurant program, we create interface for customers.
    Therefore, the customers are able to do more operations such as confirming that they receive orders and
    requesting back orders by clicking buttons.

    2. Menu can be updated while running the program. The menu can be changed by adding dish name and ingredients to
    corresponding .txt files(Menu.txt, dishIngredients.txt and Recipes.txt) in corresponding format and adding the image
    of the dish to the picture folder.

    3. The program keeps reading from .txt files to update the content while running the program. (This makes sure that
    the data is updated immediately)

    4. There are many buttons in each interface. Therefore, we disable some buttons first in order to make the users more easily
    use our program. For example, the chef can only ask for deliver after he takes the order and the customer can only
    use most of the buttons in the customer interface after he enters the table number and number of customers.

------------------------------------------------------------------------------------------------------------------------
3. OVERALL DESIGNS:
------------------------------------------------------------------------------------------------------------------------
    1. MVC pattern: use the Module-View-Controller pattern to handle the entire program. Without this patter, there will
    be tons of codes about setOnAction in the Main, which is over complicated. For each view, we have one controller and
    for each controller we have one model. This follows the Single responsibility principle, and reduce the duplicate
    and complicate interactions among the classes.

    2. Dish Builder: we create DishBuilder to generate the dishes. This reduces the possibilities of making errors since
    the customers are allowed to add or subtract ingredients, which causes the name of the dish too complicated.

    3. DependencyInjection design for the Restaurant. We create class Restaurant and declare static variables in it. We
    initialize those variables outside the class. Class Restaurant is like a database that stores the processing orders,
    undelivered orders, all the payments received and etc. Thus, our models such as class Manager and class Server can
    use these data at any time instead of saving these data into these models. In this case, we can use this to reduce
    the dependency, which follows the SOLID principle, especially the Interface segregation principle.

    4.potential design patterns that we have concerned but did not use for some reasons:
        (1)observer pattern for the class Restaurant and models: the observer pattern can be used for transfer data
        between class Restaurant and the models. However, since there are not many variables needs to be transferred and
        different models need different data at different stage of the process, it is somehow inefficient to use an
        Observer Pattern to transfer the data and the data might be lost during the process. It is more convenient to
        write getter methods in different models.
        (2)factory pattern for Buttons: the Buttons that have similar functionality are log off button and check shipment
        Button, the factory class here is considered as a lazy class. So we give it up for the Code Smell concerns
        (3)factory pattern for Employee: Considered that the use of Employee Factory will only has a
            positive effect on improving on initialization, it causes the lazy class problems,  So we give up for
            concerns of Code Smell problems.
        (4)state pattern for the order procedure: since we change the entire design to a MVC pattern, at set the Restaurant
        as a database to store all states of orders that are required by the other classes, the state pattern is no
        longer needed.

    5. Coding Style: the naming and coding style this program is tailored towards the programmers' preferences, but still
       remain clear and readable.

    6. SOLID Principles: this program is designed under the SOLID design principles for object-oriented programming.
       Each class in the program has its own responsibility, and is almost not dependent upon other classes.
       Many features that can make future modification, such as menus.


========================================================================================================================
====== II. HOW TO PROCEED  ==============================================================================================
========================================================================================================================

------------------------------------------------------------------------------------------------------------------------
1. FOR THE CUSTOMER
------------------------------------------------------------------------------------------------------------------------
    1.(aim to: log in and log out the Customer interface)
        Seen the Welcome interface,
            click "Customer",
        process to the Customer interface to start the ordering,
            enter the table number (which should be from 00 to 08, otherwise the buttons never show up),
            enter the number of customers for this table (which should be an integer, otherwise it will not allow ordering),
                (Note: the current design is set the customer number at the first time, and after that time before
                checkout, the number can be entered for any integer, just as a design for login for presentation but not
                actually counts for tip percentage and dividing bills)
            click "send" to start ordering,
        process to see the orders.
            click "log off" to switch between multiple interfaces.

    2.(aim to: Customer place the orders to p)
        At the current Customer interface,
            click on " Appetizer", "Entree"... of the upper bar to choose the category of dishes,
            click on the picture to see a choice of addition or subtraction and the choice to choose this dish,
            click on "add" or "subtract" to change some ingredients of this dish
                (Note: the # displayed is the # you add or subtract)
                (Note: you cannot subtract infinitely as the amount of any ingredients should be more than 0)
                (Note: if there is an ingredient for this dish that is of shortage after the change, than you cannot add
                 or subtract it)
            click on "confirm" to confirm choose this dish
            (Note: if there is an ingredient is of shortage, the window will not close)
            click on "view orders" to see what are currently ordered on the listView,
            click on the item in the litView of orders and click on "cancel dish " if want to cancel it before place it,
        loop in the lines before until have chosen all the wanted dishes and complete the order.
            click on "finish ordering" to pass the order to the Restaurant and wait a server to confirm,
            click on the "undelivered Orders" to see all the waiting orders that has been placed.
        can also loop in step2 if want to add new dishes at any time before checkout even after have already placed one.

    3.(aim to: Customer receive the order)
        At the current Customer interface,
            click on the "undeliveredOrders",
            click on the Dish in the listView to select it,
                and then click on "send the dish back" if want to return the dish for some reason
                else if, click on "receive dish" to confirm the dish has been delivered and accepted,
                (Note: only the selected items that has been prepared and been picked up by the server is allowed to be
                received or returned )

    4.(aim to: Customer pay the bill)
        At the current Customer interface,
        only after all the orders are received,
            click "make payment", which can only work  after all the orders are received
            select the tip percentage on the pop-up window and click "confirm", for table that has more than 8 customers,
                the tip percentage choice will start from 15.
            click on "yes" or "no" to choose if want to separate the bill, and for now, we can see the bill

    5.(aim to: Customer log off)
            click "log off" and see the Welcome interface
        process to the Employee interfaces.


------------------------------------------------------------------------------------------------------------------------
2. FOR THE SERVER
------------------------------------------------------------------------------------------------------------------------

    1.(aim to: Server log in and log off)
        Seen the Welcome view,
            click "Employee" and log in with username and password, which is a 4 digit Id number starts with "03" in
                Employees.txt and the 1 digit password following by it, then click on "log in" to enter the Server view.
        Seen the Server view,
            click "log off" to log of and switch to other interfaces.


    2.(aim to: check the orders to be placed and confirm the order to proceed them System for the dishes to Cook)
        Seen the Server view,
            click on the "check orders" to see a window of all the tables that have orders not be confirmed into the restaurant system;
            select the number of the table you are going to serve;
            click on "View Orders" to see what this table currently ordered that hasn't been sent to the chefs;
            click on "Place Orders" to push these orders into the cooking system.

    3.(aim to: check the orders of a table)
         Seen the Server view,
            click on the the "Order" and then the corresponding button of the table number, then you can see a list of
                undelivered dishes for this table in the ListView;
            click on the the "Back Orders" and then the corresponding button of the table number, then you can see a list of
                dishes that are requested to send back for this table in the ListView.

    4.(aim to: get the delivery note and confirm pick up or won't do anything else)
        Seen the Server view,
            click on any blank of the view, if there is any dishes to be pick up, then there is a window pop out;
            click on "confirm" to confirm pick up these dishes, before this, you won't be able to do other actions ont this view.

    5.(aim to: accept or reject a dish that is asked to send back)
        Seen the Server view,
            check the 'back orders' of a specific table andif it is not empty, talk with the Table,
            select dish,
            click on "Accept Back Orders" to accept this dish to be sent back, and the new same dish will be placed and
                proceed to the kitchen to be cooked;
            click on "Reject Back Orders to "reject this dish to be sent back, and this will automatically set the table
                to receive this dish and remove it from the waiting-order-list.

    6.(aim to: server load shipments)
        Seen the Server view,
            click on "Check Shipment" to see the ingredients to be loaded
            select an ingredient in the list to load;
            click on "confirm" to start this job;
            click on "Finish Loading" after scanned this ingredient to the inventory.

------------------------------------------------------------------------------------------------------------------------
3.FOR THE CHEF
------------------------------------------------------------------------------------------------------------------------

    1.(aim to: Chef log in and log off)
        Seen the Welcome view,
            click "Employee" and log in with username and password, which is a 4 digit Id number starts with "02" in
                Employees.txt and the 1 digit password following by it, then click on "log in" to enter the Chef view.
        Seen the Chef view,
            click "log off" to log of and switch to other interfaces.

    2.(aim to: check the current list of dishes that need to be cook)
        Seen the Chef view,
            click on "Check Dishes" to check the currently available dishes to be cook; then:
            select a dish in the List View; then:
            click on "Take the Order" to receive this and start cooking; then:
            click on "Ask for Deliver" after repared this dish, and only after this can a chef receive another new dish.

    3.(aim to: chef load shipment)
        Seen the Chef view,
            click on "Check Shipment" to see the ingredients to be loaded
            select an ingredient in the list to load;
            click on "confirm" to start this job;
            click on "Finish Loading" after scanned this ingredient to the inventory.


------------------------------------------------------------------------------------------------------------------------
4.FOR THE MANAGER
------------------------------------------------------------------------------------------------------------------------

    1.(aim to: Manager log in and log off)
        Seen the Welcome view,
            click "Employee" and log in with username and password, which is a 4 digit Id number starts with "01" in
                Employees.txt and the 1 digit password following by it, then click on "log in" to enter the Manager view.
        Seen the Manager view,
            click "log off" to log of and switch to other interfaces.

    2.(aim to: Manager check the current data)
        Seen the Manager view,
            if check the current inventory, click on "Inventory" and "update Inventory"
            if check the logging record, click on "logging"
            if check all the undelivered dishes, click on "Undelivered food"
            if check one days payment, click on "Check Payments", and type 'yymmdd' to search all the payments
                happened on a specific day;
            if check want to send email to request ingredients that are under threshold

    3.(aim to: Manager load shipments)
        Seen the Manager view,
            click on "Check Shipment" to see the ingredients to be loaded
            select an ingredient in the list to load;
            click on "confirm" to start this job;
            click on "Finish Loading" after scanned this ingredient to the inventory.
