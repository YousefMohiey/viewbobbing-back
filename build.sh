#!/usr/bin/env bash
# Builds the View Bobbing In Video Settings Fabric mod (manual javac, no Gradle).
set -euo pipefail

PROJ="$(cd "$(dirname "$0")" && pwd)"
MC="$HOME/AppData/Roaming/.minecraft/versions/26.3/26.3.jar"
JDK="${JDK25:-$LOCALAPPDATA/Temp/jdk25/jdk-25.0.5+8}"
LIBS="$PROJ/libs"
OUT="$PROJ/build/classes"
JAR="$PROJ/build/viewbobbing-back-1.1.0.jar"

w() { cygpath -w "$1"; }

mkdir -p "$OUT" "$(dirname "$JAR")"
rm -rf "$OUT"/*

"$JDK/bin/javac" --release 21 -nowarn \
  -cp "$(w "$MC");$(w "$LIBS/brigadier-1.3.11.jar");$(w "$LIBS/sponge-mixin-0.17.5+mixin.0.8.7.jar");$(w "$LIBS/mixinextras-fabric-0.5.5.jar");$(w "$LIBS/sodium-fabric-0.9.2+mc26.3.jar")" \
  -d "$(w "$OUT")" "$(w "$PROJ/src")"/vbmod/mixin/*.java "$(w "$PROJ/src")"/vbmod/*.java

cp "$PROJ/res/fabric.mod.json" "$PROJ/res/viewbobbing-back.mixins.json" "$PROJ/res/viewbobbing-back.sodium.mixins.json" "$PROJ/res/viewbobbing-back.legacy.mixins.json" "$OUT"/

# 1.21.9 - 1.21.11 mixins, compiled against the intermediary-named game jar
"$JDK/bin/javac" --release 21 -nowarn \
  -cp "$(w "$OUT");$(w "$LIBS/1.21.11-im.jar");$(w "$LIBS/fabric-loader-0.20.1.jar");$(w "$LIBS/brigadier-1.3.11.jar");$(w "$LIBS/guava-33.6.0-jre.jar");$(w "$LIBS/sponge-mixin-0.17.5+mixin.0.8.7.jar");$(w "$LIBS/mixinextras-fabric-0.5.5.jar");$(w "$LIBS/sodium-0.7.3.jar");$(w "$LIBS/sodium-0.8.15.jar")" \
  -d "$(w "$OUT")" "$(w "$PROJ/src")"/vbmod/legacy/*.java
(cd "$OUT" && "$JDK/bin/jar" --create --file "$(w "$JAR")" .)

echo "built: $JAR"
"$JDK/bin/jar" --list --file "$(w "$JAR")"
