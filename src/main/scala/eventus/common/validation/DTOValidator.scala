package eventus.common.validation

import eventus.dto.{CommunityCreateDTO, EventCreateDTO, MemberCreateDTO}
import zio.prelude.{Validation, ZValidation}
import eventus.common.ValidationError
import eventus.common.validation.FieldValidator._

object DTOValidator {
  // TODO - выяснить причину баги (в библиотеке prelude? zio2 RC5?), validateWith не аккумулирует строки ошибок
  // имплисит для аккумуляции ошибок в строку для ValidationError
  private implicit def toValidatorError[T](
      validation: Validation[String, T]
  ): ZValidation[Nothing, ValidationError, T] = {
    Validation
      .fromEither(validation.toEitherWith(_.mkString(", ")))
      .mapError(ValidationError)
  }

  def validateCommunityCreateDTO(
      dto: CommunityCreateDTO
  ): Validation[ValidationError, CommunityCreateDTO] =
    Validation
      .validateWith(
        for {
          v <- validateStringFieldNotBlank(dto.name, "name")
          _ <- validateStringMinLength(dto.name, "name", 2)
        } yield v,
        Validation.succeed(dto.description)
      )(CommunityCreateDTO)

  def validateEventCreateDTO(
      dto: EventCreateDTO
  ): Validation[ValidationError, EventCreateDTO] =
    Validation
      .validateWith(
        for {
          v <- validateStringFieldNotBlank(dto.title, "title")
          _ <- validateStringMinLength(dto.title, "title", 6)
        } yield v,
        Validation.succeed(dto.description),
        validateZoneDateTimeIsFuture(dto.datetime, "datetime"),
        Validation.succeed(dto.location),
        Validation.succeed(dto.link),
        Validation.succeed(dto.capacity)
      )(EventCreateDTO)

  def validateMemberCreateDTO(
      dto: MemberCreateDTO
  ): Validation[ValidationError, MemberCreateDTO] =
    validateEmailField(dto.email).map(MemberCreateDTO).mapError(ValidationError)
}
