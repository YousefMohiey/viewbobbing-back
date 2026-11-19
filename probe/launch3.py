"""Offline launcher for verification runs. Args: fabric_id mc_version asset_index assets_dir"""
import json
import os
import subprocess
import sys
import urllib.request
import zipfile

MC_DIR = os.path.join(os.path.expandvars(r"%APPDATA%"), ".minecraft")
JDK25 = os.path.expandvars(r"%LOCALAPPDATA%\Temp\jdk25\jdk-25.0.5+8")
JDK21 = r"C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot"
VERSION = sys.argv[1]
MC_VER = sys.argv[2]
ASSET_INDEX = sys.argv[3]
ASSETS = sys.argv[4]
NATIVES = os.path.join(MC_DIR, "natives-" + MC_VER)
OSNAME = "windows"


def load_json(path):
    with open(path, "r", encoding="utf-8") as fh:
        return json.load(fh)


def rules_allow(lib):
    rules = lib.get("rules")
    if not rules:
        return True
    allowed = False
    for rule in rules:
        os_rule = rule.get("os") or {}
        if os_rule.get("name") and os_rule["name"] != OSNAME:
            continue
        if os_rule.get("arch") and os_rule["arch"] not in ("x86_64",):
            continue
        if rule.get("features"):
            continue
        allowed = rule["action"] == "allow"
    return allowed


def collect_libraries(version_json):
    libs = []
    for lib in version_json["libraries"]:
        name = lib["name"]
        if ":natives-" in name and name.rsplit(":", 1)[1] != "natives-windows":
            continue
        if not rules_allow(lib):
            continue
        artifact = (lib.get("downloads") or {}).get("artifact") or {}
        path = artifact.get("path")
        if not path:
            group, art, ver = name.split(":")[:3]
            classifier = f"-{name.split(':')[3]}" if len(name.split(":")) > 3 else ""
            path = f"{group.replace('.', '/')}/{art}/{ver}/{art}-{ver}{classifier}.jar"
        full = os.path.join(MC_DIR, "libraries", path)
        if not os.path.exists(full):
            url = artifact.get("url") or (lib.get("url", "") + path)
            if url:
                os.makedirs(os.path.dirname(full), exist_ok=True)
                urllib.request.urlretrieve(url, full)
        libs.append(full)
    return libs


def extract_natives(jars):
    os.makedirs(NATIVES, exist_ok=True)
    for jar in jars:
        if "natives-windows" not in os.path.basename(jar):
            continue
        with zipfile.ZipFile(jar) as zf:
            for entry in zf.namelist():
                if entry.endswith((".dll", ".so", ".dylib")):
                    target = os.path.join(NATIVES, os.path.basename(entry))
                    if not os.path.exists(target):
                        with zf.open(entry) as src, open(target, "wb") as dst:
                            dst.write(src.read())


def main():
    vjson = load_json(os.path.join(MC_DIR, "versions", VERSION, f"{VERSION}.json"))
    parent = load_json(os.path.join(MC_DIR, "versions", MC_VER, f"{MC_VER}.json"))

    def ga(name):
        return ":".join(name.split(":")[:2])

    own = collect_libraries(vjson)
    taken = {ga(lib["name"]) for lib in vjson["libraries"]}
    parent_copy = dict(parent)
    parent_copy["libraries"] = [l for l in parent["libraries"] if ga(l["name"]) not in taken]
    libs = own + collect_libraries(parent_copy)
    client_jar = os.path.join(MC_DIR, "versions", MC_VER, f"{MC_VER}.jar")
    extract_natives(libs)

    major = parent.get("javaVersion", {}).get("majorVersion", 21)
    java = JDK25 if major >= 25 else JDK21

    classpath = ";".join(libs + [client_jar])
    cmd = [
        os.path.join(java, "bin", "java.exe"),
        "-Xmx3G",
        f"-Djava.library.path={NATIVES}",
        "-Dfabric.side=client",
        "-cp", classpath,
        "net.fabricmc.loader.impl.launch.knot.KnotClient",
        "--username", "Test",
        "--version", VERSION,
        "--gameDir", MC_DIR,
        "--assetsDir", ASSETS,
        "--assetIndex", ASSET_INDEX,
        "--uuid", "00000000000000000000000000000001",
        "--accessToken", "0",
        "--versionType", "release",
        "--width", "1280",
        "--height", "720",
    ]
    print("launching", MC_VER, "on", java)
    subprocess.run(cmd, cwd=MC_DIR)


if __name__ == "__main__":
    main()
