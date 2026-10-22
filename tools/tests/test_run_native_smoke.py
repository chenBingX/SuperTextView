import pathlib
import sys
import unittest
sys.path.insert(0, str(pathlib.Path(__file__).resolve().parents[1]))
from run_native_smoke import verify_output


class SmokeReceiptTest(unittest.TestCase):
    def test_complete_success(self):
        self.assertEqual(47, verify_output('Native smoke: passed=47 failed=0 total=47\nINSTRUMENTATION_CODE: -1'))

    def test_native_crash_without_summary(self):
        with self.assertRaisesRegex(ValueError, 'did not finish'):
            verify_output('START openBytesTruncated\nProcess crashed.')

    def test_shell_zero_exit_does_not_hide_assertion_failure(self):
        with self.assertRaises(ValueError):
            verify_output('Native smoke: passed=46 failed=1 total=47\nINSTRUMENTATION_CODE: 0')

    def test_zero_or_partial_test_run_is_not_success(self):
        for count in (0, 1, 46):
            with self.assertRaises(ValueError):
                verify_output('Native smoke: passed={0} failed=0 total={0}\nINSTRUMENTATION_CODE: -1'.format(count))

    def test_instrumentation_failure_code(self):
        with self.assertRaises(ValueError):
            verify_output('Native smoke: passed=47 failed=0 total=47\nINSTRUMENTATION_CODE: 0')


if __name__ == '__main__':
    unittest.main()
