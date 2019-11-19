package tv.teads.analytics.reporting.logger

import com.typesafe.scalalogging.{Logger => SLogger}
import zio.{UIO, URIO, ZIO}

trait Logger {
  val logger: Logger.Service[LoggingContextEnv]
}

object Logger {
  trait Service[-R] {
    def debug(message: => String): URIO[R, Unit]
    def info(message: => String): URIO[R, Unit]
    def warn(message: => String): URIO[R, Unit]
    def error(message: => String): URIO[R, Unit]
    def error(message: => String, throwable: => Throwable): URIO[R, Unit]
  }

  trait Live extends Logger {
    override val logger: Service[LoggingContextEnv] = new Service[LoggingContextEnv] {
      val l = SLogger.takingImplicit[LoggingContext]("reporting")

      private def withContext(f: LoggingContext => UIO[Unit]): URIO[LoggingContextEnv, Unit] =
        ZIO.environment[LoggingContextEnv].map(_.loggingContext).flatMap(f)

      override def debug(message: => String): URIO[LoggingContextEnv, Unit] =
        withContext { implicit c =>
          URIO.effectTotal(l.debug(s"$message"))
        }

      override def info(message: => String): URIO[LoggingContextEnv, Unit] =
        withContext { implicit c =>
          URIO.effectTotal(l.info(s"$message"))
        }

      override def warn(message: => String): URIO[LoggingContextEnv, Unit] =
        withContext { implicit c =>
          URIO.effectTotal(l.warn(s"$message"))
        }

      override def error(message: => String): URIO[LoggingContextEnv, Unit] =
        withContext { implicit c =>
          URIO.effectTotal(l.error(s"$message"))
        }

      override def error(message: => String, throwable: => Throwable): URIO[LoggingContextEnv, Unit] =
        withContext { implicit c =>
          URIO.effectTotal(l.error(s"$message", throwable))
        }
    }
  }
}
