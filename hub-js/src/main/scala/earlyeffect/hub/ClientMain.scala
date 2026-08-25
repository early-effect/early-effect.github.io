package earlyeffect.hub

import ascent.*
import zio.*

/** Browser entry: mount the Early Effect landing as an Ascent SPA. */
object ClientMain extends ZIOAppDefault:

  def run =
    for
      urls     <- HubCatalog.allowlist
      projects <- HubCatalog.fetch(urls)
      ui       <- HubApp.body(projects)
      _        <- AscentApp.mountBody(ui)
      _        <- ZIO.never
    yield ()
end ClientMain
