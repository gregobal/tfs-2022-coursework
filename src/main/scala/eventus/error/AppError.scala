package eventus.error

sealed trait AppError {
  val message: String
}

case class RepositoryError(throwable: Throwable) extends AppError {
  override val message: String = throwable.getMessage
}

case class ServiceError(throwable: Throwable) extends AppError {
  override val message: String = throwable.getMessage
}
