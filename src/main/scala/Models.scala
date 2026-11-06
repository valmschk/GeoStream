import io.circe.Encoder
import java.util.concurrent.ThreadLocalRandom

final case class GpsPoint(
    courierId: String,
    lat: Double,
    lon: Double,
    timestamp: Long
)

object GpsPoint:
  given Encoder[GpsPoint] = Encoder.forProduct4(
    "courierId", "lat", "lon", "timestamp"
  )(p => (p.courierId, p.lat, p.lon, p.timestamp))

  def random(courierId: String): GpsPoint =
    val rnd = ThreadLocalRandom.current()
    GpsPoint(
      courierId = courierId,
      lat = 55.75 + (rnd.nextDouble() - 0.5) * 0.1,
      lon = 37.61 + (rnd.nextDouble() - 0.5) * 0.1,
      timestamp = System.currentTimeMillis()
    )