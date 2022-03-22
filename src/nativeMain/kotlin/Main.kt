import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.brightGreen
import com.github.ajalt.mordant.rendering.TextStyles.*
import com.github.ajalt.mordant.terminal.Terminal
import com.soywiz.kds.iterators.fastForEach
import com.soywiz.kds.iterators.fastForEachWithIndex
import kotlin.test.assertNotNull

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

class OperationOptions : OptionGroup(name = "Operation Options", help = Msg.operationOptionsHelp) {
  val printScript by option("-p", "--print", help = Msg.printOptionHelp).flag(default = false)
  val quiet by option("-q", "--quiet", help = Msg.quietOptionHelp).flag(default = false)
  val keep by option("-k", "--keep", help = Msg.keepOptionHelp).flag(default = false)
  val time by option("-t", "--time", help = Msg.timeOptionHelp).flag(default = false)
  val clean by option("-c", "--clean", help = Msg.cleanOptionHelp).flag(default = false)
}

class DevfileCLI : CliktCommand(
  name = "dev",
  help = Msg.helpText,
  printHelpOnEmptyArgs = true,
) {
  val operationOptions by OperationOptions()
  val info by option("-i", "--info", help = Msg.infoOptionHelp).flag(default = false)

  val ops: List<String> by argument(
    name = "OPERATIONS+ARGUMENT...",
    help = Msg.opsArgumentHelp
  ).multiple(required = true)

  init {
    versionOption("v0.0.7")
    context { helpFormatter = ColorHelpFormatter() }

    dbg("Devfile ${if (extendedDebugMode) "extended" else ""} debug output is enabled")
    dbgTime("parsing") {
      Devfile.parse()
    }
    dbg("${Devfile.ops.size} ops were parsed")

    eagers()
  }

  override fun run() {
    ops.fastForEach { s ->
      val operationName = s.substringBefore('+')
      val operationArguments = s.drop(operationName.length).splitToSequence('+').filter { it.isNotEmpty() }

      val op = Devfile.ops.find { it.name == operationName }
      op ?: exitError(Msg.errorNotAnOperation(operationName))
      assertNotNull(op)

      if (info) {
        dev(TextColors.green("Information about the operation '${op.name}'"))
        t.println(op.description)
        t.println((underline + bold)("Options:"))
        op.options.toList().also { if (it.isEmpty()) t.println("${" ".repeat(4)}/") }.forEach {
          t.println("${" ".repeat(4)}$it")
        }
        t.println((underline + bold)("Arguments:"))
        op.arguments.also { if (it.isEmpty()) t.println("${" ".repeat(4)}/") }.forEach {
          t.println("${" ".repeat(4)}$it")
        }
        t.println((underline + bold)("Script:"))
        op.script.lines().fastForEachWithIndex { index, value ->
          t.println(dim((index + 1).toString().padStart(3, ' ')) + " $value")
        }
        t.println()
        return@fastForEach
      }

      val options = mutableSetOf<OpOptions>()
      with(options) {
        if (operationOptions.clean) {
          add(OpOptions.CLEAN)
          return@with
        }
        if (operationOptions.printScript) add(OpOptions.PRINT)
        if (operationOptions.quiet) add(OpOptions.QUIET)
        if (operationOptions.keep) add(OpOptions.KEEP)
        if (operationOptions.time) add(OpOptions.TIME)
      }
      op.execute(options, operationArguments)
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
