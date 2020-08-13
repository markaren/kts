# kts
Kotlin script runner.

Enables Kotlin scripts to be invoked with external maven dependencies and no prior software requirements (except for a JRE).

### Example

The following script named `example.kts` can be invoked by directly invoking `./example.kts` in a shell.

```kotlin
#!kts

//using maven("com.google.code.gson:gson:2.8.6")

import com.google.gson.Gson

println(Gson())

```

### Building

The Command Line Application `kts` can be built by invoking `./gradlew installDist`
