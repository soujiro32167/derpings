import java.io.File
import java.util.Properties

import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions, ConfigResolveOptions}
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest._

import scala.collection.JavaConverters._

trait ExternalConfig {
  val externalParameters = Map(
    "externalKey" -> "externalValue"
  )

  val externalConfig = ConfigFactory.parseMap(externalParameters.asJava)
}

class TestTypesafe extends FlatSpec with Matchers {
  "Typesafe" should "load a file with reference fallback" in {
    //val c = ConfigFactory.load("./src/main/resources/custom.conf")
    val c = ConfigFactory.load(ConfigFactory.parseFile(new File("src/main/resources/custom.conf")))
    c.getInt("woot.b") should be (2)
    c.getInt("woot.a") should be (1)
  }

  it should "use properties from a map for resolution: 1" in new ExternalConfig {
    val config = ConfigFactory.load("outside_property",
      ConfigParseOptions.defaults(),
      ConfigResolveOptions.defaults().setAllowUnresolved(true))
          .resolveWith(externalConfig)

    //println(config.getConfig("woot"))

    config.getInt("woot.a") should be (1)
    config.getString("external") should be ("externalValue")
  }

  it should "use properties from a map for resolution: 2" in new ExternalConfig {
    val config = externalConfig.withFallback(ConfigFactory.load())

    println(config.getConfig("woot"))

    config.getInt("woot.a") should be (1)
    config.getConfigList("woot.array").asScala.head.getString("external") should be ("externalValue")
    //config.getString("woot.array[0].external") should be ("externalValue")
  }

  it should "use properties from a map for resolution: 3" in new ExternalConfig {
    val config = ConfigFactory.load().withFallback(externalConfig)

    println(config.getConfig("woot"))

    config.getInt("woot.a") should be (1)
    config.getConfigList("woot.array").asScala.head.getString("external") should be ("externalValue")
  }

  it should "use properties from a map for resolution: 4" in new ExternalConfig {
    val config = ConfigFactory.parseResources("reference.conf")
      .resolveWith(externalConfig)

    println(config.getConfig("woot"))

    config.getInt("woot.a") should be (1)
    config.getConfigList("woot.array").asScala.head.getString("external") should be ("externalValue")
  }

  it should "use properties from a map for resolution: 5" in new ExternalConfig {
    val p = new Properties()
    p.putAll(externalParameters.asJava)
    System.setProperties(p)

    System.getProperty("externalKey") should be ("externalValue")

    ConfigFactory.invalidateCaches()

    val config = ConfigFactory.load()

    println(config.getConfig("woot"))

    config.getInt("woot.a") should be (1)
    config.getConfigList("woot.array").asScala.head.getString("external") should be ("externalValue")
  }
}
