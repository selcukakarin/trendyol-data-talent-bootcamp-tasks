name := "bootcamp-flink"
version := "0.1"
scalaVersion := "2.12.13"

val flinkVersion = "1.12.1"

libraryDependencies ++= Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % Provided,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % Provided
)

assemblyMergeStrategy in assembly := {
  case "BUILD" => MergeStrategy.discard
  case "META-INF/io.netty.versions.properties" => MergeStrategy.last
  case other => MergeStrategy.defaultMergeStrategy(other)
}
