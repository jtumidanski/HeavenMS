package constants.string;

import client.MapleCharacter;

public class LanguageConstants {

   public static String[] CPQBlue = new String[3];
   public static String[] CPQFindError = new String[3];
   public static String[] CPQRed = new String[3];
   public static String[] CPQPickRoom = new String[3];
   public static String[] CPQLeaderNotFound = new String[3];
   public static String[] CPQChallengeRoomAnswer = new String[3];
   public static String[] CPQChallengeRoomSent = new String[3];

   static {
      int lang;

      lang = Language.LANG_PRT.getValue();
      LanguageConstants.CPQBlue[lang] = "Maple Azul";
      LanguageConstants.CPQRed[lang] = "Maple Vermelho";
      LanguageConstants.CPQLeaderNotFound[lang] = "Nao foi possivel encontrar o Lider.";
      LanguageConstants.CPQPickRoom[lang] = "Inscreva-se no Festival de Monstros!\r\n";
      LanguageConstants.CPQChallengeRoomAnswer[lang] = "O grupo esta respondendo um desafio no momento.";
      LanguageConstants.CPQChallengeRoomSent[lang] = "Um desafio foi enviado para o grupo na sala. Aguarde um momento.";
      LanguageConstants.CPQFindError[lang] = "Nao foi possivel encontrar um grupo nesta sala.\r\nProvavelmente o grupo foi desfeito dentro da sala!";

      lang = Language.LANG_ESP.getValue();
      LanguageConstants.CPQBlue[lang] = "Maple Azul";
      LanguageConstants.CPQRed[lang] = "Maple Rojo";
      LanguageConstants.CPQLeaderNotFound[lang] = "No se pudo encontrar el Lider.";
      LanguageConstants.CPQPickRoom[lang] = "!Inscribete en el Festival de Monstruos!\r\n";
      LanguageConstants.CPQChallengeRoomAnswer[lang] = "El grupo esta respondiendo un desafio en el momento.";
      LanguageConstants.CPQChallengeRoomSent[lang] = "Un desafio fue enviado al grupo en la sala. Espera un momento.";
      LanguageConstants.CPQFindError[lang] = "No se pudo encontrar un grupo en esta sala.\r\nProbablemente el grupo fue deshecho dentro de la sala!";

      lang = Language.LANG_ENG.getValue();
      LanguageConstants.CPQBlue[lang] = "Maple Blue";
      LanguageConstants.CPQRed[lang] = "Maple Red";
      LanguageConstants.CPQLeaderNotFound[lang] = "Could not find the Leader.";
      LanguageConstants.CPQPickRoom[lang] = "Sign up for the Monster Festival!\r\n";
      LanguageConstants.CPQChallengeRoomAnswer[lang] = "The group is currently facing a challenge.";
      LanguageConstants.CPQChallengeRoomSent[lang] = "A challenge has been sent to the group in the room. Please wait a while.";
      LanguageConstants.CPQFindError[lang] = "We could not find a group in this room.\r\nProbably the group was scrapped inside the room!";
   }

   //TODO - JDT
   public static String getMessage(MapleCharacter chr, String[] message) {
      return message[2];
   }

   enum Language {
      LANG_PRT(0),
      LANG_ESP(1),
      LANG_ENG(2);

      int lang;

      Language(int lang) {
         this.lang = lang;
      }

      private int getValue() {
         return this.lang;
      }

   }
}
