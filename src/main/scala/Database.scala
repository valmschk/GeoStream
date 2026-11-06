import cats.effect.{IO, Resource, Ref}
import cats.syntax.all.*

trait GpsRepository:
  def batchInsert(points: List[GpsPoint]): IO[Unit]
  def getLastPoint(courierId: String): IO[Option[GpsPoint]]

object GpsRepository:
  def make: Resource[IO, GpsRepository] =
    Resource.make {
      for
        storeRef <- Ref.of[IO, Map[String, GpsPoint]](Map.empty)
        _ <- IO.println(
          "[DB] Открытие пула соединений и инициализация in-memory хранилища..."
        )
      yield new GpsRepository:

        override def batchInsert(points: List[GpsPoint]): IO[Unit] =
          for
            _ <- IO.println(
              s"[DB] Сохранение ${points.size} GPS-точек в память..."
            )
            _ <- storeRef.update { currentStore =>
              points
                .sortBy(_.timestamp)
                .foldLeft(currentStore) { (acc, point) =>
                  acc.updated(point.courierId, point)
                }
            }
          yield ()

        override def getLastPoint(courierId: String): IO[Option[GpsPoint]] =
          storeRef.get.map(_.get(courierId))
    } { _ =>
      IO.println("[DB] Закрытие пула соединений и освобождение ресурсов...")
    }