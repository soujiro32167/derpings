import com.typesafe.config.{ConfigFactory, ConfigParseOptions, ConfigResolveOptions}

val raw = raw"a\nb"


val quoteMatcher = "['\"]?(.*?)['\"]?".r

val noGroupMatcher = "['\"]?.*?['\"]?".r


"\"quoted\"" match {
  case quoteMatcher(quoted) => quoted
  case _ => false
}


"'quoted'" match {
  case quoteMatcher(quoted) => quoted
  case _ => false
}

"unquoted" match {
  case quoteMatcher(quoted) => quoted
  case _ => false
}

quoteMatcher unapplySeq "'somestring'" foreach println

noGroupMatcher.findFirstIn("\"someother\"")

"asd=zxc".split("=") match {
  case Array(key, value) => (key, value)
}

"asdzxc".split("=") match {
  case Array(key, value) => (key, value)
  case Array(any) => s"no match for $any"
}

val tuple = (1, "b")

Map[Int, String](0 -> "a") + tuple

"key".split('=') match {
  case Array(key, value) => (key, value)
  case other => other
}

import scala.collection.JavaConverters._

