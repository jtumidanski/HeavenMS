package client.database.data;

import java.sql.Timestamp;

public record CharacterRankData(Timestamp lastLogin, Integer loggedIn, Integer move, Integer rank,
                                Integer characterId) {
}
