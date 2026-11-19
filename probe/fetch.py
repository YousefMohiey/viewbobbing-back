"""Downloads the asset indexes for the given version jsons and every object they need."""
import json
import os
import sys
import urllib.request
from concurrent.futures import ThreadPoolExecutor

assets = sys.argv[1]
version_jsons = sys.argv[2:]

os.makedirs(os.path.join(assets, "indexes"), exist_ok=True)
os.makedirs(os.path.join(assets, "objects"), exist_ok=True)

needed = {}
for path in version_jsons:
    data = json.load(open(path, encoding="utf-8"), strict=False)
    index = data["assetIndex"]
    target = os.path.join(assets, "indexes", index["id"] + ".json")
    if not os.path.exists(target):
        urllib.request.urlretrieve(index["url"], target)
    idx = json.load(open(target, encoding="utf-8"))
    print(path, "index", index["id"], "objects", len(idx["objects"]))
    for obj in idx["objects"].values():
        needed[obj["hash"]] = obj

missing = [h for h in needed if not os.path.exists(os.path.join(assets, "objects", h[:2], h))]
print("total objects", len(needed), "missing", len(missing))


def fetch(h):
    path = os.path.join(assets, "objects", h[:2], h)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    try:
        urllib.request.urlretrieve(f"https://resources.download.minecraft.net/{h[:2]}/{h}", path)
        return 1
    except Exception:
        return 0


with ThreadPoolExecutor(max_workers=24) as pool:
    done = sum(pool.map(fetch, missing))
print("downloaded", done)
