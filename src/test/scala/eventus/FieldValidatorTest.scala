package eventus

import eventus.common.validation._
import zio.test.{Gen, ZIOSpecDefault, assertTrue, check}

import java.time.ZonedDateTime

object FieldValidatorTest extends ZIOSpecDefault {

  private val nonEmptyStringLen3To10Gen =
    Gen.stringBounded(3, 10)(Gen.alphaChar)
  private val blankStringGen = Gen.stringBounded(0, 10)(Gen.whitespaceChars)
  private val emptyString = ""

  override def spec = suite("validators for fields tests")(
    suite("validateStringFieldNotBlank")(
      test(
        "it should return result with non empty string"
      ) {
        check(nonEmptyStringLen3To10Gen) { str =>
          assertTrue(
            validateStringFieldNotBlank(str, "str").toEither.isRight
          )
        }
      },
      test(
        "it should return error with blank string"
      ) {
        check(blankStringGen) { str =>
          assertTrue(
            validateStringFieldNotBlank(str, "str").toEither.isLeft
          )
        }
      },
      test(
        "it should return error with empty string"
      ) {
        assertTrue(
          validateStringFieldNotBlank(emptyString, "str").toEither.isLeft
        )
      },
      test(
        "it should return error message when validation failed"
      ) {
        assertTrue(
          validateStringFieldNotBlank(
            emptyString,
            "str"
          ).toEither.swap
            .map(_.mkString)
            .getOrElse("") == "field value for str is empty or blank"
        )
      }
    ),
    suite("validateStringMinLength")(
      test(
        "it should return result for ok length string"
      ) {
        check(nonEmptyStringLen3To10Gen) { str =>
          assertTrue(
            validateStringMinLength(str, "str", 3).toEither.isRight
          )
        }
      },
      test(
        "it should return error message when validation failed"
      ) {
        check(nonEmptyStringLen3To10Gen) { str =>
          val min = str.length + 1
          assertTrue(
            validateStringMinLength(
              emptyString,
              "str",
              min
            ).toEither.swap
              .map(_.mkString)
              .getOrElse("") == "field value length for str less than " + min
          )
        }
      }
    ),
    suite("validateZoneDateTimeIsFuture")(
      test(
        "it should return result for date in future"
      ) {
        val td = ZonedDateTime.now().plusDays(1)
        assertTrue(
          validateZoneDateTimeIsFuture(td, "date").toEither.isRight
        )
      },
      test(
        "it should return error message when validation failed"
      ) {
        val td = ZonedDateTime.now().minusDays(1)
        assertTrue(
          validateZoneDateTimeIsFuture(td, "date").toEither.swap
            .map(_.mkString)
            .getOrElse("") == "zoneDateTime field value for date is in past"
        )
      }
    ),
    suite("validateEmailField")(
      test(
        "it should return result for valid email"
      ) {
        assertTrue(
          validateEmailField("email@val.id").toEither.isRight
        )
      },
      test(
        "it should return error message when validation failed"
      ) {
        assertTrue(
          validateEmailField("test").toEither.swap
            .map(_.mkString)
            .getOrElse("") == "field value for email not valid"
        )
      }
    )
  )
}
