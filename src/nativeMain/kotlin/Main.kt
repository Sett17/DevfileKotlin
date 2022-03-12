import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors.brightGreen
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.rendering.TextStyles.dim
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import com.soywiz.kds.iterators.fastForEach
import com.soywiz.korio.file.fullName
import com.soywiz.korio.file.std.tempVfs
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

val CLILOGO = bold(
  """ ________  _______   ___      ___ ________ ___  ___       _______      
|\   ___ \|\  ___ \ |\  \    /  /|\  _____\\  \|\  \     |\  ___ \     
\ \  \_|\ \ \   __/|\ \  \  /  / | \  \__/\ \  \ \  \    \ \   __/|    
 \ \  \ \\ \ \  \_|/_\ \  \/  / / \ \   __\\ \  \ \  \    \ \  \_|/__  
  \ \  \_\\ \ \  \_|\ \ \    / /   \ \  \_| \ \  \ \  \____\ \  \_|\ \ 
   \ \_______\ \_______\ \__/ /     \ \__\   \ \__\ \_______\ \_______\
    \|_______|\|_______|\|__|/       \|__|    \|__|\|_______|\|_______|
"""
) + "      by Sett   ${brightGreen("https://github.com/Sett17/Devfile")}\n\n"

val t = Terminal()
var debugMode = false
var extendedDebugMode = false

class DevfileCLI : CliktCommand(
  name = "dev",
  help = "Tired of the weird quirks of make? Annoyed of making typos in long chained commands, or getting to them via reverse-i-search?\nWell, here is a solution that comes as just an executable for each platform and with an extensive help command.\nby Sett | ${
    brightGreen(
      "https://github.com/Sett17/Devfile"
    )
  }\n\nAll single letter options can also be used inside the Devfile. For more information see REFERENCE.md",
  printHelpOnEmptyArgs = true,
) {
  val printScript by option("-p", "--print", help = "Prints the corresponding script for the operation and execute it").flag(default = false)
  val silent by option("-s", "--silent", help = "Stops all std output of the operation. err output is unchanged").flag(default = false)
  val delete by option(help = "Either deletes or keeps the script after execution. -k takes precedence over -d [-d]").switch(
    "-d" to true,
    "--delete" to true,
    "-k" to false,
    "--keep" to false,
  ).default(true)

  val ops: List<String> by argument(
    name = "OPERATIONS",
    completionCandidates = CompletionCandidates.Fixed(setOf("moin", "hola", "tach")),
    help = "Which operation you want to execute, in the order you want them to execute"
  ).multiple(required = true)

  init {
    context { helpFormatter = ColorHelpFormatter() }
    eagerOption(
      "-D",
      "-DD",
      help = "Enables output of debug messages for Devfile itself. This has to be its own option (or be the first one in concatenated options), because it is parsed separately. -DD prints even more Information"
    ) {}
    eagerOption("--clean-tmp", help = "Deletes all .dev files in the platform corresponding tmp directory") {
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
    dbg("Devfile ${if (extendedDebugMode) "extended" else ""} debug output is enabled")
    dbgTime("parsing") {
      Devfile.parse()
    }
    dbg("${Devfile.ops.size} ops were parsed")
    eagerOption("-l", "--list", help = "Lists all operations and exit") {
      t.println(CLILOGO)
      t.println(table {
        align = TextAlign.CENTER
        captionTop(bold("All parsed options"))
        header { row("name", "options", "lines") }
        body {
          Devfile.ops.forEach {
            row(it.name, it.options.joinToString(" "), it.script.lineNumber)
          }
        }
        captionBottom(dim("use 'dev -p OPERATION' to show the underlying script"))
      })
      exitProcess(0)
    }
  }

  override fun run() {
//    t.println(CLILOGO)
    ops.fastForEach { s ->
      val op = Devfile.ops.find { it.name == s }
      dbgExec {
        dbg(
          mutableSetOf(
            if (printScript) OpOptions.PRINT else OpOptions.DUMMY,
            if (silent) OpOptions.SILENT else OpOptions.DUMMY,
            if (delete) OpOptions.DELETE else OpOptions.KEEP,
          )
        )
      }
      op?.execute(
        mutableSetOf(
          if (printScript) OpOptions.PRINT else OpOptions.DUMMY,
          if (silent) OpOptions.SILENT else OpOptions.DUMMY,
          if (delete) OpOptions.DELETE else OpOptions.KEEP,
        )
      ) ?: exitError("$s is not an operation! Use 'dev -l' to show all operations")
    }
  }
}

fun main(args: Array<String>) {
  args.fastForEach {
    if (it.startsWith("-D")) {
      debugMode = true
      if (it.startsWith("-DD")) extendedDebugMode = true
    }
  }
  DevfileCLI().completionOption(hidden = true).main(args)
}
