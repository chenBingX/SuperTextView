#!/usr/bin/env python3
"""Install the native smoke APK on an explicitly selected device and assert results."""
import argparse
import os
import pathlib
import re
import subprocess

ROOT = pathlib.Path(__file__).resolve().parents[1]


def verify_output(text, minimum=47):
    match = re.search(r'Native smoke: passed=(\d+) failed=(\d+) total=(\d+)', text)
    if match is None:
        raise ValueError('instrumentation did not finish (possibly a native crash)')
    passed, failed, total = map(int, match.groups())
    if failed or total < minimum or passed != total or not re.search(r'INSTRUMENTATION_CODE:\s*-1\b', text):
        raise ValueError('native smoke tests failed or did not execute the full suite')
    return total


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument('--adb', default='adb')
    p.add_argument('--serial', default=os.environ.get('ANDROID_SERIAL'))
    p.add_argument('--page-size', type=int, choices=(4096, 16384), required=True)
    p.add_argument('--apk', type=pathlib.Path, default=ROOT / 'native-tests/build/outputs/apk/debug/SuperTextView-native-tests-debug.apk')
    p.add_argument('--log', type=pathlib.Path, default=ROOT / 'native-build/native-smoke.txt')
    args = p.parse_args()
    if not args.serial:
        p.error('pass --serial or ANDROID_SERIAL; do not implicitly select a user device')
    adb = [args.adb, '-s', args.serial]
    subprocess.run(adb + ['install', '-r', str(args.apk)], check=True, timeout=180)
    command = adb + ['shell', 'am', 'instrument', '-w', '-r', '-e', 'requirePageSize', str(args.page_size),
                     'com.coorchice.nativetests/com.coorchice.nativetests.NativeSmokeInstrumentation']
    result = subprocess.run(command, text=True, stdout=subprocess.PIPE,
                            stderr=subprocess.STDOUT, timeout=180)
    args.log.parent.mkdir(parents=True, exist_ok=True)
    args.log.write_text(result.stdout)
    print(result.stdout)
    if result.returncode:
        raise RuntimeError('adb instrumentation exited with ' + str(result.returncode))
    total = verify_output(result.stdout)
    print('Verified {} native tests on {} with {}-byte pages'.format(total, args.serial, args.page_size))


if __name__ == '__main__':
    main()
