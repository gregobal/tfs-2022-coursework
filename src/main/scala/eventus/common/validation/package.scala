package eventus.common

import zio.IO
import zio.prelude.Validation

import java.time.ZonedDateTime

package object validation {
  private val emailRegex =
      """^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
  
  def validateToZIO[T](
      validation: Validation[String, T]
  ): IO[ValidationError, T] = {
    Validation
      .fromEither(validation.toEitherWith(_.mkString(", ")))
      .mapError(ValidationError)
      .toZIO
  }

  def validateStringFieldNotBlank(
      fieldValue: String,
      fieldName: String
  ): Validation[String, String] =
    Validation
      .fromPredicateWith(s"field value for $fieldName is empty or blank")(
        fieldValue
      )(!_.isBlank)

  def validateStringMinLength(
      fieldValue: String,
      fieldName: String,
      minLength: Int
  ): Validation[String, String] =
    Validation
      .fromPredicateWith(
        s"field value length for $fieldName less than $minLength"
      )(
        fieldValue
      )(_.length >= minLength)

  def validateZoneDateTimeIsFuture(
      fieldValue: ZonedDateTime,
      fieldName: String
  ): Validation[String, ZonedDateTime] =
    Validation
      .fromPredicateWith(s"zoneDateTime field value for $fieldName is in past")(
        fieldValue
      )(_.isAfter(ZonedDateTime.now()))

  def validateEmailField(
      fieldValue: String,
      fieldName: String = "email"
  ): Validation[String, String] = {    
    if (fieldValue.isBlank || emailRegex.findFirstIn(fieldValue).isEmpty)
      Validation.fail(s"field value for $fieldName not valid")
    else Validation.succeed(fieldValue)
  }

  def validateIntMax(
      fieldValue: Int,
      fieldName: String,
      max: Int
  ): Validation[String, Int] =
    Validation
      .fromPredicateWith(s"field value for $fieldName more than $max")(
        fieldValue
      )(_ <= max)

  def validateIntMin(
      fieldValue: Int,
      fieldName: String,
      min: Int
  ): Validation[String, Int] =
    Validation
      .fromPredicateWith(s"field value for $fieldName less than $min")(
        fieldValue
      )(_ >= min)
}
