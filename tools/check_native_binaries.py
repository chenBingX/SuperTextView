#!/usr/bin/env python3
"""Check ELF architecture, 16 KB LOAD congruence and RELRO/writable-page safety."""
import argparse
import hashlib
import pathlib
import struct
import sys
import zipfile

ABIS = {'armeabi-v7a': (1, 40), 'arm64-v8a': (2, 183), 'x86': (1, 3), 'x86_64': (2, 62)}
PAGE = 16384


def inspect(path):
    return inspect_bytes(pathlib.Path(path).read_bytes())


def inspect_bytes(data):
    if len(data) < 52 or data[:4] != b'\x7fELF' or data[5] != 1:
        raise ValueError('not a little-endian ELF file')
    bits = data[4]
    if bits == 2:
        header = struct.unpack_from('<HHIQQQIHHHHHH', data, 16)
        fmt = '<IIQQQQQQ'
    elif bits == 1:
        header = struct.unpack_from('<HHIIIIIHHHHHH', data, 16)
        fmt = '<IIIIIIII'
    else:
        raise ValueError('unknown ELF class')
    phoff, phsize, count = header[4], header[8], header[9]
    if phsize < struct.calcsize(fmt) or phoff + phsize * count > len(data):
        raise ValueError('truncated program header table')
    segments = []
    for i in range(count):
        p = struct.unpack_from(fmt, data, phoff + phsize * i)
        if bits == 2:
            kind, flags, offset, addr, _, filesz, memsz, align = p
        else:
            kind, offset, addr, _, filesz, memsz, flags, align = p
        segments.append(dict(kind=kind, flags=flags, offset=offset, addr=addr,
                             filesz=filesz, memsz=memsz, align=align))
    loads = [p for p in segments if p['kind'] == 1]
    if not loads:
        raise ValueError('ELF has no LOAD segments')
    for p in loads:
        if p['filesz'] > p['memsz'] or p['offset'] + p['filesz'] > len(data):
            raise ValueError('invalid LOAD bounds')
        if p['align'] < PAGE or (p['addr'] - p['offset']) % PAGE:
            raise ValueError('LOAD is not 16 KB aligned/congruent')
    for r in [p for p in segments if p['kind'] == 0x6474e552]:
        start, end = r['addr'], r['addr'] + r['memsz']
        protected_start, protected_end = start // PAGE * PAGE, (end + PAGE - 1) // PAGE * PAGE
        for p in loads:
            if not p['flags'] & 2:
                continue
            left, right = p['addr'], p['addr'] + p['memsz']
            if max(left, protected_start) < min(right, start) or max(left, end) < min(right, protected_end):
                raise ValueError('RELRO shares a 16 KB page with writable LOAD data')
    return bits, header[1], [hex(p['align']) for p in loads]


def inspect_aar(path):
    """Validate the actual AAR payload, not just its ZIP CRC or source directory."""
    expected = {'jni/{}/libGifLib.so'.format(abi): abi for abi in ABIS}
    result = {}
    # JitPack's legacy image uses Python 3.5, before ZipFile's PathLike support.
    with zipfile.ZipFile(str(path) if isinstance(path, pathlib.PurePath) else path) as archive:
        entries = [i for i in archive.infolist() if i.filename.endswith('/libGifLib.so')]
        names = [i.filename for i in entries]
        if len(names) != len(set(names)):
            raise ValueError('duplicate libGifLib.so ZIP entry')
        if set(names) != set(expected):
            raise ValueError('wrong native ABI entries: missing={}, unexpected={}'.format(
                sorted(set(expected) - set(names)), sorted(set(names) - set(expected))))
        bad_entry = archive.testzip()
        if bad_entry:
            raise ValueError('ZIP CRC failed: ' + bad_entry)
        for entry in entries:
            if entry.file_size > 64 * 1024 * 1024:
                raise ValueError('unexpectedly large native library: ' + entry.filename)
            data = archive.read(entry)
            abi = expected[entry.filename]
            bits, machine, _ = inspect_bytes(data)
            if (bits, machine) != ABIS[abi]:
                raise ValueError('wrong ELF architecture for ' + abi)
            result[abi] = {'bytes': len(data), 'sha256': hashlib.sha256(data).hexdigest()}
    return result


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('root', nargs='?', type=pathlib.Path)
    parser.add_argument('--aar', type=pathlib.Path, help='check the final four-ABI AAR')
    args = parser.parse_args()
    if args.aar:
        if args.root is not None:
            parser.error('root and --aar cannot be combined')
        try:
            for abi, receipt in sorted(inspect_aar(args.aar).items()):
                print('{}: PASS ({} bytes, SHA-256 {})'.format(
                    abi, receipt['bytes'], receipt['sha256']))
            return 0
        except (OSError, ValueError, struct.error, zipfile.BadZipFile, RuntimeError) as error:
            print('AAR: FAIL: {}'.format(error), file=sys.stderr)
            return 1
    args.root = args.root or pathlib.Path(__file__).resolve().parents[1] / 'library/src/main/jniLibs'
    failures = []
    for abi, expected in ABIS.items():
        path = args.root / abi / 'libGifLib.so'
        try:
            bits, machine, alignments = inspect(path)
            if (bits, machine) != expected:
                raise ValueError('wrong ELF architecture for ' + abi)
            print('{}: PASS (LOAD {}, RELRO safe)'.format(abi, ','.join(alignments)))
        except (OSError, ValueError, struct.error) as error:
            failures.append(abi)
            print('{}: FAIL: {}'.format(abi, error), file=sys.stderr)
    return bool(failures)


if __name__ == '__main__':
    sys.exit(main())
