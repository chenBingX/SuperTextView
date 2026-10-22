import pathlib
import struct
import sys
import tempfile
import unittest

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parents[1]))
from check_native_binaries import inspect


def elf64(segments):
    data = bytearray(0x800)
    data[:16] = b'\x7fELF\x02\x01\x01' + bytes(9)
    struct.pack_into('<HHIQQQIHHHHHH', data, 16, 3, 183, 1, 0, 64, 0, 0, 64, 56, len(segments), 0, 0, 0)
    for i, (kind, flags, offset, addr, size, align) in enumerate(segments):
        struct.pack_into('<IIQQQQQQ', data, 64 + 56 * i, kind, flags, offset, addr, 0, 0, size, align)
    return data


class AlignmentTest(unittest.TestCase):
    def check(self, segments):
        with tempfile.TemporaryDirectory() as directory:
            p = pathlib.Path(directory) / 'test.so'
            p.write_bytes(elf64(segments))
            return inspect(p)

    def test_aligned_loads_and_relro(self):
        self.check([(1, 5, 0, 0, 0x1000, 0x4000), (1, 6, 0, 0x4000, 0x5000, 0x4000),
                    (0x6474e552, 4, 0, 0x4000, 0x4000, 1)])

    def test_relro_alone_in_load_need_not_fill_page(self):
        self.check([(1, 5, 0, 0, 0x1000, 0x4000), (1, 6, 0, 0x4000, 0x1000, 0x4000),
                    (0x6474e552, 4, 0, 0x4000, 0x1000, 1), (1, 6, 0, 0x8000, 0x100, 0x4000)])

    def test_old_64k_load_with_shared_writable_data_is_rejected(self):
        with self.assertRaisesRegex(ValueError, 'RELRO shares'):
            self.check([(1, 5, 0, 0, 0x30000, 0x10000), (1, 6, 0, 0x40000, 0xe6b0, 0x10000),
                        (0x6474e552, 4, 0, 0x4adf0, 0x3210, 1)])

    def test_writable_bytes_before_relro_are_rejected(self):
        with self.assertRaisesRegex(ValueError, 'RELRO shares'):
            self.check([(1, 6, 0, 0x4000, 0x4000, 0x4000), (0x6474e552, 4, 0, 0x4100, 0x3f00, 1)])

    def test_4k_load_rejected(self):
        with self.assertRaisesRegex(ValueError, 'LOAD is not'):
            self.check([(1, 5, 0, 0, 0x1000, 0x1000)])

    def test_address_offset_mismatch_rejected(self):
        with self.assertRaisesRegex(ValueError, 'LOAD is not'):
            self.check([(1, 5, 0, 0x1000, 0x1000, 0x4000)])

    def test_missing_load_rejected(self):
        with self.assertRaisesRegex(ValueError, 'no LOAD'):
            self.check([])

    def test_truncated_elf_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            p = pathlib.Path(directory) / 'broken.so'
            p.write_bytes(b'\x7fELF')
            with self.assertRaisesRegex(ValueError, 'not a little-endian ELF'):
                inspect(p)


if __name__ == '__main__':
    unittest.main()
