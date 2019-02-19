package ca.eli

import gremlin.scala.Graph
import org.apache.tinkerpop.gremlin.hadoop.Constants
import org.apache.tinkerpop.gremlin.hadoop.structure.io.VertexWritable
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer
import org.apache.tinkerpop.gremlin.spark.structure.Spark
import org.apache.tinkerpop.gremlin.structure.Direction
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import gremlin.scala._
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer
import org.apache.tinkerpop.gremlin.process.computer.traversal.TraversalVertexProgram

import scala.collection.JavaConverters._

object GremlinApp extends App {
  val graph: Graph = GraphFactory.open("/app/classes/hadoop-gryo-cluster-submit.properties")
  val g: GraphTraversalSource = graph.traversal().withComputer(classOf[SparkGraphComputer])

//  println(g.V().count().next())
//  println(g.V().out().out().values("name").next())

    g.getGraph.compute()
      .result(GraphComputer.ResultGraph.NEW)
      .persist(GraphComputer.Persist.EDGES)
      .program(TraversalVertexProgram.build().traversal(g, "gremlin-groovy", "g.V()").create())
      .submit().get()

  val rddName = Constants.getGraphLocation(g.getGraph.configuration().getString(Constants.GREMLIN_HADOOP_OUTPUT_LOCATION))

  val rdd = Spark.getRDD(rddName)

  val rdds = Spark.getRDDs

  println("rdds: " + rdds)

  //rdds foreach { rdd =>
  rdd foreach {
    case (id, writable: VertexWritable) =>
      println(writable.get().valueMap())
      println(writable.get().edges(Direction.OUT).asScala.toList)
  }
  //}
}
