package org.codefeedr.pypi

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
      .setBufferProperty("message.max.bytes", "5000000") // max message size is 5mb
      .setBufferProperty("max.request.size", "5000000") // max message size is 5 mb
      .edge(releaseSource, enrichReleases)
      .build()
      .start(args)
  }

}
