import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.eagerOption
import com.github.ajalt.mordant.terminal.Terminal
import kotlin.system.exitProcess
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.*
import kotlin.system.measureNanoTime

val CLILOGO = """ ________  _______   ___      ___ ________ ___  ___       _______      
|\   ___ \|\  ___ \ |\  \    /  /|\  _____\\  \|\  \     |\  ___ \     
\ \  \_|\ \ \   __/|\ \  \  /  / | \  \__/\ \  \ \  \    \ \   __/|    
 \ \  \ \\ \ \  \_|/_\ \  \/  / / \ \   __\\ \  \ \  \    \ \  \_|/__  
  \ \  \_\\ \ \  \_|\ \ \    / /   \ \  \_| \ \  \ \  \____\ \  \_|\ \ 
   \ \_______\ \_______\ \__/ /     \ \__\   \ \__\ \_______\ \_______\
    \|_______|\|_______|\|__|/       \|__|    \|__|\|_______|\|_______|
by Sett | ${brightGreen("https://github.com/Sett17/Devfile")}

"""
val t = Terminal()

class DevfileCLI : CliktCommand() {
  init {
    t.println(CLILOGO)
    measureNanoTime {
      Devfile.parse()
    }.also { dbg("parsing took ${it / 1e6}ms") }
    eagerOption("-l", "--list", help = "Lists all operations") {
      error("-l NOIMP")
//      t.println((bold)("Listing all ops:"))
    }
  }

  override fun run() {
    t.println("fuck you")
  }
}

fun main(args: Array<String>) = DevfileCLI().main(args)

fun error(s: String, exitCode: Int = 1) {
  t.forStdErr().danger(s)
  exitProcess(exitCode)
}

fun dbg(a: Any?) {
  t.info((dim)(a.toString()))
}