package epam.sensors

import java.io.File

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.csv.scaladsl.CsvParsing
import akka.stream.scaladsl._
import epam.sensors.models._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

  val pathStr = args.headOption.getOrElse(
    throw new IllegalArgumentException(
      "Please provide path to directory with files"
    )
  )

  val files: Seq[File] = (new File(pathStr)).listFiles
    .filter(_.isFile)
    .toSeq
    .filter(_.getName.endsWith(".csv")) //isFile to find files

  new SensorDataProcessor(files).run
    .map {
      case (validReduced, invalids) =>
        val output =
          DataInterpreter.interpret(files.size, validReduced, invalids)
        println(output)
        sys.exit()
    }
    .recover {
      case err => throw err
    }

}

class SensorDataProcessor(files: Seq[File]) {

  implicit val as = ActorSystem()
  implicit val mat = Materializer.apply(as)

  val measureInputSource = MeasuresSource.fromFiles(files)

  val reducesResultsF = measureInputSource
    .collectType[ValidMeasure]
    .via(MeasuresFlow.forValid)
    .toMat(Sink.seq)(Keep.right)
    .run()

  val invalidResults: Future[Seq[ReducedInvalid]] =
    measureInputSource
      .collectType[InvalidMeasure]
      .via(MeasuresFlow.forInvalid)
      .toMat(Sink.seq)(Keep.right)
      .run()

  def run: Future[(Seq[Reduced], Seq[ReducedInvalid])] =
    reducesResultsF zip invalidResults
}
