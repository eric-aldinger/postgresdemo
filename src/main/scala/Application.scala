import slick.lifted.ProvenShape
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.driver.PostgresDriver.api._

class Suppliers(tag: Tag)
  extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {

  // This is the primary key column:
  def id: Rep[Int] = column[Int]("SUP_ID", O.PrimaryKey)
  def name: Rep[String] = column[String]("SUP_NAME")
  def street: Rep[String] = column[String]("STREET")
  def city: Rep[String] = column[String]("CITY")
  def state: Rep[String] = column[String]("STATE")
  def zip: Rep[String] = column[String]("ZIP")

  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(Int, String, String, String, String, String)] =
  (id, name, street, city, state, zip)
}

class Query{

  def setupSup(db: Database) {
    try {
      // The query interface for the Suppliers table
      val suppliers: TableQuery[Suppliers] = TableQuery[Suppliers]

      val setupAction: DBIO[Unit] = DBIO.seq(
        // Create the schema by combining the DDLs for the Suppliers and Coffees
        // tables using the query interfaces
        (suppliers.schema).create,

        // Insert some suppliers
        suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
        suppliers += (49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
        suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")
      )

      val setupFuture: Future[Unit] = db.run(setupAction)
      val f = setupFuture.flatMap { _ =>


        val allSuppliersAction: DBIO[Seq[(Int, String, String, String, String, String)]] =
          suppliers.result

        val combinedAction: DBIO[Seq[(Int, String, String, String, String, String)]] =
          allSuppliersAction

        val combinedFuture: Future[Seq[(Int, String, String, String, String, String)]] =
          db.run(combinedAction)

        combinedFuture.map { allSuppliers =>
          allSuppliers.foreach(println)
        }

      }.flatMap { _ =>

        /* Manual SQL / String Interpolation */

        // A value to insert into the statement
        val state = "CA"

        // Construct a SQL statement manually with an interpolated value
        val plainQuery = sql"select SUP_NAME from SUPPLIERS where STATE = $state".as[String]

        println("Generated SQL for plain query:\n" + plainQuery.statements)

        // Execute the query
        db.run(plainQuery).map(println)

      }
      Await.result(f, Duration.Inf)

    } finally db.close
  }
}

object Application extends App {
  val db = Database.forConfig("test")
  val q = new Query
  q.setupSup(db)
}
