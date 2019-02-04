package sparkLauncher

import java.io.{BufferedReader, InputStreamReader}
import java.lang.ProcessBuilder.Redirect

import org.apache.spark.launcher.{SparkAppHandle, SparkLauncher}
import org.scalatest.{FlatSpec, Matchers}

class SparkLauncherTest extends FlatSpec with Matchers {

  behavior of "SparkLauncher"

  it should "pass system properties to driver" in {
    val command = new SparkLauncher()
      .setAppResource("/app/app.jar")
      .setMainClass("ca.eli.ClusterApp")
      .setDeployMode("cluster")
      .setMaster("spark://localhost:6066")
      .setConf("spark.driver.extraJavaOptions", "-Dquoted=\"'in' unquoted 'single quotes'\"")
      //.redirectOutput(ProcessBuilder.Redirect.INHERIT)
      //.redirectToLog(classOf[SparkLauncherTest].getName)
      .startApplication(new SparkAppHandle.Listener {
      override def stateChanged(handle: SparkAppHandle): Unit = {
        println("state changed")
        println("app id: " + handle.getAppId)
        println("state: " + handle.getState)
      }

      override def infoChanged(handle: SparkAppHandle): Unit = {
        println("info changed")
        println("app id: " + handle.getAppId)
        println("state: " + handle.getState)
      }
    })

    Thread.sleep(10000)
  }

  it should "launch a gremlin app" in {
    val command = new SparkLauncher()
      .setAppResource("/app/app.jar")
      .setMainClass("ca.eli.GremlinApp")
      .setDeployMode("cluster")
      .setMaster("spark://localhost:6066")
      .startApplication(new SparkAppHandle.Listener {
      override def stateChanged(handle: SparkAppHandle): Unit = {
        println("state changed")
        println("app id: " + handle.getAppId)
        println("state: " + handle.getState)
      }

      override def infoChanged(handle: SparkAppHandle): Unit = {
        println("info changed")
        println("app id: " + handle.getAppId)
        println("state: " + handle.getState)
      }
    })

    Thread.sleep(10000)
  }
}
