import cats.effect.IO
import fs2.Stream
import scala.concurrent.duration.*
import scala.util.Random

object StreamSource:
  private val couriers = List("courier-1", "courier-2", "courier-3")

  def incomingGpsStream: Stream[IO, GpsPoint] =
    Stream
      .awakeEvery[IO](500.millis)
      .map { _ =>
        GpsPoint.random(couriers(Random.nextInt(couriers.length)))
      }