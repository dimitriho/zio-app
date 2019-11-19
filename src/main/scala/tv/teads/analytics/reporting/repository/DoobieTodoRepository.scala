package tv.teads.analytics.reporting.repository

import cats.effect.Blocker
import doobie.h2.H2Transactor
import doobie.util.transactor.Transactor
import zio._
import zio.blocking.Blocking
import zio.interop.catz._

trait DoobieTodoRepositoryEnv {
  val db: DoobieTodoRepository
}

trait DoobieTodoRepository {
  val foo: Task[List[Int]]
}

object DoobieTodoRepository {
  import doobie.implicits._

  def withDoobieTodoRepositoryManaged(
      transactor: Transactor[Task],
  ): ZManaged[Any, Nothing, DoobieTodoRepository] =
    ZManaged.succeed(new DoobieTodoRepository {
      override val foo: Task[List[Int]] =
        sql"SELECT 42".query[Int].to[List].transact(transactor)
    })
}

final case class DBConfig(
    url: String,
    driver: String,
    user: String,
    password: String,
)

object DoobieRepository {
  def mkTransactor[R <: Blocking](cfg: DBConfig): ZManaged[R, Throwable, Transactor[Task]] =
    for {
      rt         <- ZIO.runtime[Any].toManaged_
      transactEC <- ZIO.environment[Blocking].flatMap(_.blocking.blockingExecutor).toManaged_
      xa <- {
        implicit val runtime: Runtime[Any] = rt
        H2Transactor
          .newH2Transactor[Task](
            cfg.url,
            cfg.user,
            cfg.password,
            rt.Platform.executor.asEC,
            Blocker.liftExecutionContext(transactEC.asEC),
          )
          .toManaged
      }
    } yield xa
}
