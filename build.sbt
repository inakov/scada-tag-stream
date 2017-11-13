enablePlugins(JavaAppPackaging)

name := "scada-tag-stream"

version := "0.1"

scalaVersion := "2.12.4"

packageName in Docker := "ocado/scada-tag-stream"
dockerExposedPorts := Seq(8080)

resolvers += Resolver.bintrayRepo("ovotech", "maven")

libraryDependencies ++= {
  val akkaV = "2.5.6"
  val akkaHttp = "10.0.10"
  val kafkaV = "1.0.0"
  val kafkaSerializationV = "0.1.19"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttp,
    "com.typesafe.akka" %% "akka-stream-kafka" % "0.17",
    "org.apache.kafka"  %% "kafka" % kafkaV,
    "org.apache.kafka"  % "kafka-streams" % kafkaV,
    "com.ovoenergy" %% "kafka-serialization-core" % kafkaSerializationV,
    "com.ovoenergy" %% "kafka-serialization-spray" % kafkaSerializationV
  )
}
unmanagedResourceDirectories in Compile += {
  baseDirectory.value / "src/main/resources"
}
        