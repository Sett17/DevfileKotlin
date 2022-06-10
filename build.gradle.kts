plugins {
  kotlin("multiplatform") version "1.7.0"
}

group = "me.sett"
version = "rolling"

repositories {
  mavenCentral()
}

dependencies {
  commonMainImplementation("com.github.ajalt.clikt:clikt:3.4.0")
  commonMainImplementation("com.github.ajalt.mordant:mordant:2.0.0-beta5")
  commonMainImplementation("com.soywiz.korlibs.korio:korio:2.6.2")
}

kotlin {
  val hostOs = System.getProperty("os.name")
  val isMingwX64 = hostOs.startsWith("Windows")
  val nativeTarget = when {
    hostOs == "Mac OS X" -> macosX64("native")
    hostOs == "Linux"    -> linuxX64("native")
    isMingwX64           -> mingwX64("native")
    else                 -> throw GradleException("Host OS is not supported in Kotlin/Native.")
  }

  nativeTarget.apply {
    binaries {
      executable {
        freeCompilerArgs = freeCompilerArgs + "-Xdisable-phases=EscapeAnalysis"
        entryPoint = "main"
        baseName = "dev"
      }
    }
  }
  sourceSets {
    val nativeMain by getting
    val nativeTest by getting
  }
}
