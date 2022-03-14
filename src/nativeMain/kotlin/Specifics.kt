import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.tempVfs
import kotlinx.coroutines.runBlocking
import platform.posix.system

object Specifics {
  private val currentOS = Platform.osFamily

  fun execute(script: Script, options: Set<OpOptions>) {
    dbgTime("creating and executing file") {
      runBlocking {

        var prefixLines = listOf<String>()
        var suffixLines = listOf<String>()
        var howToExec = ""
        var silence = ""
        var extension = ""

        when (currentOS) {
          OsFamily.LINUX   -> {
            prefixLines = listOf("shopt -s expand_aliases", "source ~/.bash_aliases")
            howToExec = "/bin/bash"
            silence = "> /dev/null"
            extension = ".dev"
          }
          OsFamily.WINDOWS -> {
            prefixLines = listOf("@echo off")
            suffixLines = listOf("@echo on")
            howToExec = ""
            silence = ">NUL"
            extension = ".dev.bat"
          }
//      OsFamily.MACOSX -> {}
          else             -> exitError("There is currently no support for executing operations on $currentOS", 38)
        }

        VfsFile(tempVfs.vfs, "devfiles/").mkdir()
        val tmpFile = VfsFile(tempVfs.vfs, "devfiles/${script.hashCode()}${extension}")
        tmpFile.writeLines(prefixLines + script.text.lines() + suffixLines)
        system("$howToExec ${tmpFile.absolutePath} ${if (OpOptions.QUIET in options) silence else ""}")
        if (OpOptions.KEEP !in options) tmpFile.delete()
      }
    }
  }

  fun edit() {
    var howToEdit = ""
    when (currentOS) {
      OsFamily.LINUX   -> {
        howToEdit = "\"\${EDITOR:-vim}\" "
      }
      OsFamily.WINDOWS -> {
        howToEdit = "start"
      }

      else             -> exitError("There is currently no support for editing files on $currentOS", 38)
    }
    system("$howToEdit ${Devfile.devfile.absolutePath}")
  }
}
