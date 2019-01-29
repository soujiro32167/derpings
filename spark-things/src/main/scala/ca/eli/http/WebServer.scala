package ca.eli.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object WebServer extends AkkaTrait {
  def main(args: Array[String]) {

    val bindingFuture = run()
    println(s"Server online at http://localhost:3000/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

  def run(host: String = "0.0.0.0", port: Int = 3000): Future[ServerBinding] = {

    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      } ~
      path("") {
        get {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "W00t"))
        }
      } ~
      path("spark") {
        post {
          entity(as[String]) { string =>
            println(s"spark post: $string");
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"spark post: $string"))
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, host, port)

    bindingFuture
  }
}
