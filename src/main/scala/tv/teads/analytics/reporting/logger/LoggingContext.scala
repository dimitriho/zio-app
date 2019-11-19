package tv.teads.analytics.reporting.logger

import com.typesafe.scalalogging.CanLog

sealed trait LoggingContext
object LoggingContext {
  case class RequestId(underlying: String) extends LoggingContext
  case object System                       extends LoggingContext

  implicit val canLog: CanLog[LoggingContext] = {
    case (originalMsg, LoggingContext.RequestId(id)) => s"[$id] $originalMsg"
    case (originalMsg, LoggingContext.System)        => originalMsg
  }
}

trait LoggingContextEnv {
  val loggingContext: LoggingContext
}

object LoggingContextEnv {
  trait Live extends LoggingContextEnv {
    override val loggingContext: LoggingContext = LoggingContext.System
  }
}
