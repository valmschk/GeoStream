import cats.effect.{IO, IOApp}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import com.comcast.ip4s.*

object Main extends IOApp.Simple:
  def run: IO[Unit] =
    PostgresGpsRepository.make.use { repo =>
      val httpServerResource = EmberServerBuilder
        .default[IO]
        .withHost(host"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(HttpApi.routes(repo).orNotFound)
        .build

      httpServerResource.use { server =>
        val pipeline: IO[Unit] =
          StreamSource.incomingGpsStream
            .through(Pipeline.process(repo))
            .compile
            .drain

        IO.println(
          s"HTTP server started at http://localhost:${server.address.getPort}"
        ) *>
          IO.println("Starting GeoStream-CE pipeline...") *>
          pipeline
      }
    }