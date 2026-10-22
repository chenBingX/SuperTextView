#!/usr/bin/env python3
"""Check the exact Maven publication: production-only AAR, JNI bytes, POM and sources."""
import argparse
import hashlib
import io
import json
import pathlib
import struct
import sys
import xml.etree.ElementTree as ET
import zipfile

from check_native_binaries import inspect_aar

ROOT = pathlib.Path(__file__).resolve().parents[1]
GROUP = 'com.github.chenBingX'
ARTIFACT = 'SuperTextView'


def check_publication(repository, version, source_root=ROOT):
    folder = pathlib.Path(repository) / GROUP.replace('.', '/') / ARTIFACT / version
    stem = '{}-{}'.format(ARTIFACT, version)
    aar = folder / (stem + '.aar')
    # Python 3.5 file APIs do not accept pathlib.Path (JitPack legacy image).
    pom = ET.parse(str(folder / (stem + '.pom'))).getroot()
    ns = {'m': 'http://maven.apache.org/POM/4.0.0'}
    for field, expected in [('groupId', GROUP), ('artifactId', ARTIFACT),
                            ('version', version), ('packaging', 'aar')]:
        if pom.findtext('m:' + field, namespaces=ns) != expected:
            raise ValueError('incorrect POM ' + field)
    native = inspect_aar(aar)
    with zipfile.ZipFile(str(aar)) as archive:
        if any('remaining_' in name for name in archive.namelist()):
            raise ValueError('regression resource leaked into production AAR')
        manifest = ET.fromstring(archive.read('AndroidManifest.xml'))
        sdk = manifest.find('uses-sdk')
        android = '{http://schemas.android.com/apk/res/android}'
        if sdk is None or sdk.get(android + 'minSdkVersion') != '19':
            raise ValueError('production minimum SDK must remain 19')
        application = manifest.find('application')
        if application is not None and any(application.get(android + name) is not None
                                           for name in ('label', 'allowBackup')):
            raise ValueError('library must not override host application settings')
        rules = archive.read('proguard.txt').decode('utf-8')
        if '-keep class com.coorchice.library.gifdecoder.JNI { *; }' not in rules:
            raise ValueError('JNI consumer keep rule missing')
        with zipfile.ZipFile(io.BytesIO(archive.read('classes.jar'))) as classes:
            required = ['com/coorchice/library/SuperTextView.class',
                        'com/coorchice/library/gifdecoder/JNI.class', 'COPYING.giflib']
            if not set(required).issubset(classes.namelist()):
                raise ValueError('production classes or giflib attribution missing')
            if any('Regression' in name or 'nativetests/' in name for name in classes.namelist()):
                raise ValueError('test classes leaked into production AAR')
            if classes.read('COPYING.giflib') != (source_root / 'library/src/main/cpp/COPYING.giflib').read_bytes():
                raise ValueError('giflib attribution differs from source')
        for abi, entry in native.items():
            source = source_root / 'library/src/main/jniLibs' / abi / 'libGifLib.so'
            if entry['sha256'] != hashlib.sha256(source.read_bytes()).hexdigest():
                raise ValueError('packaged JNI differs from validated source: ' + abi)
    with zipfile.ZipFile(str(folder / (stem + '-sources.jar'))) as sources:
        if 'com/coorchice/library/SuperTextView.java' not in sources.namelist():
            raise ValueError('production Java sources missing')
        if any('RegressionTest' in name or 'DeviceRegression' in name for name in sources.namelist()):
            raise ValueError('test sources leaked into publication')
    return {'coordinate': '{}:{}:{}'.format(GROUP, ARTIFACT, version),
            'aarBytes': aar.stat().st_size, 'aarSha256': hashlib.sha256(aar.read_bytes()).hexdigest(),
            'native': native}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--repository', type=pathlib.Path, default=pathlib.Path.home() / '.m2/repository')
    parser.add_argument('--version', required=True)
    args = parser.parse_args()
    try:
        print(json.dumps(check_publication(args.repository, args.version), indent=2))
        return 0
    except (OSError, ValueError, KeyError, struct.error, RuntimeError, ET.ParseError, zipfile.BadZipFile) as error:
        print('PUBLICATION: FAIL: {}'.format(error), file=sys.stderr)
        return 1


if __name__ == '__main__':
    sys.exit(main())
