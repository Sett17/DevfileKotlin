import com.github.ajalt.mordant.rendering.TextColors.*

object Msg {
//  val helpText = "Tired of the weird quirks of make? Annoyed of making typos in long chained commands, or getting to them via reverse-i-search?\n" +
//      "Well, here is a solution that comes as just an executable for each platform and with an extensive help command.\n" +
//      "by Sett | ${
//        brightGreen(
//          "https://github.com/Sett17/Devfile"
//        )
//      }\n" +
//      "\n" +
//      "All single letter options can also be used inside the Devfile. For more information see REFERENCE.md"


  val helpText = """```
$CLILOGO ```
Tired of the weird quirks of make? Annoyed of making typos in long chained commands, or getting to them via reverse-i-search?
Well, here is a solution that comes as just an executable for each platform and with an extensive help command.""" +
"\nby Sett | ${brightGreen("https://github.com/Sett17/Devfile")}"

  const val printOptionHelp = "Prints the corresponding script for the operation and execute it"
  const val quietOptionHelp = "Stops all std output of the operation. err output is unchanged"
  const val keepOptionHelp = "Keeps the script file after execution"
  const val timeOptionHelp = "Times the execution if the Operation, and prints it afterwards"
  const val debugOptionHelp = "Enables output of debug messages for Devfile itself. This has to be its own option (or be the first one in concatenated options), because it is parsed separately. -DD prints even more Information"
  const val cleanTmpOptionHelp = "Deletes all .dev files in the platform corresponding tmp directory"
  const val listOptionHelp = "Lists all operations and exit"
  const val editOptionHelp = "Opens the Devfile in an applicable Editor. \$EDITOR or vim under Linux, and the default editor for the format under Windows"
  const val infoOptionHelp = "Shows information about the given operations"

  const val opsArgumentHelp = "Which operation you want to execute, in the order you want them to execute"

  fun errorNotAnOperation(s: Any) : String = "$s is not an operation! Use 'dev -l' to show all operations"
}