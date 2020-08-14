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
    dependsOn(shadowJar)
    group = "distribution"
    workingDir = buildDir
    val buildFile = project.projectDir.resolve("scripts/buildExeJar.sh").relativeTo(workingDir)
    val wrapperFile = project.projectDir.resolve("scripts/exeWrapper.sh").relativeTo(workingDir)
    val jarFile = shadowJar.archiveFile.get().asFile.relativeTo(workingDir)
    val outFile = buildDir.resolve("kts").relativeTo(workingDir)
    executable = "bash"
    args(buildFile, wrapperFile, jarFile, outFile)
    inputs.files(buildFile, wrapperFile, jarFile)
    outputs.file(outFile)
  }
  withType<CreateStartScripts> {
    applicationName = "kts"
  }
}
