plugins {
  application
  id("com.github.johnrengelman.shadow") version "6.0.0"
  id("org.springframework.boot") version "2.3.3.RELEASE"
}

application {
  mainClassName = "info.laht.kts.cli.KtsCLI"
}

distributions {
  main {
    distributionBaseName.set("kts")
  }
}

springBoot {
  buildInfo()
}

dependencies {
  implementation(project(":lib"))
  implementation("info.picocli:picocli:4.5.0")
}

tasks {
  bootJar {
    archiveClassifier.set("boot")
    launchScript {
      script = projectDir.resolve("scripts/exeWrapper.sh")
    }
    layered()
    requiresUnpack("**/kotlin-script-runtime-*.jar")
    requiresUnpack("**/kotlin-stdlib-*.jar")
    requiresUnpack("**/kotlin-compiler-embeddable-*.jar")
    requiresUnpack("**/kotlin-script-util-*.jar")
    requiresUnpack("**/kotlin-scripting-compiler-embeddable-*.jar")
  }
  val installDist by getting(Sync::class)
  val shadowJar by getting(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class)
  create("executableBootJar", Copy::class) {
    dependsOn(bootJar.get())
    group = "executable"
  
    from(bootJar.get().archiveFile)
    val outDir = buildDir.resolve("executable/boot")
    into(outDir)
    rename {
      "kts"
    }
    inputs.file(bootJar.get().archiveFile)
    outputs.dir(outDir)
  }
  create("executableShadowJar", Exec::class) {
    dependsOn(shadowJar)
    group = "executable"
    workingDir = buildDir
    val buildFile = project.projectDir.resolve("scripts/buildExeJar.sh")
    val wrapperFile = project.projectDir.resolve("scripts/exeWrapper.sh")
    val jarFile = shadowJar.archiveFile.get().asFile
    val outDir = buildDir.resolve("executable/shadow")
    val outFile = outDir.resolve("kts.jar")
    executable = "bash"
    args(
      buildFile.relativeTo(workingDir),
      wrapperFile.relativeTo(workingDir),
      jarFile.relativeTo(workingDir), outFile.relativeTo(workingDir)
    )
    inputs.files(buildFile.absolutePath, wrapperFile.absolutePath, jarFile.absolutePath)
    outputs.dir(outDir)
  }
  withType<CreateStartScripts> {
    applicationName = "kts"
  }
}
