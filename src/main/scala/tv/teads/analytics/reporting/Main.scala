package tv.teads.analytics.reporting

import java.nio.ByteBuffer
import java.util.{Base64, UUID}

import cats.effect.ExitCode
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import tv.teads.analytics.AppEnv
import tv.teads.analytics.reporting.http.{HttpApp, RequestLogger}
import tv.teads.analytics.reporting.logger.{Logger, LoggingContext, LoggingContextEnv}
import tv.teads.analytics.reporting.repository.{
  DBConfig,
  DoobieRepository,
  DoobieTodoRepository,
  DoobieTodoRepositoryEnv,
}
import zio._
import zio.clock.Clock
import zio.console._
import zio.interop.catz._

object Main extends ManagedApp {
  def runHttp(port: Int): AppTask[Unit] =
    ZIO.runtime[AppEnv].flatMap { implicit rts =>
      BlazeServerBuilder[AppTask]
        .bindHttp(port, "0.0.0.0")
        .withHttpApp(RequestLogger(HttpApp.routes.orNotFound).mapF(provideContext))
        .serve
        .compile[AppTask, AppTask, ExitCode]
        .drain
    }

  private def uuidToString(uuid: UUID): String = {
    val bytes = ByteBuffer
      .allocate(java.lang.Long.BYTES * 2)
      .putLong(uuid.getMostSignificantBits)
      .putLong(uuid.getLeastSignificantBits)
      .array()

    Base64
      .getUrlEncoder
      .withoutPadding
      .encodeToString(bytes)
  }

  def provideContext[T](task: AppTask[T]): AppTask[T] =
    task.provideSomeM[AppEnv, Throwable](
      for {
        appEnv <- ZIO.environment[AppEnv]
        uuid   <- ZIO.effectTotal(UUID.randomUUID())
      } yield new Clock with Logger with LoggingContextEnv with DoobieTodoRepositoryEnv {
        override val loggingContext: LoggingContext            = LoggingContext.RequestId(uuidToString(uuid))
        override val logger: Logger.Service[LoggingContextEnv] = appEnv.logger
        override val clock: Clock.Service[Any]                 = appEnv.clock
        override val db: DoobieTodoRepository                  = appEnv.db
      },
    )

  override def run(args: List[String]): ZManaged[ZEnv, Nothing, Int] =
    (for {
      transactor <- DoobieRepository
                     .mkTransactor(DBConfig("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "", ""))
                     .orDie
      _ <- runHttp(8080)
            .provide(new AppEnv.Live(DoobieTodoRepository.withDoobieTodoRepositoryManaged(transactor)))
            .toManaged_
    } yield 0)
      .foldM(
        err => putStrLn(s"Execution failed with: $err").as(1).toManaged_,
        _ => ZManaged.succeed(0),
      )
}
