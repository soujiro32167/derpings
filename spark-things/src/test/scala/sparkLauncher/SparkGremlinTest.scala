package sparkLauncher

import java.io.File

import gremlin.scala._
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.hadoop.Constants
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer
import org.apache.tinkerpop.gremlin.process.computer.traversal.TraversalVertexProgram
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import org.scalatest.{FlatSpec, Matchers}
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource._
import org.apache.tinkerpop.gremlin.spark.structure.Spark
import org.apache.tinkerpop.gremlin.spark.structure.io.PersistedInputRDD

import scala.collection.JavaConverters._

class SparkGremlinTest extends FlatSpec with Matchers {

  behavior of "spark with gremlin"

  ignore should "do its thing" in {
    val graph: Graph = GraphFactory.open("src/test/resources/hadoop-gryo.properties")
    val g: GraphTraversalSource = graph.traversal().withComputer(classOf[SparkGraphComputer])
    //val spark = SparkSession.builder().getOrCreate()

    println("traversal results:")
    println(g.V().count().next())
    println(g.V().out().out().values("name").next())

    println("rdds:")

    val rdds = Spark.getRDDs.asScala
    SparkContext.getOrCreate().getPersistentRDDs
    rdds foreach println

    //Spark.close()
  }

  it should "run a traversal vertex program" in {

    val graph: Graph = GraphFactory.open("src/test/resources/hadoop-gryo.properties")
    val g: GraphTraversalSource = graph.traversal().withComputer(classOf[SparkGraphComputer])

    graph.compute(classOf[SparkGraphComputer])
      .result(GraphComputer.ResultGraph.NEW)
      .persist(GraphComputer.Persist.EDGES)
      .program(TraversalVertexProgram.build.traversal(
        graph.traversal.withComputer(classOf[SparkGraphComputer]),
        "gremlin-groovy",
        "g.V().valueMap(true)").create(graph)).submit.get

    val rdds = Spark.getRDDs.asScala
    rdds foreach { rdd =>
      rdd foreach { item =>
        println(item)
      }
    }

    //Spark.close()
  }

  ignore should "use remote data" in {
    val g: GraphTraversalSource =
      traversal()
        .withRemote(DriverRemoteConnection.using("localhost", 8182, "g"))
          .withComputer(classOf[SparkGraphComputer])

    println(g.V().count().next())
    println(g.V().out().out().values("name").next())
  }

  ignore should "use remote spark" in {
    val graph: Graph = GraphFactory.open("src/test/resources/hadoop-gryo-cluster-submit.properties")
    val g: GraphTraversalSource = graph.traversal().withComputer(classOf[SparkGraphComputer])

    println(g.V().count().next())
    println(g.V().out().out().values("name").next())
  }

}
