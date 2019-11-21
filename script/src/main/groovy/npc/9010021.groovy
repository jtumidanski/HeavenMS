package npc

import config.YamlConfig
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9010021 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      if (!YamlConfig.config.server.USE_REBIRTH_SYSTEM) {
         cm.sendOk("... I came from distant planes to assist the fight against the #rBlack Magician#k. Right now I search my master, have you seen him?")
         cm.dispose()
         return
      }
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendNext("Come to me when you want to be reborn again. You currently have a total of #r" + cm.getChar().getReborns() + " #krebirths.")
      } else if (status == 1) {
         cm.sendSimple("What do you want me to do today: \r\n \r\n #L0##bI want to be rebirthed#l \r\n #L1##bMaybe next time#k#l")
      } else if (status == 2) {
         if (selection == 0) {
            if (cm.getChar().getLevel() == 200) {
               cm.sendYesNo("Are you sure you want to be rebirthed?")
            } else {
               cm.sendOk("You are not level 200, please come back when you hit level 200.")
               cm.dispose()
            }
         } else if (selection == 1) {
            cm.sendOk("Ok Bye")
            cm.dispose()
         }
      } else if (status == 3 && type == 1) {
         cm.getChar().executeReborn()
         cm.sendOk("You have now been reborn. That's a total of #r" + cm.getChar().getReborns() + "#k rebirths")
         cm.dispose()
      }
   }
}

NPC9010021 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9010021(cm: cm))
   }
   return (NPC9010021) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }