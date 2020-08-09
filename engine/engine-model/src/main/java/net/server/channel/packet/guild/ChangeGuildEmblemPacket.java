package net.server.channel.packet.guild;

public class ChangeGuildEmblemPacket extends BaseGuildOperationPacket {
   private final Short background;

   private final Byte backgroundColor;

   private final Short logo;

   private final Byte logoColor;

   public ChangeGuildEmblemPacket(Byte theType, Short background, Byte backgroundColor, Short logo, Byte logoColor) {
      super(theType);
      this.background = background;
      this.backgroundColor = backgroundColor;
      this.logo = logo;
      this.logoColor = logoColor;
   }

   public Short background() {
      return background;
   }

   public Byte backgroundColor() {
      return backgroundColor;
   }

   public Short logo() {
      return logo;
   }

   public Byte logoColor() {
      return logoColor;
   }
}
