package net.server.world;

import client.MapleCharacter;

public class MapleMessengerCharacter {
   private String name;
   private int id, position;
   private int channel;
   private boolean online;

   public MapleMessengerCharacter(MapleCharacter character, int position) {
      this.name = character.getName();
      this.channel = character.getClient().getChannel();
      this.id = character.getId();
      this.online = true;
      this.position = position;
   }

   public int getId() {
      return id;
   }

   public int getChannel() {
      return channel;
   }

   public String getName() {
      return name;
   }

   public boolean isOnline() {
      return online;
   }

   public int getPosition() {
      return position;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final MapleMessengerCharacter other = (MapleMessengerCharacter) obj;
      if (name == null) {
         return other.name == null;
      } else return name.equals(other.name);
   }
}
