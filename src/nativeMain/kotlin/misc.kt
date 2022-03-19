import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.output.HelpFormatter
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.rendering.TextStyles.*
import com.soywiz.klock.measureTime
import kotlin.math.min
import kotlin.system.exitProcess

fun exitError(s: String, exitCode: Int = 1) {
  t.forStdErr().danger(s)
  exitProcess(exitCode)
}

fun dev(a: Any?) {
  t.println(TextColors.gray("DEV: ") + a.toString())
}

fun dbg(a: Any?) {
  if (debugMode) {
    t.info("DBG: " + dim(a.toString()))
  }
}

fun dbgExec(block: () -> Unit) {
  if (debugMode && extendedDebugMode) {
    block.invoke()
  }
}

fun separator() {
  t.println((dim + TextColors.gray)("─".repeat(min(t.info.width, 80))))
//  HorizontalRule()
}

fun dbgTime(name: String, block: () -> Unit) {
  if (debugMode) {
    measureTime {
      block.invoke()
    }.also { dbg("$name took ${it.milliseconds}ms") }
  } else {
    block.invoke()
  }
}

class ColorHelpFormatter : CliktHelpFormatter(showDefaultValues = true) {
  override fun renderHelpText(help: String, tags: Map<String, String>) = dim(super.renderHelpText(help, tags))
  override fun renderTag(tag: String, value: String) = green(super.renderTag(tag, value))
  override fun renderOptionName(name: String) = yellow(super.renderOptionName(name))
  override fun renderArgumentName(name: String) = yellow(super.renderArgumentName(name))
  override fun renderSubcommandName(name: String) = yellow(super.renderSubcommandName(name))
  override fun renderSectionTitle(title: String) = (bold + underline)(super.renderSectionTitle(title))
  override fun optionMetavar(option: HelpFormatter.ParameterHelp.Option) = green(super.optionMetavar(option))
}

