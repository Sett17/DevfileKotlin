import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.mordant.rendering.TextColors.brightGreen
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.terminal.Terminal
import com.soywiz.kds.iterators.fastForEach

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
  help = Msg.helpText,
  printHelpOnEmptyArgs = true,
) {
  val printScript by option("-p", "--print", help = Msg.printOptionHelp).flag(default = false)
  val quiet by option("-q", "--quiet", help = Msg.quietOptionHelp).flag(default = false)
  val keep by option("-k", "--keep", help = Msg.keepOptionHelp).flag(default = false)
  val time by option("-t", "--time", help = Msg.timeOptionHelp).flag(default = false)

  val ops: List<String> by argument(
    name = "OPERATIONS",
    help = Msg.opsArgumentHelp
  ).multiple(required = true)

  init {
    versionOption("0.0.1")
    context { helpFormatter = ColorHelpFormatter() }

    eagers()

    dbg("Devfile ${if (extendedDebugMode) "extended" else ""} debug output is enabled")
    dbgTime("parsing") {
      Devfile.parse()
    }
    dbg("${Devfile.ops.size} ops were parsed")
  }

  override fun run() {
//    t.println(CLILOGO)
    ops.fastForEach { s ->
      val op = Devfile.ops.find { it.name == s }
      val options = mutableSetOf(
        if (printScript) OpOptions.PRINT else OpOptions.DUMMY,
        if (quiet) OpOptions.QUIET else OpOptions.DUMMY,
        if (keep) OpOptions.KEEP else OpOptions.DUMMY,
        if (time) OpOptions.TIME else OpOptions.DUMMY,
      )
      dbgExec { dbg(options) }
      op?.execute(options) ?: exitError(Msg.errorNotAnOperation(s))
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
