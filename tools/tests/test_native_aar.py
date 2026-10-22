import pathlib
import struct
import subprocess
import sys
import tempfile
import unittest
import warnings
import zipfile

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parents[1]))
from check_native_binaries import ABIS, inspect_aar


def elf(abi):
    bits, machine = ABIS[abi]
    data = bytearray(512)
    data[:16] = b'\x7fELF' + bytes([bits, 1, 1]) + bytes(9)
    if bits == 2:
        struct.pack_into('<HHIQQQIHHHHHH', data, 16,
                         3, machine, 1, 0, 64, 0, 0, 64, 56, 1, 0, 0, 0)
        struct.pack_into('<IIQQQQQQ', data, 64, 1, 4, 0, 0, 0, 512, 4096, 16384)
    else:
        struct.pack_into('<HHIIIIIHHHHHH', data, 16,
                         3, machine, 1, 0, 52, 0, 0, 52, 32, 1, 0, 0, 0)
        struct.pack_into('<IIIIIIII', data, 52, 1, 0, 0, 0, 512, 4096, 4, 16384)
    return bytes(data)


class NativeAarTest(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.path = pathlib.Path(self.directory.name) / 'library.aar'
        self.entries = {'jni/{}/libGifLib.so'.format(abi): elf(abi) for abi in ABIS}

    def write(self, entries=None):
        with zipfile.ZipFile(self.path, 'w') as archive:
            for name, data in (entries or self.entries).items():
                archive.writestr(name, data)

    def test_four_correct_abis_with_sizes_and_hashes(self):
        self.write()
        result = inspect_aar(self.path)
        self.assertEqual(set(ABIS), set(result))
        self.assertTrue(all(r['bytes'] == 512 and len(r['sha256']) == 64
                            for r in result.values()))

    def test_crc_valid_but_empty_so_is_rejected(self):
        self.entries['jni/armeabi-v7a/libGifLib.so'] = b''
        self.write()
        with zipfile.ZipFile(self.path) as archive:
            self.assertIsNone(archive.testzip())
        with self.assertRaises(ValueError):
            inspect_aar(self.path)

    def test_truncated_elf_is_rejected(self):
        self.entries['jni/arm64-v8a/libGifLib.so'] = b'\x7fELF'
        self.write()
        with self.assertRaises(ValueError):
            inspect_aar(self.path)

    def test_wrong_architecture_is_rejected(self):
        self.entries['jni/x86_64/libGifLib.so'] = elf('arm64-v8a')
        self.write()
        with self.assertRaisesRegex(ValueError, 'wrong ELF architecture'):
            inspect_aar(self.path)

    def test_missing_abi_is_rejected(self):
        del self.entries['jni/x86/libGifLib.so']
        self.write()
        with self.assertRaisesRegex(ValueError, 'missing='):
            inspect_aar(self.path)

    def test_unexpected_abi_is_rejected(self):
        self.entries['jni/armeabi/libGifLib.so'] = elf('armeabi-v7a')
        self.write()
        with self.assertRaisesRegex(ValueError, 'unexpected='):
            inspect_aar(self.path)

    def test_duplicate_entry_is_rejected(self):
        self.write()
        with warnings.catch_warnings():
            warnings.simplefilter('ignore', UserWarning)
            with zipfile.ZipFile(self.path, 'a') as archive:
                archive.writestr('jni/x86/libGifLib.so', elf('x86'))
        with self.assertRaisesRegex(ValueError, 'duplicate'):
            inspect_aar(self.path)

    def test_non_native_entry_crc_is_checked_too(self):
        self.entries['assets/payload.txt'] = b'CRC-STABLE-PAYLOAD'
        self.write()
        data = self.path.read_bytes().replace(b'CRC-STABLE-PAYLOAD', b'BAD-STABLE-PAYLOAD')
        self.path.write_bytes(data)
        with self.assertRaisesRegex(ValueError, 'CRC'):
            inspect_aar(self.path)

    def test_truncated_zip_is_rejected(self):
        self.write()
        self.path.write_bytes(self.path.read_bytes()[:-22])
        with self.assertRaises(zipfile.BadZipFile):
            inspect_aar(self.path)

    def test_cli_bad_aar_returns_failure(self):
        self.entries['jni/armeabi-v7a/libGifLib.so'] = b''
        self.write()
        tool = pathlib.Path(__file__).resolve().parents[1] / 'check_native_binaries.py'
        result = subprocess.run([sys.executable, str(tool), '--aar', str(self.path)],
                                capture_output=True, text=True)
        self.assertEqual(1, result.returncode)
        self.assertIn('AAR: FAIL', result.stderr)


if __name__ == '__main__':
    unittest.main()
