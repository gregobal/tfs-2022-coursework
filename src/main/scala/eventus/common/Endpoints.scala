package eventus.common

import eventus.community.endpoint.{CommunityEndpoint, MemberEndpoint}
import eventus.community.service.{CommunityService, MemberService}
import eventus.event.endpoint.{
  EventEndpoint,
  ParticipantEndpoint,
  ReviewEndpoint
}
import eventus.event.service.{EventService, ParticipantService, ReviewService}
import eventus.notification.service.NotificationService
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zhttp.http.{Http, Request, Response}
import zio.{RIO, Task}

object Endpoints {
  type AppServices =
    EventService
      with CommunityService
      with MemberService
      with ParticipantService
      with NotificationService
      with ReviewService

  private val endpointsRegistry = List(
    CommunityEndpoint.all,
    MemberEndpoint.all,
    EventEndpoint.all,
    ParticipantEndpoint.all,
    ReviewEndpoint.all
  ).flatten

  val zioHttp: Http[AppServices, Throwable, Request, Response] =
    endpointsRegistry
      .map(endpoint =>
        ZioHttpInterpreter()
          .toHttp(
            endpoint.asInstanceOf[ZServerEndpoint[AppServices, ZioStreams]]
          )
      )
      .reduce((a, b) => a ++ b)

  private type EffectType[A] = RIO[AppServices, A]
  val swagger: Http[AppServices, Throwable, Request, Response] =
    ZioHttpInterpreter[AppServices]().toHttp(
      SwaggerInterpreter()
        .fromEndpoints[Task](
          endpointsRegistry.map(_.endpoint),
          "Eventus",
          "0.1.0"
        )
        .asInstanceOf[List[ServerEndpoint[ZioStreams, EffectType]]]
    )
}
