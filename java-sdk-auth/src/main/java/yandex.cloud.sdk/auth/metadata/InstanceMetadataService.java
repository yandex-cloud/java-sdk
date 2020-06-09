package yandex.cloud.sdk.auth.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Retrieves metadata from remote metadata server. HTTP is used to retrieve metadata.
 * All metadata keys are prefixed with {@value METADATA_PATH_PREFIX}.
 */
public class InstanceMetadataService {
    /**
     * Default URL of metadata server accessible from VMs
     */
    public static final String METADATA_SERVER_URL = "http://169.254.169.254";
    /**
     * Default prefix of all metadata keys
     */
    private static final String METADATA_PATH_PREFIX = "/computeMetadata/v1/";

    /**
     * Configuration for {@link HttpURLConnection} used to retrieve metadata
     */
    private final HttpConnectionConfig config;
    /**
     * URL of metadata server including prefix
     */
    private final URI metadataServerUrl;

    /**
     * Constructs a <code>InstanceMetadataService</code> with default <code>HttpConnectionConfig</code>
     */
    public InstanceMetadataService() {
        this(HttpConnectionConfig.DEFAULT);
    }

    /**
     * Constructs a <code>InstanceMetadataService</code> with given <code>HttpConnectionConfig</code>
     * @param config configuration for {@link HttpURLConnection} used to retrieve metadata
     */
    public InstanceMetadataService(HttpConnectionConfig config) {
        this(config, METADATA_SERVER_URL);
    }

    /**
     * Constructs a <code>InstanceMetadataService</code> with given <code>HttpConnectionConfig</code> and metadata server URL (without prefix)
     * @param config configuration for {@link HttpURLConnection} used to retrieve metadata
     * @param metadataServerUrl URL of metadata server (without prefix)
     */
    public InstanceMetadataService(HttpConnectionConfig config, String metadataServerUrl) {
        this.config = config;

        // check that URL is correct
        try {
            URL url = new URL(metadataServerUrl + METADATA_PATH_PREFIX);
            this.metadataServerUrl = url.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new yandex.cloud.sdk.auth.metadata.MalformedURLException(metadataServerUrl, e);
        }
    }

    /**
     * Retrieves value with the given key from metadata.
     * @param key metadata key, prefix {@value METADATA_PATH_PREFIX} will be added
     * @return retrieved metadata value
     * @throws IOException if an I/O exception occurs during request
     */
    public String getValue(String key) throws IOException {
        HttpURLConnection connection = null;
        try {
            URI fullUrl = null;

            try {
                fullUrl = metadataServerUrl.resolve(key);
                connection = (HttpURLConnection) fullUrl.toURL().openConnection();
            } catch (MalformedURLException e) {
                throw new yandex.cloud.sdk.auth.metadata.MalformedURLException(fullUrl.getPath());
            }

            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                throw new MetadataException(key, e);
            }
            connection.setConnectTimeout(config.getConnectTimeoutMs());
            connection.setReadTimeout(config.getReadTimeoutMs());
            connection.setInstanceFollowRedirects(false);

            connection.setRequestProperty("Metadata-Flavor", "Google");

            int status = connection.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Error retrieving metadata: " + status);
            }

            String response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                response = content.toString();
            }

            return response;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}

