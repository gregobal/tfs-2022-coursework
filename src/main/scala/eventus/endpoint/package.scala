package eventus

import eventus.service.{
  CommunityService,
  EventService,
  MemberService,
  NotificationService,
  ParticipantService,
  ReviewService
}
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.interceptor.log.DefaultServerLog
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zhttp.http.{Http, Request, Response}
import zio.logging.backend.SLF4J
import zio.{RIO, Task}

package object endpoint {
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
