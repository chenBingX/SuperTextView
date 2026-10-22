#!/usr/bin/env python3
"""Rebuild all shipped GifLib ABIs from source; preserve API 19 for 32-bit."""
import argparse
import json
import os
import pathlib
import shutil
import subprocess

ROOT = pathlib.Path(__file__).resolve().parents[1]
ABIS = ('armeabi-v7a', 'arm64-v8a', 'x86', 'x86_64')


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument('--ndk', type=pathlib.Path, default=os.environ.get('ANDROID_NDK_HOME'))
    p.add_argument('--cmake', default=shutil.which('cmake') or 'cmake')
    p.add_argument('--build-dir', type=pathlib.Path, default=ROOT / 'native-build')
    p.add_argument('--output-dir', type=pathlib.Path, default=ROOT / 'library/src/main/jniLibs')
    p.add_argument('--abis', nargs='+', choices=ABIS, default=ABIS)
    args = p.parse_args()
    if args.ndk is None:
        p.error('set ANDROID_NDK_HOME or pass --ndk (NDK r25 preserves API 19)')
    ndk = args.ndk.expanduser().resolve()
    minimum = json.loads((ndk / 'meta/platforms.json').read_text())['min']
    if minimum > 19 and any(abi in ('armeabi-v7a', 'x86') for abi in args.abis):
        p.error('this NDK drops API 19; use NDK r25 for the complete ABI set')
    print((ndk / 'source.properties').read_text(), flush=True)
    # Build and validate every requested ABI before replacing any checked-in binary.
    artifacts = []
    for abi in args.abis:
        build = args.build_dir.resolve() / abi
        api = 21 if abi in ('arm64-v8a', 'x86_64') else 19
        build.mkdir(parents=True, exist_ok=True)
        subprocess.run([args.cmake, str(ROOT / 'library/src/main/cpp'),
                        '-DCMAKE_TOOLCHAIN_FILE=' + str(ndk / 'build/cmake/android.toolchain.cmake'),
                        '-DANDROID_ABI=' + abi, '-DANDROID_PLATFORM=android-' + str(api),
                        '-DANDROID_STL=c++_static', '-DCMAKE_BUILD_TYPE=Release'], cwd=build, check=True)
        subprocess.run([args.cmake, '--build', str(build), '--', '-j4'], check=True)
        artifact = build / 'libGifLib.so'
        tools = list((ndk / 'toolchains/llvm/prebuilt').glob('*/bin/llvm-strip'))
        if len(tools) != 1:
            raise RuntimeError('cannot identify NDK llvm-strip')
        subprocess.run([str(tools[0]), '--strip-unneeded', str(artifact)], check=True)
        from check_native_binaries import inspect, ABIS as expected
        bits, machine, _ = inspect(artifact)
        if (bits, machine) != expected[abi]:
            raise RuntimeError('wrong architecture: ' + abi)
        artifacts.append((abi, artifact))
    for abi, artifact in artifacts:
        target = args.output_dir.resolve() / abi / 'libGifLib.so'
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(artifact, target)
        print('Built {}: {} bytes'.format(abi, target.stat().st_size), flush=True)


if __name__ == '__main__':
    main()
