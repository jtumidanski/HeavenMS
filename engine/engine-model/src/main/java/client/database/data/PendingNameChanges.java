package client.database.data;

public record PendingNameChanges(Integer id, Integer characterId, String oldName, String newName) {
}
