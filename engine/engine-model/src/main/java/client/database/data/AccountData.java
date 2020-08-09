package client.database.data;

public record AccountData(Integer id, String name, String password, Integer gender, Boolean banned, String pin,
                          String pic, Integer characterSlots, Boolean tos, String language, String country) {
}
