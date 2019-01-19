import gremlin.scala.{Graph, ScalaGraph, TraversalSource}
import org.apache.tinkerpop.gremlin.driver.Client.ClusteredClient
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph.EmptyGraphFactory
import resource._
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import scala.collection.JavaConverters._


object GremlinScalaToString extends App {
  val graph: Graph = EmptyGraphFactory.open(null)
  val g = graph.traversal()

  val result = g.V().valueMap()

  println(g.V().valueMap())
}
