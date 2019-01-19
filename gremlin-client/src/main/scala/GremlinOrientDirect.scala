import org.apache.tinkerpop.gremlin.orientdb.{OrientGraph, OrientGraphFactory}
import resource._

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}

object GremlinOrientDirect extends App{

  val result = //managed(OrientGraph.open(conf))
    managed(OrientGraph.open("remote:localhost:31558/infinityDS"))
    .map { graph =>
      val g = graph.traversal
      g.V().valueMap(true).asScala.toList
  }



  result.tried match {
    case Success(value) => println(value)
    case Failure(ex) => throw ex
  }
}
