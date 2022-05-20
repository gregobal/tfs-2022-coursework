package eventus.common.validation

import zio.prelude.Validation

import java.time.ZonedDateTime

object FieldValidator {

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
    val emailRegex =
      """^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
    if (fieldValue.isBlank || emailRegex.findFirstIn(fieldValue).isEmpty)
      Validation.fail(s"field value for $fieldName not valid")
    else Validation.succeed(fieldValue)
  }
}
