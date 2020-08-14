dependencies {
  api("org.jetbrains.kotlin:kotlin-script-runtime")
  api("org.jetbrains.kotlin:kotlin-script-util")
  api("org.jetbrains.kotlin:kotlin-compiler-embeddable")
  api("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable")
  
  val maven_version = "3.5.0"
  api("org.apache.maven:maven-resolver-provider:$maven_version")
  
  val maven_resolver_version = "1.4.2"
  api("org.apache.maven.resolver:maven-resolver-api:$maven_resolver_version")
  api("org.apache.maven.resolver:maven-resolver-impl:$maven_resolver_version")
  api("org.apache.maven.resolver:maven-resolver-util:$maven_resolver_version")
  api("org.apache.maven.resolver:maven-resolver-transport-http:$maven_resolver_version")
  api("org.apache.maven.resolver:maven-resolver-transport-file:$maven_resolver_version")
  api("org.apache.maven.resolver:maven-resolver-connector-basic:$maven_resolver_version")
  
}

val generatedSourceDir = buildDir.resolve("generated/src/main/kotlin")
kotlin {
  sourceSets {
    main {
      kotlin.srcDir(generatedSourceDir)
    }
  }
}

tasks {
  val injectVersion by creating {
    doLast {
      val packageFolder = project.group.toString().replace(".", "/")
      val versionFile = generatedSourceDir.resolve("$packageFolder/version.kt")
      if (!versionFile.parentFile.exists()) {
        versionFile.parentFile.mkdirs()
        versionFile.createNewFile()
      }
      versionFile.writeText(
        "package ${project.group}\n\n" +
            "object kts {\n\n" +
            "   @JvmField\n" +
            "   val version = \"${project.version}\"\n\n" +
            "}\n"
      )
      
    }
  }
  val compileKotlin by getting {
    dependsOn(injectVersion)
  }
}
