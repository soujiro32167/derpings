package ca.eli

import gremlin.scala.Graph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory

object GremlinApp extends App {
  val graph: Graph = GraphFactory.open("/app/classes/hadoop-gryo-cluster-submit.properties")
  val g: GraphTraversalSource = graph.traversal().withComputer(classOf[SparkGraphComputer])

  println(g.V().count().next())
  println(g.V().out().out().values("name").next())
}
