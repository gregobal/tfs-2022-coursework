package eventus.common

import zio.ZIO

sealed trait AppError {
  val message: String
}

case class RepositoryError(throwable: Throwable) extends AppError {
  override val message: String = throwable.getMessage
}

case class ServiceError(override val message: String) extends AppError

object AppError {
  def handleServerLogicError[R, A](
      zio: ZIO[R, AppError, A]
  ): ZIO[R, String, A] = zio
    .tapError(appError => ZIO.logError(appError.message))
    .mapError {
      case ServiceError(message)      => message
      case RepositoryError(throwable) => throw throwable
    }
}
