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

public class InstanceMetadataService {
    private static final String METADATA_SERVER_URL = "http://169.254.169.254/computeMetadata/v1/";

    private final HttpConnectionConfig config;
    private final URI metadataServerUrl;

    public InstanceMetadataService() {
        this(HttpConnectionConfig.DEFAULT);
    }

    public InstanceMetadataService(HttpConnectionConfig config) {
        this(config, METADATA_SERVER_URL);
    }

    public InstanceMetadataService(HttpConnectionConfig config, String metadataServerUrl) {
        this.config = config;

        // check that URL is correct
        try {
            URL url = new URL(metadataServerUrl);
            this.metadataServerUrl = url.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new yandex.cloud.sdk.auth.metadata.MalformedURLException(metadataServerUrl, e);
        }
    }

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
                throw new RuntimeException("Failed to retrieve metadata value", e);
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
                StringBuffer content = new StringBuffer();
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

