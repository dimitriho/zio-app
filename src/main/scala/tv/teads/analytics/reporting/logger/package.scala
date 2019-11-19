package tv.teads.analytics.reporting

import zio.ZIO

package object logger {
  def debug(message: => String): ZIO[Logger with LoggingContextEnv, Nothing, Unit] =
    ZIO.environment[Logger].flatMap(_.logger.debug(message))

  def info(message: => String): ZIO[Logger with LoggingContextEnv, Nothing, Unit] =
    ZIO.environment[Logger].flatMap(_.logger.info(message))

  def warn(message: => String): ZIO[Logger with LoggingContextEnv, Nothing, Unit] =
    ZIO.environment[Logger].flatMap(_.logger.warn(message))

  def error(message: => String): ZIO[Logger with LoggingContextEnv, Nothing, Unit] =
    ZIO.environment[Logger].flatMap(_.logger.error(message))

  def error(message: => String, throwable: => Throwable): ZIO[Logger with LoggingContextEnv, Nothing, Unit] =
    ZIO.environment[Logger].flatMap(_.logger.error(message, throwable))
}
