package net.server.channel.packet.interaction;

public class CreateMatchCardPlayerInteractionPacket extends BaseCreatePlayerInteractionPacket {
   private final String description;

   private final Boolean hasPassword;

   private final String password;

   private final Integer theType;

   public CreateMatchCardPlayerInteractionPacket(Byte mode, Byte createType, String description, Boolean hasPassword, String password, Integer theType) {
      super(mode, createType);
      this.description = description;
      this.hasPassword = hasPassword;
      this.password = password;
      this.theType = theType;
   }

   public String description() {
      return description;
   }

   public Boolean hasPassword() {
      return hasPassword;
   }

   public String password() {
      return password;
   }

   public Integer theType() {
      return theType;
   }
}
