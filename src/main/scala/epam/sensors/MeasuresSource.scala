package epam.sensors

import java.io.File

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.alpakka.csv.scaladsl.CsvParsing
import akka.stream.scaladsl.{BroadcastHub, FileIO, Keep, Merge, Source}
import epam.sensors.models.{InvalidMeasure, MeasureInput, ValidMeasure}

object MeasuresSource {
  def fromFiles(
    files: Seq[File]
  )(implicit mat: Materializer): Source[MeasureInput, NotUsed] = {
    files
      .map(
        file =>
          FileIO
            .fromPath(file.toPath)
            .via(CsvParsing.lineScanner(CsvParsing.Comma))
            .drop(1)
            .map(_.map(_.utf8String))
            .collect {
              _ match {
                case sensorId :: "NaN" :: Nil => InvalidMeasure(sensorId, -1)
                case sensorId :: value :: Nil =>
                  ValidMeasure(sensorId, value.toInt)
              }
          }
      )
      .foldLeft(Source.empty[MeasureInput]) {
        case (x1, x2) => Source.combine(x1, x2)(Merge(_))
      }
      .toMat(BroadcastHub.sink(256))(Keep.right)
      .run()
  }
}
