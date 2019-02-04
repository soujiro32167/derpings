import java.util

import org.apache.tinkerpop.gremlin.structure.VertexProperty

//import org.apache.tinkerpop.gremlin.structure.{Edge, Property}
import resource._
import gremlin.scala._
import org.apache.tinkerpop.gremlin.driver.Client.ClusteredClient
import org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0
import org.apache.tinkerpop.gremlin.driver.{Client, Cluster, Result}
import org.apache.tinkerpop.gremlin.orientdb.io.OrientIoRegistry
//import org.apache.tinkerpop.gremlin.structure.{Vertex, VertexProperty}
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoMapper

import collection.JavaConverters._

object GremlinClient extends App {

  for {
    cluster <- managed(Cluster.build()
      .addContactPoint("localhost")
       // .addContactPoint("docker-for-desktop")
        //.addContactPoint("192.168.65.3")
        .port(8182)
        .serializer(new GryoMessageSerializerV3d0(GryoMapper.build().addRegistry(OrientIoRegistry.instance())))
      .create())
    client <- managed(cluster.connect[ClusteredClient]())
  } {

    //System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "Trace")

//    val results: util.List[Result] =
//      //client.submit("g1.V().project('attributes', 'label').by(valueMap()).by(label)")
//      client.submit("gt1.V().union(gt1.V(), gt1.E())")
//      .all().get()
//
//    results.asScala.map( _.getElement ).foreach {
//      case v: Vertex =>
//        println(s"vertex: $v")
//        val properties: Map[String, Any] = v.properties[Any]().asScala.map {
//          p: VertexProperty[Any] => (p.key(), p.value() )
//        }.toMap
//        println(s"properties: $properties")
//      case e: Edge =>
//        println(s"edge: $e")
//        val properties: Map[String, Any] = e.properties[Any]().asScala.map {
//          p: Property[Any] => (p.key(), p.value())
//        }.toMap
//        println(s"properties: $properties")
//    }

//      val results: Iterator[Result] =
//      //gt1.V().group().by(label).project('vertex', 'relations').by().by(bothE().fold())
//        client.submit("gt1.V().group().by(label).by(project('vertex').by(fold()))")
//        .iterator().asScala

    //results.foreach(println)

    val all = client.submit("gt1.V().project('vertex', 'relations').by().by(bothE().fold())").iterator().asScala

    //all.foreach(println)

    type VertexAndRelations = (Vertex, Seq[Edge])

    all.foreach{ result =>
      println(result)
      val map = result.get(classOf[util.Map[String, Any]])
      val vertexAndRelations: VertexAndRelations = (map.get("vertex").asInstanceOf[Vertex], map.get("relations").asInstanceOf[util.List[Edge]].asScala)
      println("vertex: " + vertexAndRelations._1.label())
      println("properties: " + vertexAndRelations._1.properties().asScala.toList)
      println("edges: " + vertexAndRelations._2.map(e => e.property[String]("relationLabel").value()))
    }

    //println(results)
  }
}
