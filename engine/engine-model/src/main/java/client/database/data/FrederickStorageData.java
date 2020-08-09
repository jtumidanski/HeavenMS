package client.database.data;

import java.sql.Timestamp;

public record FrederickStorageData(Integer characterId, String name, Integer worldId, Timestamp timestamp,
                                   Integer dayNotes, Timestamp lastLogoutTime) {
}
