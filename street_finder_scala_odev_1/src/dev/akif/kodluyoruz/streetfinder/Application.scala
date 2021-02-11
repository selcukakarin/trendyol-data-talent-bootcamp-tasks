package dev.akif.kodluyoruz.streetfinder
import java.io.File
import scala.io.Source
/**
 * See CSV file at: https://github.com/makiftutuncu/kodluyoruz-scala/blob/main/data/streets.csv
 *
 * Original data: https://github.com/life/il-ilce-mahalle-sokak-cadde-sql
 */

object CsvLoaderI extends CsvLoader {
  def loadCsv(file: File): List[String] ={
    val scalaFileContents = Source.fromFile(file)
    var streetList :   List[String] = {
      scalaFileContents.getLines()
        .map(_.split(",")
          .map(_.trim).map(_.replaceAll("[']","")).apply(1)).toList

    }
    scalaFileContents.close
    streetList
  }
}

object StreetFinderI {
  def streetFinder(streets: List[String], names: Set[String]): scala.collection.mutable.Map[String, Int]={
    val myMuteMap = scala.collection.mutable.Map[String,Int]()
    val mappedSt = StreetFinderI.group_all_map(streets)
    for(name<-names){
      if(mappedSt.contains(s"${name}")) {
         myMuteMap += (s"${name}" -> mappedSt(s"${name}"))
      }
    }
    myMuteMap
  }
  def group_all_map[A](list1:List[A]):Map[A, Int]= {
    val groupedSt = list1.groupBy(el => el).map(e => (e._1, e._2.length)).toMap
    groupedSt
  }
}



object Application {
  def main(args: Array[String]): Unit = {
    val file_ = new File("data/streets.csv" )
    val StList:List[String]=CsvLoaderI.loadCsv(file_)
    val searchValues = Set("ATATÜRK CD.","ATATÜRK SK.","sd")
    ///searchNames--->scala.io.StdIn.readLine() can get from the user
    print(StreetFinderI.streetFinder(StList,searchValues))
  }
}
