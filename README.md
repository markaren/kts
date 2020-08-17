# kts
Kotlin script runner.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/markaren/kts/issues) 

[ ![Download](https://api.bintray.com/packages/laht/mvn/kts/images/download.svg?version=0.2.1) ](https://bintray.com/laht/mvn/kts/0.2.1/link)

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

* [markaren](https://github.com/markaren)
* [mpetuska](https://github.com/mpetuska)
