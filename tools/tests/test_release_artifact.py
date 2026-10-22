import io
import pathlib
import sys
import tempfile
import unittest
from unittest import mock
import zipfile
import xml.etree.ElementTree as ET

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parents[1]))
from check_release_artifact import check_publication
from test_native_aar import ABIS, elf


class ReleaseArtifactTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.root = pathlib.Path(self.tmp.name)
        self.version = 'v3.2.7'
        self.folder = self.root / 'repo/com/github/chenBingX/SuperTextView' / self.version
        self.folder.mkdir(parents=True)
        self.stem = self.folder / ('SuperTextView-' + self.version)
        (self.root / 'library/src/main/cpp').mkdir(parents=True)
        (self.root / 'library/src/main/cpp/COPYING.giflib').write_bytes(b'giflib attribution')
        self.entries = {'AndroidManifest.xml': b'<manifest xmlns:android="http://schemas.android.com/apk/res/android"><uses-sdk android:minSdkVersion="19" /></manifest>'}
        for abi in ABIS:
            data = elf(abi)
            path = self.root / 'library/src/main/jniLibs' / abi / 'libGifLib.so'
            path.parent.mkdir(parents=True); path.write_bytes(data)
            self.entries['jni/' + abi + '/libGifLib.so'] = data
        classes = io.BytesIO()
        with zipfile.ZipFile(classes, 'w') as jar:
            for name in ['com/coorchice/library/SuperTextView.class',
                         'com/coorchice/library/gifdecoder/JNI.class']:
                jar.writestr(name, b'class')
            jar.writestr('COPYING.giflib', b'giflib attribution')
        self.entries['classes.jar'] = classes.getvalue()
        self.entries['proguard.txt'] = b'-keep class com.coorchice.library.gifdecoder.JNI { *; }'
        self.pom = '<project xmlns="http://maven.apache.org/POM/4.0.0"><groupId>com.github.chenBingX</groupId><artifactId>SuperTextView</artifactId><version>v3.2.7</version><packaging>aar</packaging></project>'
        with zipfile.ZipFile(str(self.stem) + '-sources.jar', 'w') as jar:
            jar.writestr('com/coorchice/library/SuperTextView.java', 'source')

    def check(self):
        pathlib.Path(str(self.stem) + '.pom').write_text(self.pom)
        with zipfile.ZipFile(str(self.stem) + '.aar', 'w') as archive:
            for name, data in self.entries.items(): archive.writestr(name, data)
        return check_publication(self.root / 'repo', self.version, self.root)

    def test_valid_production_publication(self):
        self.assertEqual('com.github.chenBingX:SuperTextView:v3.2.7', self.check()['coordinate'])

    def test_legacy_python_file_apis_receive_strings_not_paths(self):
        # Python 3.5's ZipFile/ElementTree do not implement os.PathLike support.
        real_zip, real_parse = zipfile.ZipFile, ET.parse

        def old_zip(file, *args, **kwargs):
            if isinstance(file, pathlib.PurePath):
                raise TypeError('legacy ZipFile rejects Path')
            return real_zip(file, *args, **kwargs)

        def old_parse(file, *args, **kwargs):
            if isinstance(file, pathlib.PurePath):
                raise TypeError('legacy ElementTree rejects Path')
            return real_parse(file, *args, **kwargs)

        with mock.patch.object(zipfile, 'ZipFile', side_effect=old_zip), \
                mock.patch.object(ET, 'parse', side_effect=old_parse):
            self.assertEqual('com.github.chenBingX:SuperTextView:v3.2.7', self.check()['coordinate'])

    def test_wrong_pom_version_fails(self):
        self.pom = self.pom.replace('<version>v3.2.7', '<version>wrong')
        with self.assertRaisesRegex(ValueError, 'POM version'): self.check()

    def test_test_resource_leak_fails(self):
        self.entries['res/layout/remaining_font.xml'] = b'fixture'
        with self.assertRaisesRegex(ValueError, 'regression resource'): self.check()

    def test_missing_jni_rule_fails(self):
        self.entries['proguard.txt'] = b''
        with self.assertRaisesRegex(ValueError, 'consumer keep rule'): self.check()

    def test_native_source_mismatch_fails(self):
        path = self.root / 'library/src/main/jniLibs/arm64-v8a/libGifLib.so'
        path.write_bytes(b'different source')
        with self.assertRaisesRegex(ValueError, 'differs from validated source'): self.check()

    def test_raised_minimum_sdk_fails(self):
        self.entries['AndroidManifest.xml'] = self.entries['AndroidManifest.xml'].replace(b'"19"', b'"21"')
        with self.assertRaisesRegex(ValueError, 'minimum SDK'): self.check()

    def test_missing_native_attribution_fails(self):
        classes = io.BytesIO()
        with zipfile.ZipFile(classes, 'w') as jar:
            jar.writestr('com/coorchice/library/SuperTextView.class', b'class')
            jar.writestr('com/coorchice/library/gifdecoder/JNI.class', b'class')
        self.entries['classes.jar'] = classes.getvalue()
        with self.assertRaisesRegex(ValueError, 'attribution missing'): self.check()

    def test_host_application_override_fails(self):
        self.entries['AndroidManifest.xml'] = self.entries['AndroidManifest.xml'].replace(
            b'</manifest>', b'<application android:allowBackup="true" /></manifest>')
        with self.assertRaisesRegex(ValueError, 'host application settings'): self.check()

    def test_missing_sources_fails(self):
        pathlib.Path(str(self.stem) + '-sources.jar').unlink()
        with self.assertRaises(FileNotFoundError): self.check()


if __name__ == '__main__':
    unittest.main()
