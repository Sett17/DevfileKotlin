import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.output.HelpFormatter
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.rendering.TextStyles.*
import kotlin.system.exitProcess
import kotlin.system.measureNanoTime

fun exitError(s: String, exitCode: Int = 1) {
  t.forStdErr().danger(s)
  exitProcess(exitCode)
}

fun dbg(a: Any?) {
  if (debugMode) {
    t.info("DEBUG: " + dim(a.toString()))
  }
}

fun dbgExec(block: () -> Unit) {
  if (debugMode && extendedDebugMode) {
    block.invoke()
  }
}

fun dbgTime(name: String, block: () -> Unit) {
  if (debugMode) {
    measureNanoTime {
      block.invoke()
    }.also { dbg("$name took ${it / 1e6}ms") }
  } else {
    block.invoke()
  }
}

class ColorHelpFormatter : CliktHelpFormatter() {
  override fun renderHelpText(help: String, tags: Map<String, String>) = dim(super.renderHelpText(help, tags))
  override fun renderTag(tag: String, value: String) = green(super.renderTag(tag, value))
  override fun renderOptionName(name: String) = yellow(super.renderOptionName(name))
  override fun renderArgumentName(name: String) = yellow(super.renderArgumentName(name))
  override fun renderSubcommandName(name: String) = yellow(super.renderSubcommandName(name))
  override fun renderSectionTitle(title: String) = (bold + underline)(super.renderSectionTitle(title))
  override fun optionMetavar(option: HelpFormatter.ParameterHelp.Option) = green(super.optionMetavar(option))
}
