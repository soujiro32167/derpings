package sparkLauncher

import java.io.File

import gremlin.scala._
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.tinkerpop.gremlin.driver.Client.ClusteredClient
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.hadoop.Constants
import org.apache.tinkerpop.gremlin.hadoop.structure.io.VertexWritable
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer
import org.apache.tinkerpop.gremlin.process.computer.traversal.TraversalVertexProgram
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import org.scalatest.{FlatSpec, Matchers}
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource._
import org.apache.tinkerpop.gremlin.process.traversal.traverser.util.TraverserSet
import org.apache.tinkerpop.gremlin.spark.structure.Spark
import org.apache.tinkerpop.gremlin.spark.structure.io.PersistedInputRDD
import org.apache.tinkerpop.gremlin.structure.Direction

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
    // no RDDs since we did not persist
    rdds should have size 0

    //Spark.close()
  }

  it should "run a traversal vertex program" in {

    val graph: Graph = GraphFactory.open("src/test/resources/hadoop-gryo.properties")
    val g: GraphTraversalSource = graph.traversal().withComputer(classOf[SparkGraphComputer])

    val result = graph.compute(classOf[SparkGraphComputer])
      .result(GraphComputer.ResultGraph.NEW)
      .persist(GraphComputer.Persist.EDGES)
      .program(TraversalVertexProgram.build.traversal(
        graph.traversal.withComputer(classOf[SparkGraphComputer]),
        "gremlin-groovy",
        "g.V().has('name', 'josh')").create(graph)).submit.get

    val traverserSet: TraverserSet[Vertex] = result.memory().get[TraverserSet[Vertex]](TraversalVertexProgram.HALTED_TRAVERSERS)

    traverserSet should have size 1

    val filteredIds: Set[AnyRef] = traverserSet.asScala.map(_.get().id()).toSet

    // convert output location to RDD name
    val rddName = Constants.getGraphLocation(graph.configuration().getString(Constants.GREMLIN_HADOOP_OUTPUT_LOCATION))

    val rdd = Spark.getRDD(rddName).asInstanceOf[RDD[(Int, VertexWritable)]]

    val rdds = Spark.getRDDs

    rdds should have size 1
    rdds should contain only rdd

    rdd foreach {
      case (id, writable) =>
        println(writable.get().valueMap("id", "name", "age"))
        println(writable.get().edges(Direction.BOTH).asScala.toList)
    }

    val filteredRdd = rdd.filter{
      case (_, writable) => filteredIds.contains(writable.get().id())
    }

    filteredRdd.count shouldBe 1

    filteredRdd.collect().head match {
      case (id, writable) => writable.get().property[String]("name").value() shouldBe "josh"
    }

    rdd.count shouldBe 6
  }

  ignore should "use remote data and spark" in {
//    val g: GraphTraversalSource =
//      traversal()
//        .withRemote(DriverRemoteConnection.using("localhost", 8182, "sparkTraversal"))
//
//    println(g.V().has("name", "josh").next())


//    val cluster = Cluster.open()
//    val client = cluster.connect[ClusteredClient]()
//
//    client.submit(
//      """
//        |sparkGraph.compute()
//          |.result(GraphComputer.ResultGraph.NEW)
//          |.persist(GraphComputer.Persist.EDGES)
//          |.program(TraversalVertexProgram.build().traversal(g, "gremlin-groovy", "sparkTraversal.V()").create())
//          |.submit().get()
//      """.stripMargin
//    ).all().get()

    val spark = SparkSession.builder().master("spark://localhost:7077").appName("other-app").getOrCreate()

//    g.getGraph.compute()
//      .result(GraphComputer.ResultGraph.NEW)
//      .persist(GraphComputer.Persist.EDGES)
//      .program(TraversalVertexProgram.build().traversal(g, "gremlin-groovy", "sparkTraversal.V()").create())
//      .submit().get()

    println(spark.sparkContext.getPersistentRDDs)
    println(Spark.getRDDs)


//
//    // convert output location to RDD name
//    val rddName = Constants.getGraphLocation(g.getGraph.configuration().getString(Constants.GREMLIN_HADOOP_OUTPUT_LOCATION))
//
//    val rdd = Spark.getRDD(rddName)
////
////    val rdd = Spark.getRDD("output/~g")
//    val rdds = Spark.getRDDs
//
//    rdds should have size 1
//    rdds should contain only rdd
//
//
//    //rdds foreach { rdd =>
//    rdd foreach {
//      case (id, writable: VertexWritable) =>
//        println(writable.get().valueMap("id", "name", "age"))
//        println(writable.get().edges(Direction.BOTH).asScala.toList)
//    }
//    //}
//
//    rdd.collect() should have size 6

  }

  // this needs the input file to be on the same path in the driver as the executors
  ignore should "use remote spark" in {
    val graph: Graph = GraphFactory.open("src/test/resources/hadoop-gryo-client.properties")
    val g: GraphTraversalSource = graph.traversal().withComputer(classOf[SparkGraphComputer])

//    println(g.V().count().next())
//    println(g.V().out().out().values("name").next())

    g.getGraph.compute
      .result(GraphComputer.ResultGraph.NEW)
      .persist(GraphComputer.Persist.EDGES)
      .program(TraversalVertexProgram.build().traversal(g, "gremlin-groovy", "g.V()").create())
      .submit().get()

    // convert output location to RDD name
    val rddName = Constants.getGraphLocation(g.getGraph.configuration().getString(Constants.GREMLIN_HADOOP_OUTPUT_LOCATION))

    val rdd = Spark.getRDD(rddName)

    val rdds = Spark.getRDDs

    rdds should have size 1
    rdds should contain only rdd


    //rdds foreach { rdd =>
    rdd foreach {
      case (id, writable: VertexWritable) =>
        println(writable.get().valueMap("id", "name", "age"))
        println(writable.get().edges(Direction.BOTH).asScala.toList)
    }
    //}

    rdd.collect() should have size 6
  }

}
