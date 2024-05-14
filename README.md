# Scala-Discount-Rule-Engine
## Introduction
This project leverages Scala's functional programming paradigm to process orders, calculate discounts, and store the results in a database. It emphasizes a modular approach, dividing the functionality into three main stages: general functions, qualifier functions, and calculation functions.

## Functionalities
The project offers the following key functionalities:

- **Order Processing:** Reads orders from a CSV file, applies discount rules, and calculates final prices.
- **Discount Calculation:** Implements various discount calculation rules based on different criteria such as expiry date, product category, quantity, etc.
- **Database Interaction:** Stores processed orders along with relevant information in a database.
- **Logging:** Logs events such as order processing and database interactions.

## Functional Programming Approach
### General Functions
The project begins with general-purpose functions responsible for basic operations like file reading, logging, and database interaction. These functions provide a foundation for subsequent stages.

### Qualifier Functions and Calculation Functions
The core of the project lies in the separation of qualifier functions and calculation functions. Qualifier functions determine whether an order qualifies for a specific discount rule based on predefined criteria. Calculation functions then compute the actual discount based on the qualifying criteria.

- **Qualifier Functions:** These functions examine each order and return a Boolean value indicating whether it meets specific criteria. For example, `ProductsOnSale2()` checks if a product is eligible for a discount based on its category.
  
- **Calculation Functions:** Once an order qualifies for a discount based on the qualifier functions, calculation functions compute the discount amount. For instance, `calc2()` calculates the discount percentage based on the type of product.

### Applying Rules to Orders
The project combines qualifier functions and calculation functions through higher-order functions, allowing for flexible and scalable rule definitions. The `Rules()` function aggregates pairs of qualifier and calculation functions, enabling the application of multiple discount rules to each order.

## Requirements
- Java Development Kit (JDK)
- Scala SDK
- Oracle Database (or any other supported database)



