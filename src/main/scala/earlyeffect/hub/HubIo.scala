package earlyeffect.hub

import zio.*

import scala.jdk.CollectionConverters.*

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, StandardCopyOption}

/** Java NIO lifted once into `IO[HubError, A]`. Callers stay on HubError. */
object HubIo:

  def readUtf8(path: Path): IO[HubError, String] =
    blocking(s"read $path"):
      Files.readString(path, StandardCharsets.UTF_8).nn

  def readBytes(path: Path): IO[HubError, Array[Byte]] =
    blocking(s"read bytes $path"):
      Files.readAllBytes(path).nn

  def writeUtf8(path: Path, text: String): IO[HubError, Unit] =
    blocking(s"write $path"):
      Files.writeString(path, text, StandardCharsets.UTF_8)
      ()

  def copy(from: Path, to: Path): IO[HubError, Unit] =
    blocking(s"copy $from → $to"):
      Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
      ()

  def createDirectories(path: Path): IO[HubError, Unit] =
    blocking(s"mkdir $path"):
      Files.createDirectories(path)
      ()

  def listRegularFiles(dir: Path): IO[HubError, Vector[Path]] =
    blocking(s"list $dir"):
      val stream = Files.list(dir)
      try stream.iterator().asScala.filter(Files.isRegularFile(_)).toVector
      finally stream.close()

  private def blocking[A](op: String)(thunk: => A): IO[HubError, A] =
    ZIO.attemptBlockingIO(thunk).mapError(io => HubError.Io(op, message(io)))

  private def message(e: IOException): String =
    Option(e.getMessage).map(_.nn).filter(_.nonEmpty).getOrElse(e.getClass.getSimpleName.nn)
end HubIo
