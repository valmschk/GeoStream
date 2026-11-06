import cats.effect.{IO, Resource}
import cats.syntax.all.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*
import natchez.Trace.Implicits.noop

object PostgresGpsRepository:
  def make: Resource[IO, GpsRepository] =
    Session.single[IO](
      host = "localhost",
      port = 5432,
      user = "postgres",
      password = Some("postgres"),
      database = "petcat"
    ).map { session =>
      new GpsRepository:

        private val gpsPointCodec: Codec[GpsPoint] =
          (varchar *: float8 *: float8 *: int8).imap {
            case (id, lat, lon, ts) => GpsPoint(id, lat, lon, ts)
          } { p =>
            (p.courierId, p.lat, p.lon, p.timestamp)
          }

        private val upsertQuery: Command[GpsPoint] =
          sql"""
            INSERT INTO gps_points (courier_id, lat, lon, timestamp)
            VALUES ($varchar, $float8, $float8, $int8)
            ON CONFLICT (courier_id) DO UPDATE SET
              lat = EXCLUDED.lat,
              lon = EXCLUDED.lon,
              timestamp = EXCLUDED.timestamp
          """.command.contramap[GpsPoint] { p =>
            (p.courierId, p.lat, p.lon, p.timestamp)
          }

        private val selectQuery: Query[String, GpsPoint] =
          sql"""
            SELECT courier_id::varchar, lat, lon, timestamp
            FROM gps_points
            WHERE courier_id = $varchar
          """.query(gpsPointCodec)

        override def batchInsert(points: List[GpsPoint]): IO[Unit] =
          session.prepare(upsertQuery).flatMap { prepared =>
            points.traverse_(p => prepared.execute(p))
          }

        override def getLastPoint(courierId: String): IO[Option[GpsPoint]] =
          session.prepare(selectQuery).flatMap { prepared =>
            prepared.option(courierId)
          }.handleErrorWith { err =>
            IO.println(s"[DB ERROR] getLastPoint($courierId): ${err.getMessage}") *>
            IO.println(s"[DB ERROR] ${err.toString}") *>
            IO.raiseError(err)
          }
    }