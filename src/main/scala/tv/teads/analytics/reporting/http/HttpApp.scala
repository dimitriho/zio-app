package tv.teads.analytics.reporting
package http

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._

object HttpApp extends Http4sDsl[AppTask] {
  val routes: HttpRoutes[AppTask] =
    HttpRoutes
      .of[AppTask] {
        case GET -> Root / "hello" / name =>
          for {
            list <- repository.fetchIntList
            _    <- logger.info(s"Said hello to $name - $list")
            res  <- Ok(s"Hello, $name! $list")
          } yield res
      }
}
