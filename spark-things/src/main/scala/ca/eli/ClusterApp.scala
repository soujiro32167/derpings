package ca.eli

import com.softwaremill.sttp._


object ClusterApp extends App{
  println(System.getProperty("quoted"))
  sttp
    .body(System.getProperty("quoted"))
    .post(uri"http://host.docker.internal:3000/spark")
}
