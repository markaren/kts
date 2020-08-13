# kts
Kotlin script runner

Enables Kotlin script to be invoked with external maven dependencies.

### Example

The following script can be invoked by invoking directly in a shell.

```kotlin
#!kts

//using maven("com.google.code.gson:gson:2.8.6")

import com.google.gson.Gson

println(Gson())

```

### Building

The Command Line Application `kts` can be built by invoking `./gradlew installDist`
