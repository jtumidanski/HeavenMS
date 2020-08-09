package client.database.data;

public record BbsThreadReplyData(Integer threadId, Integer replyId, Integer posterCharacterId, Long timestamp,
                                 String content) {
}
