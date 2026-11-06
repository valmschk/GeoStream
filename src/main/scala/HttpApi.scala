import cats.effect.IO
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.CirceEntityCodec.*
import io.circe.Json

object HttpApi:
  def routes(repo: GpsRepository): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "couriers" / courierId / "last-point" =>
      repo.getLastPoint(courierId).flatMap {
        case Some(point) =>
          Ok(point)
        case None =>
          NotFound(
            Json.obj(
              "error" -> Json.fromString(
                s"Курьер $courierId не найден или еще не отправлял точки"
              )
            )
          )
      }
  }