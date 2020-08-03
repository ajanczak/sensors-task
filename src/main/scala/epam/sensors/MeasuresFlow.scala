package epam.sensors

import akka.NotUsed
import akka.stream.scaladsl.Flow
import epam.sensors.models.{
  InvalidMeasure,
  Reduced,
  ReducedInvalid,
  ValidMeasure
}

object MeasuresFlow {

  def forValid: Flow[ValidMeasure, Reduced, NotUsed] =
    Flow[ValidMeasure]
      .groupBy(Int.MaxValue, _.sensorId)
      .map { measureInput =>
        import measureInput._
        Reduced(sensorId, value, value, value, 1)
      }
      .reduce { (r1, r2) =>
        Reduced(
          sensorId = r1.sensorId,
          min = Math.min(r1.min, r2.min),
          max = Math.max(r1.max, r2.max),
          sum = r1.sum + r2.sum,
          amount = r1.amount + r2.amount
        )
      }
      .mergeSubstreams

  def forInvalid: Flow[InvalidMeasure, ReducedInvalid, NotUsed] = {
    Flow[InvalidMeasure]
      .groupBy(Int.MaxValue, _.sensorId)
      .map(invalid => ReducedInvalid(invalid.sensorId, 1))
      .reduce { (i1, i2) =>
        ReducedInvalid(i1.sensorId, i1.amount + i2.amount)
      }
      .mergeSubstreams
  }
}
