package server.processor;

import client.MapleCharacter;
import tools.PacketCreator;
import tools.UserMessage;
import tools.packet.npctalk.GetNPCTalk;
import tools.packet.npctalk.GetNPCTalkNum;

public class NpcConversationProcessor {
   private static final Object lock = new Object();

   private static volatile NpcConversationProcessor instance;

   public static NpcConversationProcessor getInstance() {
      NpcConversationProcessor result = instance;
      if (result == null) {
         synchronized (lock) {
            result = instance;
            if (result == null) {
               result = new NpcConversationProcessor();
               instance = result;
            }
         }
      }
      return result;
   }

   public void sendNext(MapleCharacter character, int npcId, UserMessage text) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 0, text.to(character).evaluate(), "00 01", (byte) 0));
   }

   public void sendPrev(MapleCharacter character, int npcId, UserMessage text) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 0, text.to(character).evaluate(), "01 00", (byte) 0));
   }

   public void sendNextPrev(MapleCharacter character, int npcId, UserMessage text) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 0, text.to(character).evaluate(), "01 01", (byte) 0));
   }

   public void sendOk(MapleCharacter character, int npcId, UserMessage text) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 0, text.to(character).evaluate(), "00 00", (byte) 0));
   }

   public void sendYesNo(MapleCharacter character, int npcId, UserMessage text) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 1, text.to(character).evaluate(), "", (byte) 0));
   }

   public void sendAcceptDecline(MapleCharacter character, int npcId, UserMessage text) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 0x0C, text.to(character).evaluate(), "", (byte) 0));
   }

   public void sendSimple(MapleCharacter character, int npcId, UserMessage text) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 4, text.to(character).evaluate(), "", (byte) 0));
   }

   public void sendNext(MapleCharacter character, int npcId, UserMessage text, byte speaker) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 0, text.to(character).evaluate(), "00 01", speaker));
   }

   public void sendPrev(MapleCharacter character, int npcId, UserMessage text, byte speaker) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 0, text.to(character).evaluate(), "01 00", speaker));
   }

   public void sendNextPrev(MapleCharacter character, int npcId, UserMessage text, byte speaker) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 0, text.to(character).evaluate(), "01 01", speaker));
   }

   public void sendOk(MapleCharacter character, int npcId, UserMessage text, byte speaker) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 0, text.to(character).evaluate(), "00 00", speaker));
   }

   public void sendYesNo(MapleCharacter character, int npcId, UserMessage text, byte speaker) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 1, text.to(character).evaluate(), "", speaker));
   }

   public void sendAcceptDecline(MapleCharacter character, int npcId, UserMessage text, byte speaker) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 0x0C, text.to(character).evaluate(), "", speaker));
   }

   public void sendSimple(MapleCharacter character, int npcId, UserMessage text, byte speaker) {
      PacketCreator.announce(character, new GetNPCTalk(npcId, (byte) 4, text.to(character).evaluate(), "", speaker));
   }

   public void sendGetNumber(MapleCharacter character, int npcId, UserMessage text, int def, int min, int max) {
      PacketCreator.announce(character, new GetNPCTalkNum(npcId, text.to(character).evaluate(), def, min, max));
   }
}
