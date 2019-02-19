package ca.eli

import gremlin.scala.Graph
import org.apache.tinkerpop.gremlin.hadoop.Constants
import org.apache.tinkerpop.gremlin.hadoop.structure.io.VertexWritable
import org.apache.tinkerpop.gremlin.spark.structure.Spark
import org.apache.tinkerpop.gremlin.structure.Direction
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import gremlin.scala._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer
import org.apache.tinkerpop.gremlin.process.computer.traversal.TraversalVertexProgram
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.traverser.util.TraverserSet
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer

import scala.collection.JavaConverters._

object GremlinApp extends App {
  val graph: Graph = GraphFactory.open("/app/classes/hadoop-gryo-cluster-submit.properties")
  val g: GraphTraversalSource = graph.traversal().withComputer(classOf[SparkGraphComputer])

//  println(g.V().count().next())
//  println(g.V().out().out().values("name").next())

    val result = graph.compute()
      .result(GraphComputer.ResultGraph.NEW)
      .persist(GraphComputer.Persist.EDGES)
//      .program(TraversalVertexProgram.build().traversal(g.V().has("name", "josh").asAdmin()).create())
    .program(TraversalVertexProgram.build().traversal(
      graph.traversal, "gremlin-groovy", "g.V().has('name', 'josh')").create()
    ).submit().get()

  val traverserSet: TraverserSet[Vertex] = result.memory().get[TraverserSet[Vertex]](TraversalVertexProgram.HALTED_TRAVERSERS)

  assert(traverserSet.size() == 1)

  val spark = SparkSession.builder().master("spark://spark-master:7077").appName("inside-cluster").getOrCreate()

  val filteredIds: Set[AnyRef] = traverserSet.asScala.map(_.get().id()).toSet
//  val filteredIds = Set[AnyRef](new Integer(4))

  val filteredIdsBroadcast = spark.sparkContext.broadcast(filteredIds)

  println("filtered ids: " + filteredIds)

  // convert output location to RDD name
  val rddName = Constants.getGraphLocation(graph.configuration().getString(Constants.GREMLIN_HADOOP_OUTPUT_LOCATION))

  val rdd = Spark.getRDD(rddName).asInstanceOf[RDD[(AnyRef, VertexWritable)]]

  val rdds = Spark.getRDDs

  assert(rdds.size() == 1)
  assert(rdds.asScala.head == rdd)

  rdd foreach {
    case (id, writable) =>
      println("id: " + id)
      println(writable.get().valueMap())
      println(writable.get().edges(Direction.BOTH).asScala.toList)
  }

  val filteredRdd = rdd.filter {
    case (id, writable) =>
      println(s"filtering writable: $writable with id $id")
      println(s"filteredIds: $filteredIds")
      println(s"filteredIdsBroadcast: $filteredIdsBroadcast")
      println(s"filteredIdsBroadcast value: ${filteredIdsBroadcast.value}")
      filteredIdsBroadcast.value.contains(id)
  }

  assert(filteredRdd.count == 1)

  filteredRdd.collect().head match {
    case (id, writable) =>
      assert(writable.get().property[String]("name").value() == "josh")
  }

  assert(rdd.count == 6)
}
