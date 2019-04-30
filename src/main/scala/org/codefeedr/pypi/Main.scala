package org.codefeedr.pypi

import org.codefeedr.pipeline.PipelineBuilder

object Main {

  def main(args: Array[String]): Unit = {
    val releaseSource = new PyPiReleasesStage()
    val enrichReleases = new PyPiReleasesExtStage()

    new PipelineBuilder()
      .setBufferProperty("message.max.bytes", "5000000") // max message size is 5mb
      .setBufferProperty("max.request.size", "5000000") // max message size is 5 mb
      .edge(releaseSource, enrichReleases)
      .start(args)
  }

}
