package earlyeffect.hub

import specular.site.ProjectMeta

/** Split a live catalog into Write / Prove / Ship bays. Unknown names fall through to Write. */
object CatalogGroups:

  final case class Bay(layer: HubCopy.Layer, projects: Vector[ProjectMeta])

  def bays(projects: Vector[ProjectMeta]): Vector[Bay] =
    val used                                             = scala.collection.mutable.LinkedHashSet.empty[String]
    def take(names: Vector[String]): Vector[ProjectMeta] =
      names.flatMap { name =>
        projects.find(p => !used.contains(p.name) && matches(p, name)).map { p =>
          used += p.name
          p
        }
      }
    val grouped   = HubCopy.layers.map(layer => Bay(layer, take(layer.names)))
    val leftovers = projects.filterNot(p => used.contains(p.name))
    grouped match
      case head +: rest => head.copy(projects = head.projects ++ leftovers) +: rest
      case _            => grouped
  end bays

  def blurb(project: ProjectMeta): String =
    val fromMeta = project.description.map(_.trim).filter(_.nonEmpty).filterNot { d =>
      d.equalsIgnoreCase(project.name) || d.equalsIgnoreCase(project.displayTitle)
    }
    fromMeta.orElse(HubCopy.blurbs.get(project.name.toLowerCase)).getOrElse(project.displayTitle)

  private def matches(project: ProjectMeta, name: String): Boolean =
    val n    = project.name.toLowerCase
    val t    = project.displayTitle.toLowerCase.replace(' ', '-')
    val want = name.toLowerCase
    n == want || t == want || n == s"sbt-$want" || t == s"sbt-$want"
end CatalogGroups
