package earlyeffect.hub

import specular.site.LiveCatalog
import zio.*

/** Browser entry: refresh hub catalog cards from allowlisted metadata.json URLs. */
object ClientMain extends ZIOAppDefault:

  def run =
    for
      _ <- LiveCatalog.bootstrap
      _ <- ZIO.never
    yield ()
end ClientMain
