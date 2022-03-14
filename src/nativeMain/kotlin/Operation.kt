import OpOptions.*
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.dim
import com.soywiz.klock.measureTime

class Operation(val name: String, val options: Sequence<OpOptions> = sequenceOf(), var script: Script = Script("")) {

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
    return "Operation(name='$name', options=${options.toList().size}:${options.joinToString(",")}, script='hash:${script.hashCode()}')"
  }

  fun execute(extraOptions: MutableSet<OpOptions> = mutableSetOf()) {
    extraOptions.addAll(options)
    dev(green("running Operation '$name' with ${extraOptions.filter { it != DUMMY }.joinToString(",") { it.toStringShort() }}"))
    dbg("Trying to execute with options=${extraOptions.toList().size}:${extraOptions.joinToString(",")}")
    if (PRINT in extraOptions) {
      t.println((yellow + dim)(script.text))
    }
    seperator()

    // hier nen if mit scroll region dann

    if (TIME in extraOptions) {
      measureTime {
        Specifics.execute(script, extraOptions)
      }.also { seperator(); dev(yellow("Operation '$name' took ${it.millisecondsLong}ms")) }
    } else {
      Specifics.execute(script, extraOptions)
    }
  }
}

enum class OpOptions(val option: String) {
  QUIET("q"),
  PRINT("p"),
  KEEP("k"),
  TIME("t"),
  DUMMY("DUMMY");

  fun toStringShort(): String {
    return super.toString().substring(0..0)
  }
}

inline fun <reified T : Enum<T>, V> ((T) -> V).find(value: V): T? {
  return enumValues<T>().firstOrNull { this(it) == value }
}

fun Sequence<String>.toOpOptions(): Sequence<OpOptions> {
  return this.filter { it.isNotEmpty() }.map { OpOptions::option.find(it) ?: DUMMY.also { _ -> exitError("'$it' is not a valid option") } }
}