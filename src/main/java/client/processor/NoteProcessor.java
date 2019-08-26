package client.processor;

import client.database.administrator.NoteAdministrator;
import tools.DatabaseConnection;

public class NoteProcessor {
   private static NoteProcessor ourInstance = new NoteProcessor();

   public static NoteProcessor getInstance() {
      return ourInstance;
   }

   private NoteProcessor() {
   }

   public void sendNote(String to, String from, String msg, byte fame) {
      DatabaseConnection.withConnection(connection -> NoteAdministrator.getInstance().sendNote(connection, to, from, msg, fame));
   }
}
