package client.database.data;

public record QuestData(short questId, int status, long time, long expires, int forfeited, int completed,
                        int questStatusId) {
}
