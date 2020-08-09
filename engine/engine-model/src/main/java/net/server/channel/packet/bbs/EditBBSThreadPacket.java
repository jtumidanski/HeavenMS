package net.server.channel.packet.bbs;

public class EditBBSThreadPacket extends BaseBBSOperationPacket {
   private final Integer threadId;

   private final Boolean isNotice;

   private final String title;

   private final String text;

   private final Integer icon;

   public EditBBSThreadPacket(Byte mode, Integer threadId, Boolean isNotice, String title, String text, Integer icon) {
      super(mode);
      this.threadId = threadId;
      this.isNotice = isNotice;
      this.title = title;
      this.text = text;
      this.icon = icon;
   }

   public Integer threadId() {
      return threadId;
   }

   public Boolean isNotice() {
      return isNotice;
   }

   public String title() {
      return title;
   }

   public String text() {
      return text;
   }

   public Integer icon() {
      return icon;
   }
}
