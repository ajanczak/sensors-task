package epam.sensors

package models {

  sealed trait MeasureInput {
    val sensorId: String
  }

  case class InvalidMeasure(sensorId: String, value: Int) extends MeasureInput
  case class ValidMeasure(sensorId: String, value: Int) extends MeasureInput

  case class Reduced(sensorId: String,
                     min: Int,
                     max: Int,
                     sum: Int,
                     amount: Int)
  case class ReducedInvalid(sensorId: String, amount: Int)

}
