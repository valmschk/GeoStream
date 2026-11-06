import cats.effect.testing.specs2.CatsEffect
import org.specs2.mutable.Specification

class RepositorySpec extends Specification with CatsEffect:
  "GpsRepository" should {
    "save and retrieve the last point for a courier" in {
      GpsRepository.make.use { repo =>
        val p1 = GpsPoint("c1", 55.0, 37.0, 1000L)
        val p2 = GpsPoint("c1", 56.0, 38.0, 2000L) 
        
        for
          _   <- repo.batchInsert(List(p1, p2))
          res <- repo.getLastPoint("c1")
        yield res must beSome(p2)
      }
    }
  }