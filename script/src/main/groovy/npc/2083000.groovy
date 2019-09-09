package npc

import constants.ServerConstants
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2083000 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            if (cm.haveItem(4001086)) {
               cm.sendYesNo("Do you want to access #b#m240050400##k right now?")
            } else if (ServerConstants.USE_ENABLE_SOLO_EXPEDITIONS) {
               if (canBypassHTPQ()) {
                  cm.sendYesNo("Do you want to access #b#m240050400##k right now?")
               } else {
                  cm.sendOk("Those who don't have the #r#t4001086##k must prove their valor before challenging #bHorntail#k. Come here holding the #r3 certificates#k that proves you're worthy to || the task.")
                  // NPC picture is so long it goes through some section of text, || to fill up that space
                  cm.dispose()
               }
            } else {
               cm.sendOk("Those who don't have the #r#t4001086##k must prove their valor before challenging #bHorntail#k.")
               cm.dispose()
            }
         } else {
            cm.warp(240050400)
            cm.dispose()
         }
      }
   }

   def canBypassHTPQ() {
      return cm.haveItem(4001083) && cm.haveItem(4001084) && cm.haveItem(4001085)
   }
}

NPC2083000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2083000(cm: cm))
   }
   return (NPC2083000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }