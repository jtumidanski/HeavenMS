package net.server.channel.packet.bbs;

public class NewBBSThreadPacket extends BaseBBSOperationPacket {
   private final Boolean isNotice;

   private final String title;

   private final String text;

   private final Integer icon;

   public NewBBSThreadPacket(Byte mode, Boolean isNotice, String title, String text, Integer icon) {
      super(mode);
      this.isNotice = isNotice;
      this.title = title;
      this.text = text;
      this.icon = icon;
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
