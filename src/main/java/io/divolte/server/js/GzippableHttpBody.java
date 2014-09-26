package io.divolte.server.js;

import java.nio.ByteBuffer;
import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Value class for a HTTP body and its accompanying ETag.
 *
 * In addition, a Gzipped variant is available, so long as it is smaller
 * than the uncompressed version.
 */
@ParametersAreNonnullByDefault
public class GzippableHttpBody extends HttpBody {
    private static final Logger logger = LoggerFactory.getLogger(GzippableHttpBody.class);

    private final Optional<HttpBody> gzippedBody;

    public GzippableHttpBody(final ByteBuffer data, final String eTag) {
        super(data, eTag);
        logger.debug("Compressing resource.");
        final Optional<ByteBuffer> gzippedData = Gzip.compress(data);
        if (gzippedData.isPresent()) {
            logger.info("Compressed resource: {} -> {}",
                        data.remaining(), gzippedData.get().remaining());
            gzippedBody = Optional.of(new HttpBody(gzippedData.get(), "\"gz+" + eTag.substring(1)));
        } else {
            logger.info("Resource not compressable.");
            gzippedBody = Optional.empty();
        }
    }

    public Optional<HttpBody> getGzippedBody() {
        return gzippedBody;
    }
}
