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
      .enableCheckpointing(1000) // checkpointing every 1000ms
      .setBufferProperty(KafkaBuffer.COMPRESSION_TYPE, "gzip")
      .setBufferProperty(KafkaBuffer.BROKER, "localhost:29092")
      .setBufferProperty(KafkaBuffer.ZOOKEEPER, "localhost:2181")
      .setBufferProperty("message.max.bytes", "5000000") // max message size is 5mb
      .setBufferProperty("max.request.size", "5000000") // max message size is 5 mb
      .edge(releaseSource, enrichReleases)
      .build()
      .start(args)
  }

}
