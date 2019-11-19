package tv.teads.analytics.reporting.http

import cats.data.Kleisli
import org.http4s.HttpApp
import org.http4s.dsl.Http4sDsl
import tv.teads.analytics.reporting.{logger, AppTask}
import zio.interop.catz._

object RequestLogger extends Http4sDsl[AppTask] {
  def apply(service: HttpApp[AppTask]): HttpApp[AppTask] =
    Kleisli { request =>
      for {
        _ <- logger.info(s"Request ${request.method} ${request.uri}")
        response <- service(request)
                     .onError(error => logger.error(error.prettyPrint))
                     .orElse(InternalServerError("Internal server error LOL"))
        _ <- logger.info(s"Response ${response.status}")
      } yield response
    }
}
