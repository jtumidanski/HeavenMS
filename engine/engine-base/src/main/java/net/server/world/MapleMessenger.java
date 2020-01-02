package net.server.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class MapleMessenger {

   private int id;
   private List<MapleMessengerCharacter> members = new ArrayList<>(3);
   private boolean[] pos = new boolean[3];

   public MapleMessenger(int id, MapleMessengerCharacter messengerCharacter) {
      this.id = id;
      for (int i = 0; i < 3; i++) {
         pos[i] = false;
      }
      addMember(messengerCharacter, messengerCharacter.getPosition());
   }

   public int getId() {
      return id;
   }

   public Collection<MapleMessengerCharacter> getMembers() {
      return Collections.unmodifiableList(members);
   }

   public void addMember(MapleMessengerCharacter member, int position) {
      members.add(member);
      member.setPosition(position);
      pos[position] = true;
   }

   public void removeMember(MapleMessengerCharacter member) {
      int position = member.getPosition();
      pos[position] = false;
      members.remove(member);
   }

   public int getLowestPosition() {
      for (byte i = 0; i < 3; i++) {
         if (!pos[i]) {
            return i;
         }
      }
      return -1;
   }

   public int getPositionByName(String name) {
      for (MapleMessengerCharacter messengerCharacter : members) {
         if (messengerCharacter.getName().equals(name)) {
            return messengerCharacter.getPosition();
         }
      }
      return -1;
   }
}

