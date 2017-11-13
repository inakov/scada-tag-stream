package ocado.scada.tag.stream

import java.util.Properties

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.kstream._
import org.apache.kafka.streams.state.{QueryableStoreTypes, ReadOnlyKeyValueStore}
import model._
import spray.json._


object Application extends App {
  val bootstrapServers = "localhost:9092"
  val builder = new KStreamBuilder

  val streamingConfig = {
    val settings = new Properties
    settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream")
    settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    settings.put(StreamsConfig.APPLICATION_SERVER_CONFIG, "localhost:8081")
    // Specify default (de)serializers for record keys and for record values.
    settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass.getName)
    settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass.getName)
    settings
  }

  val storeName = "tag-aggregation-store"

  val blocksAggregation: KTable[String, String] = builder.stream(Serdes.String(), Serdes.String(), "tag_updates")
    .mapValues[String](update => {
        val tagUpdate = JsonParser(update).convertTo[TagUpdate]
        Map[String, TagUpdate](tagUpdate.tagClass -> tagUpdate).toJson.toString
      }).groupByKey().aggregate(() => "{}",
      (aggKey: String, value1: String, value2: String) => {
        val newMap = JsonParser(value1).convertTo[Map[String, TagUpdate]]
        val aggMap = JsonParser(value2).convertTo[Map[String, TagUpdate]]
        (aggMap ++ newMap).toJson.toString
      }, Serdes.String, storeName)

//  val blockDefinitions: KTable[String, String] = builder.table("block_definitions")

  blocksAggregation.join(blocksAggregation, (value1: String, value2: String) => value2)

  val streams: KafkaStreams = new KafkaStreams(builder, streamingConfig)
  streams.start

  val metadataMonitor = new Thread(() => {
      while(true){
        Thread.sleep(1000)
        try{
          val keyValueStore: ReadOnlyKeyValueStore[String, String] = streams.store(storeName, QueryableStoreTypes.keyValueStore[String, String])
          println(s"Metadata from kafka: ${streams.allMetadata()}")
          print(s"Blocks in the local store[")
          val it = keyValueStore.all
          keyValueStore.get("test")
          var keyCount = 0
          while(it.hasNext){
            val nextBlock = it.next()
            if(keyCount < 10){
              println(s"${nextBlock.value},")
            }
            keyCount = keyCount + 1
          }
          it.close
          if(keyCount > 10) print(s"... ${keyCount - 10} more blocks.")
          println(s"]")
        }catch { case ex: Exception => println("Exception", ex)}
      }
  })

  metadataMonitor.run

  Runtime.getRuntime.addShutdownHook(new Thread(() => streams.close))
}
