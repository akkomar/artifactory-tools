import play.api.libs.json
import play.api.libs.json.Json
import com.typesafe.config.ConfigFactory

object Main extends App {

  import java.time.{ZoneOffset, LocalDateTime, LocalDate}
  import scalaj.http._
  val date = LocalDate.of(2015,6,28).atStartOfDay().toEpochSecond(ZoneOffset.ofTotalSeconds(0)) * 1000

  val conf = ConfigFactory.load()


  private val artifactoryUrl: String = conf.getString("artifactory.apiUrl")


  private val apiUser: String = conf.getString("artifactory.apiUser")
  private val apiPassword: String = conf.getString("artifactory.apiPassword")
  val response: HttpResponse[String] = Http(s"$artifactoryUrl/api/search/creation?from=1&to=$date&repos=ext-snapshot-local").option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(5000)).auth(apiUser,apiPassword).asString
//  response.body
//  response.code
//  response.headers

  val artifactUrls = (Json.parse(response.body) \\ "uri").map(_.as[String])

//  println(artifactUrls)
  println(artifactUrls.size)

  artifactUrls.foreach{artifactUrl=>
    val artifact = artifactUrl.split("storage").last
    val res = Http(s"$artifactoryUrl/$artifact").option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(5000)).method("DELETE").auth(apiUser,apiPassword).asString
    println(res)
  }
}
