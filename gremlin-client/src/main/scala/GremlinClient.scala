import resource._
import gremlin.scala._
import org.apache.tinkerpop.gremlin.driver.Client.ClusteredClient
import org.apache.tinkerpop.gremlin.driver.{Client, Cluster}

import collection.JavaConverters._

object GremlinClient extends App {

  for {
    cluster <- managed(Cluster.build()
      .addContactPoint("localhost")
       // .addContactPoint("docker-for-desktop")
        //.addContactPoint("192.168.65.3")
        .port(31082)
      .create())
    client <- managed(cluster.connect[ClusteredClient]())
  } {

    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "Trace")

    val results =
      //client.submit("g1.V().project('attributes', 'label').by(valueMap()).by(label)")
      client.submit("g1.V().label()")
      //.all().get()
      .some(1).get()

    println(results.asScala)
  }
}
