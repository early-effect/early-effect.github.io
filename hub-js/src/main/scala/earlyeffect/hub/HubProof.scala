package earlyeffect.hub

import ascent.*
import ascent.dsl.*
import zio.*

import ascent.dom as d

/** Ascent island: compile/does-not flipper. SSR already painted the first beat. */
object HubProof:

  def mount: UIO[Unit] =
    val node = d.document.getElementById(HubView.ProofMountId)
    if node == null then ZIO.unit
    else
      val root = node.asInstanceOf[d.Element]
      val n    = HubCopy.proofBeats.size
      for
        beat <- sq(0)
        _    <- ZIO.succeed(clear(root))
        _    <- AscentApp.mount(view(beat, n), root)
      yield ()

  private def view(beat: Source[Int], n: Int): UI[Any] =
    val current = beat.map(i => HubCopy.proofBeats(Math.floorMod(i, n)))
    E.div(
      current.map(HubView.proofPanel),
      E.div(
        HubCss.ProofNav,
        E.button(
          HubCss.Ghost,
          Ev.onClick(_ => beat.update(i => Math.floorMod(i - 1, n))),
          "Previous",
        ),
        E.button(
          HubCss.Ghost,
          Ev.onClick(_ => beat.update(i => Math.floorMod(i + 1, n))),
          "Next",
        ),
      ),
    )

  private def clear(el: d.Element): Unit =
    el.innerHTML = ""
end HubProof
