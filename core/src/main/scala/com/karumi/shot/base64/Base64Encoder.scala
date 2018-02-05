package com.karumi.shot.base64

import java.io.{ByteArrayOutputStream, File}
import java.util.Base64
import javax.imageio.ImageIO

import org.apache.commons.io.Charsets

object Base64Encoder {

  def base64FromFile(filePath: String): String = {
    val diffScreenshotFile = new File(filePath)
    val bufferedImage = ImageIO.read(diffScreenshotFile)
    val outputStream = new ByteArrayOutputStream()
    ImageIO.write(bufferedImage, "png", outputStream)
    val diffImageBase64Encoded =
      Base64.getEncoder.encode(outputStream.toByteArray)
    val diffBase64UTF8 = new String(diffImageBase64Encoded, Charsets.UTF_8)
    diffBase64UTF8
  }

}
