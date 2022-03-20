import OpOptions.*
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.rendering.TextStyles.dim
import com.soywiz.klock.measureTime

class Operation(val name: String, val options: Sequence<OpOptions>, val arguments: List<String> = listOf(), var script: String = "", val description: String = "") {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true

    other as Operation

    if (name != other.name) return false
    if (script != other.script) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + script.hashCode()
    return result
  }

  override fun toString(): String {
    return "Operation(name='$name', options=${options.toList().size}:${options.joinToString(",")}, script='hash:${script.hashCode()}', description='$description')"
  }

  fun execute(extraOptions: MutableSet<OpOptions> = mutableSetOf(), operationArguments: Sequence<String>) {
    extraOptions.addAll(options)
    val extraOptionsList = extraOptions.filterNot { it == DUMMY }

    val argumentMap = mutableMapOf<String, String>()
    operationArguments.forEachIndexed { index, s ->
      val z = arguments.getOrNull(index)?.removePrefix("+") ?: exitError("No argument found in operation for #${index + 1} supplied argument")
      argumentMap[z as String] = s
      script = script.replace("\\{\\{ *$z *}}".toRegex(RegexOption.IGNORE_CASE), s)
    }

    if (CLEAN in extraOptions) {
      Specifics.execute(script, extraOptions, argumentMap)
      return
    }
    dev(green("running operation '$name' ${if (extraOptionsList.isEmpty()) "" else "with"} ${extraOptionsList.joinToString(",") { it.toStringShort() }}"))
    dbg("Trying to execute with options=${extraOptionsList.size}:${extraOptionsList.joinToString(",")}")

    if (PRINT in extraOptions) {
      t.println((yellow + dim)(script))
    }
    separator()

    if (TIME in extraOptions) {
      measureTime {
        Specifics.execute(script, extraOptions, argumentMap)
      }.also { separator(); dev(yellow("Operation '$name' took ${it.millisecondsLong}ms")) }
    } else {
      Specifics.execute(script, extraOptions, argumentMap)
    }
  }
}

enum class OpOptions(val option: String) {
  QUIET("q"),
  PRINT("p"),
  KEEP("k"),
  TIME("t"),
  CLEAN("c"),
  DUMMY("DUMMY");

  fun toStringShort(): String {
    return super.toString().substring(0..0)
  }
}

inline fun <reified T : Enum<T>, V> ((T) -> V).find(value: V): T? {
  return enumValues<T>().firstOrNull { this(it) == value }
}

fun Sequence<String>.parse(): Pair<Sequence<OpOptions>, List<String>> {
  val z1 = this.filter {
    it.isNotEmpty()
  }
  return Pair(
    z1.filterNot { it.first() == '+' }
      .map { OpOptions::option.find(it) ?: DUMMY.also { _ -> t.forStdErr().danger("'$it' is not a valid option. See 'Operation Options' under dev -h or all supported options") } },
    z1.filter { it.first() == '+' }.toList()
  )
}