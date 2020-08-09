package net.server.channel.packet.command.admin;

public class QuestResetPacket extends BaseAdminCommandPacket {
   private final Integer questId;

   public QuestResetPacket(Byte mode, Integer questId) {
      super(mode);
      this.questId = questId;
   }

   public Integer questId() {
      return questId;
   }
}
