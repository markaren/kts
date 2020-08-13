plugins {
  id("base")
  kotlin("jvm") version "1.4.0-rc"
}

repositories {
  jcenter()
  mavenCentral()
}

tasks {
  wrapper {
    gradleVersion = "6.5.1"
    distributionType = Wrapper.DistributionType.ALL
  }
  
  test {
    failFast = true
    useJUnitPlatform()
  }
}

println("Gradle version is ${gradle.getGradleVersion()}")

group = "info.laht.kts"
version = rootProject.file("VERSION").readLines()[0]
println("Building ${project.name} v$version")

subprojects {
  apply(plugin = "java")
  apply(plugin = "kotlin")
  
  group = rootProject.group
  version = rootProject.version
  
  repositories {
    mavenCentral()
  }
  
  dependencies {
    val slf4j_version = "1.7.25"
    implementation("org.slf4j:slf4j-api:$slf4j_version")
    implementation(kotlin("stdlib"))
    runtimeOnly("org.slf4j:slf4j-log4j12:$slf4j_version")
    
    val junit_version = "5.3.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
  }
  
  pluginManager.withPlugin("kotlin") {
    dependencies {
      implementation("org.jetbrains.kotlin:kotlin-stdlib")
    }
  }
  
}
