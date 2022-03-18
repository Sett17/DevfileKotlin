import com.github.ajalt.clikt.completion.CompletionCandidates
import kotlinx.coroutines.runBlocking
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.localCurrentDirVfs
import com.soywiz.korio.util.substringAfterOrNull

@ThreadLocal
object Devfile {
  val vfs = localCurrentDirVfs.vfs

  // only allowed to do this because this parse function is called in the main init, so I'm sure it's executed before any reference to this var
  lateinit var devfile: VfsFile
  val ops = mutableSetOf<Operation>()
  var completionCandidates = CompletionCandidates.Fixed(setOf())

  var currentZOperation :Operation? = null

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

      val zScript = StringBuilder()
      file.readLines().forEach {
        if (it.startsWith("***")) {
          currentZOperation?.let { op ->
            op.script = zScript.trimEnd().toString()
            ops.add(op)
          }
          val nameDescription = it.trimStart('*').substringBefore('*')
          dbg("both: $nameDescription")
          val name = nameDescription.substringBefore("|")
          val description = nameDescription.substringAfterOrNull("|") ?: ""
          dbg("description: $description")
          val options = it.drop(nameDescription.length + 3).splitToSequence('*')
          val z = options.parse()
          currentZOperation = Operation(name, z.first, z.second, description = description)
          if (currentZOperation != null && OpOptions.DUMMY in currentZOperation!!.options) {
            exitError("Error parsing operation '${currentZOperation!!.name}'")
          }
          zScript.clear()
        } else if (currentZOperation != null) {
          zScript.append(it)
          zScript.append('\n')
        }
      }
      currentZOperation?.let { op ->
        op.script = zScript.trimEnd().toString()
        ops.add(op)
      }
      completionCandidates = CompletionCandidates.Fixed(ops.map { it.name.lowercase() }.toSet())
      dbgExec {
        ops.forEach(::dbg)
      }
    }
  }
}