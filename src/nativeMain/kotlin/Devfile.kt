import kotlinx.coroutines.runBlocking
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.LocalVfs
import com.soywiz.korio.file.std.LocalVfsV2
import com.soywiz.korio.file.std.localCurrentDirVfs
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.rendering.TextStyles.*

@ThreadLocal
object Devfile {
  val vfs = localCurrentDirVfs.vfs

  fun parse() {
    runBlocking {
      var file = VfsFile(vfs, "dev.file")
      if (!file.exists()) {
        file = VfsFile(vfs, "Devfile")
        if (!file.exists()) {
          error("Couldn't read Devfile or devfile", 2)
        }
      }
      dbg(file)

    }
  }
}