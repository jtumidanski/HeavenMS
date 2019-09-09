package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9040010 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      EventInstanceManager eim = cm.getPlayer().getEventInstance()
      if (eim != null) {
         if (cm.isEventLeader()) {
            if (cm.haveItem(4001024)) {
               cm.removeAll(4001024)
               Object prev = eim.setProperty("bossclear", "true", true)
               if (prev == null) {
                  int start = (eim.getProperty("entryTimestamp")).toInteger()
                  long diff = System.currentTimeMillis() - start

                  int points = 1000 - Math.floor(diff / (100 * 60)).intValue()
                  if (points < 100) {
                     points = 100
                  }

                  cm.getGuild().gainGP(points)
               }

               eim.clearPQ()
            } else {
               cm.sendOk("This is your final challenge. Defeat the evil lurking within the Rubian and return it to me. That is all.")
            }
         } else {
            cm.sendOk("This is your final challenge. Defeat the evil lurking within the Rubian and let your instance leader return it to me. That is all.")
         }
      } else {
         cm.warp(990001100)
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9040010 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9040010(cm: cm))
   }
   return (NPC9040010) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }