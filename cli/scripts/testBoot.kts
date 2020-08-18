#!../build/executable/boot/kts

@file:DependsOn("com.google.code.gson:gson:2.8.6")

import com.google.gson.Gson

val gson = Gson()

"2+4=${2+4}"
