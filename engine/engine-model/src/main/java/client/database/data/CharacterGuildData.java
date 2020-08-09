package client.database.data;

public record CharacterGuildData(Integer id, Integer guildId, Integer guildRank, String name, Integer allianceRank,
                                 Integer level, Integer job) {
}
