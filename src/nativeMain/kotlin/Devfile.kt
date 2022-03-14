import com.github.ajalt.clikt.completion.CompletionCandidates
import kotlinx.coroutines.runBlocking
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.localCurrentDirVfs
import com.soywiz.korio.file.std.tempVfs

@ThreadLocal
object Devfile {
  val vfs = localCurrentDirVfs.vfs
  // only allowed to do this because this parse function is called in the main init, so I'm sure it's executed before any reference to this var
  lateinit var devfile : VfsFile
  val ops = mutableSetOf<Operation>()
  var completionCandidates = CompletionCandidates.Fixed(setOf())

  fun parse() {
    runBlocking {
      var file = VfsFile(vfs, "Devfile")
      if (!file.exists()) {
        file = VfsFile(vfs, "dev.file")
        if (!file.exists()) {
          exitError("Couldn't read Devfile or dev.file", 2)
        }
      }
      devfile = file

      var zOperation: Operation? = null
      val zScript = StringBuilder()
      file.readLines().forEach {
        if (it.startsWith("***")) {
          zOperation?.let { op ->
            op.script = Script(zScript.trimEnd() .toString())
            ops.add(op)
          }
          val name = it.trimStart('*').substringBefore('*')
          val options = it.drop(name.length + 3).splitToSequence('*')
          zOperation = Operation(name, options.toOpOptions())
          zScript.clear()
        } else if (zOperation != null) {
          zScript.append(it)
          zScript.append('\n')
        }
      }
      zOperation?.let { op ->
        op.script = Script(zScript.trimEnd().toString())
        ops.add(op)
      }
      completionCandidates = CompletionCandidates.Fixed(ops.map { it.name.lowercase() }.toSet())
      dbgExec {
        ops.forEach(::dbg)
      }
    }
  }
}