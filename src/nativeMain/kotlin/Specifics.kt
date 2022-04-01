import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.jailedLocalVfs
import com.soywiz.korio.file.std.tempVfs
import kotlinx.coroutines.runBlocking
import platform.posix.system

@ThreadLocal
object Specifics {
  private var currentOS: OS = if (Platform.osFamily == OsFamily.LINUX) {
    if (system("cat /proc/version | grep -q WSL2") == 0) {
      OS.WSL_LINUX
    } else OS.LINUX
  } else if (Platform.osFamily == OsFamily.WINDOWS) {
    OS.WINDOWS
  } else {
    exitError("${Platform.osFamily} is currently not supported by Devfile", 95)
    OS.UNSUPPORTED
  }

  fun execute(script: String, options: Set<OpOptions>, argumentMap: MutableMap<String, String>) {
    dbgTime("creating and executing file") {
      runBlocking {

        if (currentOS == OS.WSL_LINUX && OpOptions.WSLWINDOWS in options) {
          currentOS = OS.WSL_WINDOWS
        }

        var prefixLines = listOf<String>()

        when (currentOS) {
          OS.LINUX       -> {
            prefixLines = listOf("shopt -s expand_aliases", "source ~/.bash_aliases") + argumentMap.map { "DEV_${it.key.uppercase()}=${it.value}" }
          }
          OS.WSL_LINUX   -> {
            prefixLines = listOf("shopt -s expand_aliases", "source ~/.bash_aliases") + argumentMap.map { "DEV_${it.key.uppercase()}=${it.value}" }
          }
          OS.WINDOWS     -> {
            prefixLines = listOf("@echo off") + argumentMap.map { "set DEV_${it.key.uppercase()}=${it.value}" }
          }
          OS.WSL_WINDOWS -> {
            prefixLines = listOf("@echo off") + argumentMap.map { "set DEV_${it.key.uppercase()}=${it.value}" }
          }
          else           -> exitError("There is currently no support for executing operations on $currentOS", 38)
        }

        val tmpVfs = if (OpOptions.WSLWINDOWS in options) {
          jailedLocalVfs("/mnt/c/Windows/Temp").vfs
        } else {
          tempVfs.vfs
        }
        VfsFile(tmpVfs, "devfiles/")
        val tmpFile = VfsFile(tmpVfs, "devfiles/${script.hashCode()}${currentOS.extension}").also { dbg(it.windowsPath) }
        tmpFile.writeLines(prefixLines + script.lines() + currentOS.suffixLines)
        system("${currentOS.howToExec} ${tmpFile.windowsPath} ${if (OpOptions.QUIET in options) currentOS.silence else ""}")
        if (OpOptions.KEEP !in options) tmpFile.delete()
      }
    }
  }

  fun edit() {
    Devfile.parse()
    var howToEdit = ""
    when (currentOS) {
      OS.LINUX       -> {
        howToEdit = "\"\${EDITOR:-vim}\" "
      }
      OS.WSL_LINUX   -> {
        howToEdit = "\"\${EDITOR:-vim}\" "
      }
      OS.WSL_WINDOWS -> {
        howToEdit = "\"\${EDITOR:-vim}\" "
      }
      OS.WINDOWS     -> {
        howToEdit = "start"
      }

      else           -> exitError("There is currently no support for editing files on $currentOS", 38)
    }
    system("$howToEdit ${Devfile.devfile.absolutePath}")
  }
}

enum class OS {
  LINUX {
    override val suffixLines = listOf("")
    override val howToExec = "/bin/bash"
    override val silence = "> /dev/null"
    override val extension = ".dev"

  },
  WINDOWS {
    override val suffixLines = listOf("@echo on")
    override val howToExec = ""
    override val silence = ">NUL"
    override val extension = ".dev.bat"
  },
  WSL_LINUX {
    override val suffixLines = LINUX.suffixLines
    override val howToExec = LINUX.howToExec
    override val silence = LINUX.silence
    override val extension = LINUX.extension
  },
  WSL_WINDOWS {
    override val suffixLines = WINDOWS.suffixLines
    override val howToExec = "/mnt/c/Windows/System32/cmd.exe /c"
    override val silence = LINUX.silence
    override val extension = WINDOWS.extension
  },
  UNSUPPORTED {
    override val suffixLines = listOf("")
    override val howToExec = ""
    override val silence = ""
    override val extension = ""
  };

  abstract val suffixLines: List<String>
  abstract val howToExec: String
  abstract val silence: String
  abstract val extension: String
}