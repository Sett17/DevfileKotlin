import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.rendering.TextStyles.*
import com.soywiz.korio.file.Vfs
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.VfsProcessHandler
import com.soywiz.korio.file.std.cacheVfs
import com.soywiz.korio.file.std.tempVfs
import kotlinx.coroutines.runBlocking
import platform.posix.exit

class Operation(val name: String, val options: Sequence<OpOptions> = sequenceOf(), var script: Script = Script("")) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true

    other as Operation

    if (name != other.name) return false
    if (script != other.script) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + script.hashCode()
    return result
  }

  override fun toString(): String {
    return "Operation(name='$name', options=${options.toList().size}:${options.joinToString(",")}, script='hash:${script.hashCode()}')"
  }

  fun execute(extraOptions: MutableSet<OpOptions> = mutableSetOf()) {
    extraOptions.addAll(options)
    dbg("Trying to execute with options=${extraOptions.toList().size}:${extraOptions.joinToString(",")}")
    if (extraOptions.contains(OpOptions.PRINT)) {
      t.println(yellow(script.text))
    }
    when (Platform.osFamily) {
      OsFamily.LINUX -> {
        dbgTime("creating and executing file") {
          runBlocking {
            VfsFile(tempVfs.vfs, "devfiles/").mkdir()
            val tmpFile = VfsFile(tempVfs.vfs, "devfiles/${script.hashCode()}.dev")
            tmpFile.writeLines(
              listOf(
                "shopt -s expand_aliases",
                "source ~/.bash_aliases"
              ) + script.text.lines()
            )
            platform.posix.system("/bin/bash ${tmpFile.absolutePath} ${if (extraOptions.contains(OpOptions.SILENT)) "> /dev/null" else ""}")
            if (extraOptions.contains(OpOptions.DELETE) && !extraOptions.contains(OpOptions.KEEP)) tmpFile.delete()
          }
        }
      }
//      OsFamily.WINDOWS -> {}
//      OsFamily.MACOSX -> {}
      else           -> exitError("There is currently no support for executing operations on ${Platform.osFamily}", 38)
    }
  }
}

enum class OpOptions(val option: String) {
  SILENT("s"),
  PRINT("p"),
  DELETE("d"),
  KEEP("k"),
  DUMMY("DUMMY"),

}

inline fun <reified T : Enum<T>, V> ((T) -> V).find(value: V): T? {
  return enumValues<T>().firstOrNull { this(it) == value }
}

fun Sequence<String>.toOpOptions(): Sequence<OpOptions> {
  return this.filter { it.isNotEmpty() }.map { OpOptions::option.find(it) ?: OpOptions.DUMMY.also { _ -> exitError("'$it' is not a valid option") } }
}