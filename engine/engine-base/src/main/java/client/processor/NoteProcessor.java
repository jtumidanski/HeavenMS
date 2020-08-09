package client.processor;

import database.DatabaseConnection;
import database.administrator.NoteAdministrator;

public class NoteProcessor {
   private static NoteProcessor ourInstance = new NoteProcessor();

   public static NoteProcessor getInstance() {
      return ourInstance;
   }

   private NoteProcessor() {
   }

   public void sendNote(String to, String from, String msg, byte fame) {
      DatabaseConnection.getInstance().withConnection(connection -> NoteAdministrator.getInstance().sendNote(connection, to, from, msg, fame));
   }
}
