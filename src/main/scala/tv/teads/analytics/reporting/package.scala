package tv.teads.analytics

import tv.teads.analytics.reporting.logger.{Logger, LoggingContextEnv}
import tv.teads.analytics.reporting.repository.{DoobieTodoRepository, DoobieTodoRepositoryEnv}
import zio.ZIO
import zio.clock.Clock

package object reporting {
  type AppEnv      = Clock with Logger with LoggingContextEnv with DoobieTodoRepositoryEnv
  type AppTask[+A] = ZIO[AppEnv, Throwable, A]
}

object AppEnv {
  class Live(val db: DoobieTodoRepository)
      extends Clock.Live
      with Logger.Live
      with LoggingContextEnv.Live
      with DoobieTodoRepositoryEnv
}
