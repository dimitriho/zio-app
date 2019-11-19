package tv.teads.analytics.reporting

import zio.{RIO, ZIO}

package object repository {
  def fetchIntList: RIO[DoobieTodoRepositoryEnv, List[Int]] =
    ZIO.environment[DoobieTodoRepositoryEnv].flatMap(_.db.foo)
}
