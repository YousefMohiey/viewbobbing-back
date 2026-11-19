#!/usr/bin/env bash
# Runs the verification probe against every untested supported version.
set -u
cd "$(dirname "$0")"
MC="$HOME/AppData/Roaming/.minecraft"
RESULTS="results.txt"
: > "$RESULTS"
ASSETS="$(cygpath -w "$TMPDIR/assets")"

run_one() {
  local fid="$1" ver="$2" idx="$3"
  rm -f "$MC/mods/"*.jar 2>/dev/null
  cp ../build/viewbobbing-back-1.0.0.jar ../vbmod-probe.jar "$MC/mods/"
  rm -f "$MC/logs/latest.log" 2>/dev/null
  timeout 115 python launch3.py "$fid" "$ver" "$idx" "$ASSETS" > "run-$ver.out" 2>&1
  sleep 2
  powershell -NoProfile -Command "Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force" >/dev/null 2>&1
  sleep 3
  echo "=== $ver ===" >> "$RESULTS"
  grep "\[probe\]" "$MC/logs/latest.log" 2>/dev/null | sed 's/.*\[STDOUT\]: //' >> "$RESULTS"
  grep -ciE "Mixin apply failed|Could not execute entrypoint" "$MC/logs/latest.log" 2>/dev/null | sed 's/^/errors=/' >> "$RESULTS"
}

run_one fabric-loader-0.20.1-26.1 26.1 30
run_one fabric-loader-0.20.1-26.1.1 26.1.1 30
run_one fabric-loader-0.20.1-26.1.2 26.1.2 30
run_one fabric-loader-0.20.1-26.2 26.2 32
run_one fabric-loader-0.20.1-1.21.9 1.21.9 27
echo "ALL_RUNS_DONE" >> "$RESULTS"
