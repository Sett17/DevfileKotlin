import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.eagerOption
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.table.table
import com.soywiz.kds.iterators.fastForEach
import com.soywiz.korio.file.fullName
import com.soywiz.korio.file.std.tempVfs
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun CliktCommand.eagers() {
  eagerOption("-l", "--list", help = Msg.listOptionHelp) {
    t.println(CLILOGO)
    t.println(table {
      align = TextAlign.CENTER
      captionTop(TextStyles.bold("All parsed operations"))
      header { row("name", "options", "lines") }
      body {
        Devfile.ops.forEach {
          row(it.name, it.options.map { o -> o.toStringShort() }.joinToString(" "), it.script.lines().size)
        }
      }
      captionBottom(TextStyles.dim("use 'dev -i OPERATION' to show the underlying script"))
    })
    exitProcess(0)
  }

  eagerOption("-e", "--edit", help = Msg.editOptionHelp) {
    Specifics.edit()
    exitProcess(0)
  }

  eagerOption("--clean-tmp", help = Msg.cleanTmpOptionHelp) {
    runBlocking {
      var howMany: Int
      var freedBytes = 0L
      tempVfs.vfs.listSimple("devfiles").also { howMany = it.size }.fastForEach {
        freedBytes += it.stat().size
        dbg("Deleting ${it.fullName}")
        it.delete()
      }
      t.info("Deleted $howMany temporary dev files, to free up ${(freedBytes / 1024.0)} KiB")
      exitProcess(0)
    }
  }

  eagerOption(
    "-D",
    "-DD",
    help = Msg.debugOptionHelp
  ) {}

}