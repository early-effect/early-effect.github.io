package earlyeffect.hub

import ascent.*
import zio.*

/** Browser entry: live catalog + proof islands. The page itself is SSR. */
object ClientMain extends ZIOAppDefault:

  def run =
    for
      _ <- HubCatalog.refresh
      _ <- HubProof.mount
      _ <- ZIO.never
    yield ()
end ClientMain
