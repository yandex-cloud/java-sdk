package yandex.cloud.sdk;

/**
 * Contains available Yandex.Cloud Compute platforms.
 */
public enum Platform {
    STANDARD_V1("standard-v1"),
    STANDARD_V2("standard-v2");

    private final String id;

    Platform(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Platform{" +
                "id='" + id + '\'' +
                '}';
    }
}
