#!/usr/bin/env python3
"""Optional independent host-side image oracle. Requires Pillow, not the APK."""
from hashlib import sha256
from pathlib import Path

from PIL import Image

ASSETS = Path(__file__).resolve().parents[1] / 'src/main/assets'
R = (255, 0, 0, 255)
G = (0, 255, 0, 255)
B = (0, 0, 255, 255)
T = (0, 0, 0, 0)
K = (0, 0, 0, 255)
# These expected arrays describe the intended image, not decoded native results.
EXPECTED = {
    'known-pixels.gif': [
        (100, (0, 0, 3, 2), [R, T, B, G, R, T]),
        (200, (1, 0, 3, 2), [R, G, B, G, R, B]),
    ],
    'plain-87a.gif': [(0, (0, 0, 3, 2), [R, K, B, G, R, K])],
}

for name, frames in EXPECTED.items():
    path = ASSETS / name
    with Image.open(path) as image:
        assert image.size == (3, 2), (name, image.size)
        assert image.n_frames == len(frames), (name, image.n_frames)
        for index, (duration, extent, pixels) in enumerate(frames):
            image.seek(index)
            assert image.info.get('duration', 0) == duration, (name, index, image.info)
            assert image.dispose_extent == extent, (name, index, image.dispose_extent)
            rgba = image.convert('RGBA')
            actual = [rgba.getpixel((x, y)) for y in range(2) for x in range(3)]
            assert actual == pixels, (name, index, actual)
            print('PASS', name, 'frame', index, 'duration', duration, 'pixels', actual)
    print('SHA256', sha256(path.read_bytes()).hexdigest(), name)
print('Independent Pillow fixture checks passed')
