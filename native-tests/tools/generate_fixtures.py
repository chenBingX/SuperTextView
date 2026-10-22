#!/usr/bin/env python3
"""Write tiny GIF fixtures from GIF89a block definitions, not decoder output.

No third-party modules are needed. --check verifies checked-in bytes unchanged.
The pixel oracle is deliberately hand-written in the Java runner and README.
"""
import argparse
from pathlib import Path
import struct

ASSETS = Path(__file__).resolve().parents[1] / 'src/main/assets'


def word(value):
    return struct.pack('<H', value)


def image(left, top, width, height, indices, local_palette=b''):
    # Clear before every pixel keeps LZW codes three bits wide. This is a small
    # standards-based encoder, not an implementation of the native renderer.
    codes = []
    for index in indices:
        codes.extend((4, index))
    codes.append(5)
    packed = sum(code << (3 * i) for i, code in enumerate(codes))
    payload = packed.to_bytes((3 * len(codes) + 7) // 8, 'little')
    return (b',' + word(left) + word(top) + word(width) + word(height)
            + bytes([0x81 if local_palette else 0]) + local_palette
            + b'\x02' + bytes([len(payload)]) + payload + b'\x00')


def fixtures():
    # Global indices: transparent/black, red, green, blue.
    palette = bytes.fromhex('000000 ff0000 00ff00 0000ff')
    screen = word(3) + word(2) + b'\x81\x00\x00' + palette
    # Graphic control: leave in place, transparency index 0, delay in 1/100 s.
    gce100 = bytes.fromhex('21 f9 04 05 0a 00 00 00')
    gce200 = bytes.fromhex('21 f9 04 05 14 00 00 00')
    first = image(0, 0, 3, 2, [1, 0, 3, 2, 1, 0])
    # A different local palette makes using the global palette observably wrong.
    local_palette = bytes.fromhex('000000 0000ff ff0000 00ff00')
    second = image(1, 0, 2, 2, [3, 0, 0, 1], local_palette)
    animated = b'GIF89a' + screen + gce100 + first + gce200 + second + b';'
    # No graphics-control extension: all pixels, including black, are opaque.
    plain = (b'GIF87a' + screen
             + image(0, 0, 3, 2, [1, 0, 3, 2, 1, 0]) + b';')
    return {'known-pixels.gif': animated, 'plain-87a.gif': plain}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    ASSETS.mkdir(parents=True, exist_ok=True)
    for name, expected in fixtures().items():
        path = ASSETS / name
        if args.check:
            if path.read_bytes() != expected:
                raise SystemExit('fixture differs: ' + str(path))
            print('OK', name, len(expected), 'bytes')
        else:
            path.write_bytes(expected)
            print('WROTE', name, len(expected), 'bytes')


if __name__ == '__main__':
    main()
