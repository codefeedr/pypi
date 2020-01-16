package org.codefeedr.pypi

import java.util.concurrent.TimeUnit

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.time.Time
import org.codefeedr.buffer.KafkaBuffer
import org.codefeedr.pipeline.PipelineBuilder
import org.codefeedr.plugins.pypi.stages.{
  PyPiReleaseExtStage,
  PyPiReleasesStage
}

object Main {

  def main(args: Array[String]): Unit = {
    val releaseSource = new PyPiReleasesStage()
    val enrichReleases = new PyPiReleaseExtStage()

    new PipelineBuilder()
      .setPipelineName("PyPi plugin")
      .setRestartStrategy(RestartStrategies.fixedDelayRestart(
        3,
        Time.of(10, TimeUnit.SECONDS))) // try restarting 3 times
      .enableCheckpointing(5000) // checkpointing every 5000ms
      .setBufferProperty(KafkaBuffer.COMPRESSION_TYPE, "gzip")
      .setBufferProperty(KafkaBuffer.AMOUNT_OF_PARTITIONS, "8")
      .setBufferProperty(KafkaBuffer.AMOUNT_OF_REPLICAS, "2")
      .setBufferProperty(KafkaBuffer.BROKER, "kafka-0.kafka-headless.codefeedr:9092,kafka-1.kafka-headless.codefeedr:9092,kafka-2.kafka-headless.codefeedr:9092")
      .setBufferProperty(KafkaBuffer.ZOOKEEPER, "zookeeper-0.zookeeper-headless.codefeedr:2181,zookeeper-1.zookeeper-headless.codefeedr:2181,zookeeper-2.zookeeper-headless.codefeedr:2181")
      .setBufferProperty("message.max.bytes", "10485760") // max message size is 10mb
      .setBufferProperty("max.request.size", "10485760") // max message size is 10 mb
      .edge(releaseSource, enrichReleases)
      .build()
      .start(args)
  }

}
