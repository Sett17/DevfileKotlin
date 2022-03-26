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

  const val operationOptionsHelp = "All of these options can also be used as option letters in the Devfile"
  const val printOptionHelp = "Prints the corresponding script for the operation and execute it"
  const val quietOptionHelp = "Stops all std output of the operation. err output is unchanged"
  const val keepOptionHelp = "Keeps the script file after execution"
  const val timeOptionHelp = "Times the execution of the Operation, and prints it afterwards"
  const val cleanOptionHelp = "Removes all output related to Devfile. To e.g. use it with pipes. Some debug messages will still be printed, when the debug mode is enabled"

  const val debugOptionHelp = "Enables output of debug messages for Devfile itself. This has to be its own option (or be the first one in concatenated options), because it is parsed separately. -DD prints even more Information"
  const val versionOptionHelp = "Prints the current version and exit"
  const val cleanTmpOptionHelp = "Deletes all .dev files in the platform corresponding tmp directory"
  const val listOptionHelp = "Lists all operations and exit"
  const val editOptionHelp = "Opens the Devfile in an applicable Editor. \$EDITOR or vim under Linux, and the default editor for the format under Windows"
  const val initOptionHelp = "Creates a Devfile and puts a sample operation into it"
  const val infoOptionHelp = "Shows information about the given operations and then exits without executing it"

  const val opsArgumentHelp = "Which operation you want to execute, in the order you want them to execute. The arguments name should be supplied in the same order as defied in the Devfile"

  fun errorNotAnOperation(s: Any) : String = "$s is not an operation! Use 'dev -l' to show all operations"
}
