import cats.effect.IO
import fs2.Pipe
import scala.concurrent.duration.*

object Pipeline:
  def process(repo: GpsRepository): Pipe[IO, GpsPoint, Unit] =
    _.filter(p => p.lat != 0.0 && p.lon != 0.0)
      .groupWithin(100, 2.seconds)
      .evalMap { chunk =>
        val points = chunk.toList
        if points.nonEmpty then
          repo.batchInsert(points).handleErrorWith { err =>
            IO.println(s"[PIPELINE ERROR] Не удалось сохранить точки: ${err.getMessage}") *>
            IO.raiseError(err) 
          }
        else IO.unit
      }