package Project

import java.io.{File, FileOutputStream, PrintWriter}
import java.sql.{Date, DriverManager}
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate}
import scala.io.Source

object Project extends App {

  val orders = Source.fromFile("src/main/resources/TRX1000.csv").getLines().toList.tail

  def ExpireDays1(order: String): Boolean = {
    val orderDate = order.split(",")(0).substring(0, 10)
    val expiryDate = order.split(",")(2)

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val orderLocalDate = LocalDate.parse(orderDate, formatter)
    val expiryLocalDate = LocalDate.parse(expiryDate, formatter)

    val daysDifference = java.time.temporal.ChronoUnit.DAYS.between(orderLocalDate, expiryLocalDate)


    if (daysDifference < 30) {
      true
    } else {
      false
    }
  }

  def calc1(order: String): Double = {
    val orderDateStr = order.split(",")(0).substring(0, 10)
    val expiryDateStr = order.split(",")(2)

    val orderYear = orderDateStr.substring(0, 4).toInt
    val orderMonth = orderDateStr.substring(5, 7).toInt
    val orderDay = orderDateStr.substring(8, 10).toInt

    val expiryYear = expiryDateStr.substring(0, 4).toInt
    val expiryMonth = expiryDateStr.substring(5, 7).toInt
    val expiryDay = expiryDateStr.substring(8, 10).toInt

    val orderTotalDays = orderYear * 365 + orderMonth * 30 + orderDay
    val expiryTotalDays = expiryYear * 365 + expiryMonth * 30 + expiryDay

    val daysDifference = expiryTotalDays - orderTotalDays
    //println("daysDifference using days between : " + daysDifference)

    def calculateDiscount(daysRemaining: Int, acc: Double = 0.0): Double = {
      if (daysRemaining >= 30) 0.0 else {
        if (daysRemaining >= 29) {
          acc + 0.01 * (daysRemaining - 28)
        } else {
          calculateDiscount(daysRemaining + 1, acc + 0.01)
        }
      }
    }

    calculateDiscount(daysDifference)
  }

  def ProductsOnSale2(order: String): Boolean = {
    val product = order.split(",")(1)
    val check = product match {
      case p if p.startsWith("Cheese") => true
      case p if p.startsWith("Wine") => true
      case _ => false
    }
    check
  }

  def calc2(order: String): Double = {
    val product = order.split(",")(1)

    val disc = product match {
      case p if p.startsWith("Cheese") => 0.1
      case p if p.startsWith("Wine") => 0.05
      case _ => 0
    }
    disc
  }

  def products23th(order: String): Boolean = {
    val orderDate = order.split(",")(0).substring(0, 10)
    val orderMonth = orderDate.substring(5, 7).toInt
    val orderDay = orderDate.substring(8, 10).toInt

    if (orderMonth == 3 & orderDay == 23) true
    else false
  }

  def calc3(order: String): Double = {
    val orderDate = order.split(",")(0).substring(0, 10)
    val orderMonth = orderDate.substring(5, 7).toInt
    val orderDay = orderDate.substring(8, 10).toInt

    if (orderMonth == 3 & orderDay == 23) 0.5
    else 0.0
  }

  def NoOfQuan4(order: String): Boolean = {
    val product = order.split(",")(3).toInt
    val check = product match {
      case p if p > 5 => true
      case _ => false
    }
    check
  }

  def calc4(order: String): Double = {
    val product = order.split(",")(3).toInt
    val check = product match {
      case p if p >= 6 & p <= 9 => 0.05
      case p if p >= 10 & p <= 14 => 0.07
      case p if p >= 15 => 0.1
      case _ => 0
    }
    check
  }

  def SalesThroughApp(order: String): Boolean = {
    val channel = order.split(",")(5)
    channel == "App"
  }

  def calc5(order: String): Double = {
    val quantity = order.split(",")(3).toInt
    val discount = quantity match {
      case q if q <= 5 => 0.05
      case q if q <= 10 => 0.1
      case q if q <= 15 => 0.15
      case _ => 0.0 // No discount for quantities above 15
    }
    discount
  }

  def SalesWithVisa(order: String): Boolean = {
    val paymentMethod = order.split(",")(6)
    paymentMethod == "Visa"
  }

  def calc6(order: String): Double = {
    val discount = 0.05
    discount
  }

  def Rules(): List[(String => Boolean, String => Double)] = {
    List((ProductsOnSale2, calc2),
      (NoOfQuan4, calc4),
      (ExpireDays1, calc1),
      (products23th, calc3),
      (SalesThroughApp, calc5),
      (SalesWithVisa, calc6))
  }

  def log_event(writer: PrintWriter, file: File, log_level: String, message: String): Unit = {
    writer.write(s"Timestamp: ${Instant.now()}\tLogLevel: ${log_level}\tMessage: ${message}\n")
    writer.flush()
  }

  val f: File = new File("src/main/resources/logs.txt")
  val writer = new PrintWriter(new FileOutputStream(f, true))
  log_event(writer, f, "info", "OpenningWriter")


  def calculate_discount(order: String, Rules: List[(String => Boolean, String => Double)]): String = {
    val x = Rules.filter(_._1(order)).map(_._2(order)).take(2)
    val y: Double = if (x.isEmpty) 0 else (x.sum / x.length.toDouble)
    val quan = order.split(",")(3).toInt
    val unitprice = order.split(",")(4).toDouble
    val finalprice = ((unitprice * quan) - (unitprice * quan * y))

    order + "," + y + "," + finalprice
  }

  val url = "jdbc:oracle:thin:@//localhost:1521/XE"
  val username = "SCALA"
  val password = "root"
  val connection = DriverManager.getConnection(url, username, password)

  def write_to_db(order: String): Unit = {
    order.split(",").toList match {
      case orderDateStr :: productName :: expiryDateStr :: quantityStr :: unitPriceStr :: channel :: paymentMethod :: discountStr :: finalPriceStr :: Nil =>
        try {
          val orderDate = LocalDate.parse(orderDateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
          val expiryDate = LocalDate.parse(expiryDateStr)
          val quantity = quantityStr.toInt
          val unitPrice = unitPriceStr.toDouble
          val daysToExpiry = java.time.temporal.ChronoUnit.DAYS.between(orderDate, expiryDate).toInt
          val productCategory = productName.split(" - ")(0)
          val discount = discountStr.toDouble
          val finalPrice = finalPriceStr.toDouble
          val insertStatement =
            """
              |INSERT INTO orders (order_date, expiry_date, days_to_expiry, product_category,
              |                   product_name, quantity, unit_price, channel, payment_method,
              |                   discount, total_due)
              |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
              |""".stripMargin

          val preparedStatement = connection.prepareStatement(insertStatement)
          log_event(writer, f, "info", s"Order ($order) Inserted")

          preparedStatement.setDate(1, Date.valueOf(orderDate.toString))
          preparedStatement.setDate(2, Date.valueOf(expiryDate.toString))
          preparedStatement.setInt(3, daysToExpiry)
          preparedStatement.setString(4, productCategory)
          preparedStatement.setString(5, productName)
          preparedStatement.setInt(6, quantity)
          preparedStatement.setDouble(7, unitPrice)
          preparedStatement.setString(8, channel)
          preparedStatement.setString(9, paymentMethod)
          preparedStatement.setDouble(10, discount)
          preparedStatement.setDouble(11, finalPrice)

          preparedStatement.executeUpdate()

          preparedStatement.close()

        } catch {
          case e: Exception =>
            e.printStackTrace()
            println(s"Failed to insert order into database: ${e.getMessage}")
            log_event(writer, f, s"Error in  ($order) ", e.getMessage)

        }

    }
  }


  val OrdersWithDiscount = orders.map(x => calculate_discount(x, Rules()))

  OrdersWithDiscount.foreach(write_to_db(_))
  connection.close()


}