# kts
Kotlin script runner.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Enables Kotlin scripts to be invoked with external maven dependencies and no prior software requirements (except for a JRE).

### Example

The following script named `example.kts` can be invoked by directly invoking `./example.kts` in a shell.

```kotlin
#!kts

@file:DependsOn("com.google.code.gson:gson:2.8.6")

import com.google.gson.Gson

println(Gson())

```

Alternatively, invoke `./kts example.kts`.

##### Custom repositories

Custom repositories may be added like so:

```kotlin
@file:Repository("https://some.repo.com/maven")
```

`mavenCentral` is available by default.

### Building

The Command Line Application `kts` can be built by invoking `./gradlew installDist`

##### Contributors

* [markaren](https://github.com/markaren)
* [mpetuska](https://github.com/mpetuska)



# NOTE

You might want to use kotlin-main-kts >= 1.4.0 instead of this if you don't need transitive dependencies. 
It provides autocompletion in IntelliJ and script caching. Adding `kotlin` from the Kotlin compiler distribution is just as easy as downloading `kts`. 

