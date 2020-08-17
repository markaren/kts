# kts
Kotlin script runner.

Enables Kotlin scripts to be invoked with external maven dependencies and no prior software requirements (except for a JRE).

### Example

The following script named `example.kts` can be invoked by directly invoking `./example.kts` in a shell.

```kotlin
#!kts

//using artifact("com.google.code.gson:gson:2.8.6")

import com.google.gson.Gson

println(Gson())

```

Alternatively, invoke `./kts example.kts`.

##### Custom repositories

Custom repositories may be added like so:

```kotlin
//using repository("https://some.repo.com/maven")
```

`mavenCentral` is available by default.

### Building

The Command Line Application `kts` can be built by invoking `./gradlew installDist`

##### Contributors

[markaren](https://github.com/markaren)
[mpetuska](https://github.com/mpetuska)
