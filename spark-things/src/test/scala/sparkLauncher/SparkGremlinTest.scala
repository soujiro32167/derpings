package sparkLauncher

import java.io.File

import gremlin.scala._
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.process.computer.traversal.TraversalVertexProgram
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import org.scalatest.{FlatSpec, Matchers}
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource._
import org.apache.tinkerpop.gremlin.spark.structure.Spark

import scala.collection.JavaConverters._

class SparkGremlinTest extends FlatSpec with Matchers {

  "spark with gremlin" should "do its thing" in {
    val graph: Graph = GraphFactory.open("src/test/resources/hadoop-gryo.properties")
    val g: GraphTraversalSource = graph.traversal().withComputer(classOf[SparkGraphComputer])
    //val spark = SparkSession.builder().getOrCreate()

    //graph.compute().program()
    val program = TraversalVertexProgram.build()
      .traversal(g, "gremlin-groovy", "g.V()")
      .create(graph)
    val result = graph.compute().program(program).submit().get()

    println("memory: " + result.memory())
    //println("" + result.graph().)

    result.close()

    //println(g.V().count().next())
    //println(g.V().out().out().values("name").next())

    //val rdds = Spark.getRDDs.asScala
    //rdds foreach println

    //Spark.close()
  }

  "spark with gremlin" should "use remote data" in {
    val g: GraphTraversalSource =
      traversal()
        .withRemote(DriverRemoteConnection.using("localhost", 8182, "g"))
          .withComputer(classOf[SparkGraphComputer])

    println(g.V().count().next())
    println(g.V().out().out().values("name").next())
  }

  "spark with gremlin" should "use remote spark" in {
    val graph: Graph = GraphFactory.open("src/test/resources/hadoop-gryo-cluster-submit.properties")
    val g: GraphTraversalSource = graph.traversal().withComputer(classOf[SparkGraphComputer])

    println(g.V().count().next())
    println(g.V().out().out().values("name").next())
  }

}
