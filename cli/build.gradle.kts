plugins {
  application
  id("com.github.johnrengelman.shadow") version "6.0.0"
}

application {
  mainClassName = "info.laht.kts.cli.KtsCLI"
}

distributions {
  main {
    distributionBaseName.set("kts")
  }
}

dependencies {
  implementation(project(":lib"))
  implementation("info.picocli:picocli:4.5.0")
}

tasks {
  val installDist by getting(Sync::class)
  val shadowJar by getting(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class)
  create("executableJar", Exec::class) {
//    enabled = !org.gradle.internal.os.OperatingSystem.current().isWindows
    dependsOn(shadowJar)
    group = "distribution"
    workingDir = buildDir
    val wrapperFile = project.projectDir.resolve("exeWrapper.sh").relativeTo(workingDir).invariantSeparatorsPath
    val jarFile = shadowJar.archiveFile.get().asFile.relativeTo(workingDir).invariantSeparatorsPath
    val outFile = buildDir.resolve("kts").relativeTo(workingDir).invariantSeparatorsPath
    executable = "bash"
    args(
      "-c",
      "'cat $wrapperFile $jarFile > $outFile && chmod +x $outFile'"
    )
    inputs.files(shadowJar.outputs)
    outputs.file(outFile)
  }
  withType<CreateStartScripts> {
    applicationName = "kts"
  }
}
