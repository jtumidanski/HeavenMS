package npc

import scripting.npc.NPCConversationManager
import tools.MasterBroadcaster
import tools.MessageBroadcaster
import tools.PacketCreator
import tools.ServerNoticeType
import tools.packet.field.effect.PlaySound
import tools.packet.field.effect.ShowEffect

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012027 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   String harpNote = 'C'
   String[] harpSounds = ["do", "re", "mi", "pa", "sol", "la", "si"]   // musical order detected thanks to Arufonsu
   String harpSong = "CCGGAAGFFEEDDC|GGFFEED|GGFFEED|CCGGAAGFFEEDDC|"

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            MasterBroadcaster.getInstance().sendToAllInMap(cm.getMap(), new PlaySound("orbis/" + harpSounds[cm.getNpc() - 2012027]))

            if (cm.isQuestStarted(3114)) {
               int idx = cm.getQuestProgress(3114, 7777)

               if (idx != -1) {
                  String nextNote = harpSong[idx]

                  if (harpNote != nextNote) {
                     cm.setQuestProgress(3114, 7777, 0)

                     PacketCreator.announce(cm.getPlayer(), new ShowEffect("quest/party/wrong_kor"))
                     PacketCreator.announce(cm.getPlayer(), new PlaySound("Party1/Failed"))

                     MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, "You've missed the note... Start over again.")
                  } else {
                     nextNote = harpSong[idx + 1]

                     if (nextNote == '|') {
                        idx++

                        if (idx == 45) {     // finished lullaby
                           MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, "Twinkle, twinkle, little star, how I wonder what you are.")
                           cm.setQuestProgress(3114, 7777, -1)

                           PacketCreator.announce(cm.getPlayer(), new ShowEffect("quest/party/clear"))
                           PacketCreator.announce(cm.getPlayer(), new PlaySound("Party1/Clear"))

                           cm.dispose()
                           return
                        } else {
                           if (idx == 14) {
                              MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, "Twinkle, twinkle, little star, how I wonder what you are!")
                           } else if (idx == 22) {
                              MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, "Up above the world so high,")
                           } else if (idx == 30) {
                              MessageBroadcaster.getInstance().sendServerNotice(cm.getPlayer(), ServerNoticeType.PINK_TEXT, "like a diamond in the sky.")
                           }
                        }
                     }

                     cm.setQuestProgress(3114, 7777, idx + 1)
                  }
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC2012027 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012027(cm: cm))
   }
   return (NPC2012027) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }