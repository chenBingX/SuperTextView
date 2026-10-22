package com.coorchice.library;

import com.coorchice.library.gifdecoder.GifDecoder;
import com.coorchice.library.utils.STVUtils;
import org.junit.Test;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

/** GIF identification must work on a host/ABI with no GifLib installed. */
public class GifSignatureTest {
    @Test public void recognizesBothGifSignaturesWithoutNativeCode() {
        for (String signature : new String[] {"GIF87a", "GIF89a"}) {
            byte[] bytes = signature.getBytes(StandardCharsets.US_ASCII);
            assertTrue(STVUtils.isGif(bytes));
            assertTrue(GifDecoder.isGif(bytes));
        }
    }

    @Test public void rejectsTruncatedOrInvalidSignaturesWithoutNativeCode() {
        for (String signature : new String[] {"", "G", "GIF", "GIF89", "GIF88a", "gif89a", "GIF89b", "PNG89a"}) {
            byte[] bytes = signature.getBytes(StandardCharsets.US_ASCII);
            assertFalse(STVUtils.isGif(bytes));
            assertFalse(GifDecoder.isGif(bytes));
        }
    }

    @Test public void rejectsPngAndNullWithoutLoadingNativeLibrary() {
        byte[] png = {(byte) 0x89, 'P', 'N', 'G', 13, 10, 26, 10};
        assertFalse(STVUtils.isGif(png));
        assertFalse(GifDecoder.isGif(png));
        assertFalse(STVUtils.isGif(null));
        assertFalse(GifDecoder.isGif(null));
        assertFalse(GifDecoder.isGif(new Object()));
    }

    @Test public void preservesCaseInsensitiveFileSuffixDetection() {
        assertTrue(GifDecoder.isGif("/tmp/image.GIF"));
        assertFalse(GifDecoder.isGif("/tmp/image.png"));
    }
}
