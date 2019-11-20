package tools.packet.login;

public enum LoginFailedReason {
   DELETED_OR_BLOCKED(3), //ID deleted or blocked
   INCORRECT_PASSWORD(4), //Incorrect password
   NOT_REGISTERED(5), //Not a registered id
   SYSTEM_ERROR(6), //System error
   ALREADY_LOGGED_IN(7), //Already logged in
   SYSTEM_ERROR_2(8), //System error
   SYSTEM_ERROR_3(9), //System error
   TOO_MANY_CONNECTIONS(10), //Cannot process so many connections
   AGE_LIMIT(11), // Only users older than 20 can use this channel
   UNABLE_TO_LOG_ON_AS_MASTER_AT_IP(13), //Unable to log on as master at this ip
   WRONG_GATEWAY(14), //Wrong gateway or personal info and weird korean button
   PROCESSING_REQUEST(15), //Processing request with that korean button!
   ACCOUNT_VERIFICATION_NEEDED(16), //Please verify your account through email...
   WRONG_PERSONAL_INFO(17), //Wrong gateway or personal info
   ACCOUNT_VERIFICATION_NEEDED_2(21), //Please verify your account through email...
   LICENSE_AGREEMENT(23), //License agreement
   MAPLE_EUROPE_NOTICE(25), //Maple Europe notice =[ FUCK YOU NEXON
   FULL_CLIENT_NOTICE(27); //Some weird full client notice, probably for trial versions

   private final int value;

   LoginFailedReason(int value) {
      this.value = value;
   }

   public int getValue() {
      return value;
   }

   public static LoginFailedReason fromValue(int value) {
      for (LoginFailedReason op : LoginFailedReason.values()) {
         if (op.getValue() == value) {
            return op;
         }
      }
      return null;
   }
}
