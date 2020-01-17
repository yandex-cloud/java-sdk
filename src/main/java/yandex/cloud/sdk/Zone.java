package yandex.cloud.sdk;

/**
 * Contains available Yandex.Cloud availability zones.
 */
public enum Zone {
    RU_CENTRAL1_A("ru-central1-a"),
    RU_CENTRAL1_B("ru-central1-b"),
    RU_CENTRAL1_C("ru-central1-c");

    private final String id;

    Zone(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "id='" + id + '\'' +
                '}';
    }
}
