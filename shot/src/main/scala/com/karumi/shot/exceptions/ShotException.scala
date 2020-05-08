package com.karumi.shot.exceptions

case class ShotException(private val message: String,
                         private val cause: Throwable = None.orNull)
    extends RuntimeException(message, cause)
