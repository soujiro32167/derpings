name := "gremlin-client"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.3.4.14",
  "org.apache.tinkerpop" % "gremlin-driver" % "3.3.5",
  "com.orientechnologies" % "orientdb-gremlin" % "3.0.10",
  "com.jsuereth" %% "scala-arm" % "2.0",
  "org.apache.tinkerpop" % "tinkergraph-gremlin" % "3.3.5",
  "org.slf4j" % "slf4j-simple" % "1.7.25",
  "com.typesafe" % "config" % "1.3.3"
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

