package client.database.data;

public record PetData(String name, Byte level, Integer closeness, Integer fullness, Boolean summoned, Integer flag) {
}
