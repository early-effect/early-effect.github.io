package earlyeffect.hub

/** Named failures for the hub. Values, not exceptions: `ZIO[R, HubError, A]`. */
sealed trait HubError:
  def render: String
  override def toString: String = render

object HubError:

  final case class RefusedUrl(url: String) extends HubError:
    def render = s"Refusing non-http(s) metadata URL: $url"

  final case class HttpStatus(url: String, status: Int) extends HubError:
    def render = s"GET $url → $status"

  final case class BodyTooLarge(url: String, bytes: Int) extends HubError:
    def render = s"$url: body is $bytes bytes (max ${specular.site.ProjectMeta.MaxBodyBytes})"

  final case class InvalidMeta(url: String, reason: String) extends HubError:
    def render = s"$url: $reason"

  final case class Transport(url: String, detail: String) extends HubError:
    def render = s"$url: $detail"

  case object ClientNotLinked extends HubError:
    def render = "JS client not linked; run sbt specularSite (or hubJS/fastLinkJS) first."

  final case class LandingIncomplete(missing: Vector[String]) extends HubError:
    def render = s"Landing HTML missing expected copy: ${missing.mkString(", ")}"

  final case class Io(op: String, detail: String) extends HubError:
    def render = s"$op: $detail"

  final case class Build(op: String, detail: String) extends HubError:
    def render = s"$op: $detail"
end HubError
