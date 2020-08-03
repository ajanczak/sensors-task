package epam.sensors

import epam.sensors.models.{Reduced, ReducedInvalid}

object DataInterpreter {

  // TODO Interpret and providing input should be detached.
  def interpret(files: Int,
                reduced: Seq[Reduced],
                invalids: Seq[ReducedInvalid]): String = {

    val totalValid = reduced.map(_.amount).sum
    val totalInvalid = invalids.map(_.amount).sum

    val avgs = reduced.map(x => x.sum / x.amount)
    val withAvgs = reduced.zip(avgs).sortBy(_._2).reverse

    val missingIds = (invalids.map(_.sensorId)) diff reduced.map(_.sensorId)

    s"""
       |
       |Num of processed files: $files
       |Num of processed measurements: $totalValid
       |Num of failed measurements: $totalInvalid
       |
       |Sensors with highest avg humidity:
       |
       |sensor-id,min,avg,max
       |${withAvgs
         .map {
           case (reduced, avg) =>
             f"${reduced.sensorId},${reduced.min},$avg,${reduced.max}"
         }
         .mkString("\n")}
       |${missingIds.map(v => f"$v,Nan,Nan,Nan").mkString("\n")}
      """.stripMargin
  }
}
