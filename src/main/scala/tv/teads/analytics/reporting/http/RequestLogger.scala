package tv.teads.analytics.reporting.http

import cats.data.Kleisli
import org.http4s.HttpApp
import tv.teads.analytics.reporting.{logger, AppTask}

object RequestLogger {
  def apply(service: HttpApp[AppTask]): HttpApp[AppTask] =
    Kleisli { request =>
      for {
        _        <- logger.info(s"Request ${request.method} ${request.uri}")
        response <- service(request)
        _        <- logger.info(s"Response ${response.status}")
      } yield response
    }
}
