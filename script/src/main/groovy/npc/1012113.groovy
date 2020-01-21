package npc

import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Tommy (HPQ)
	Map(s): 		
	Description: 	
*/
class NPC1012113 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (cm.getPlayer().getMap().getId() == 910010100) { //Clear map
            if (status == 0) {
               cm.sendNext(I18nMessage.from("1012113_HELLO"))
            } else if (status == 1) {
               if (cm.isEventLeader()) {
                  cm.sendYesNo(I18nMessage.from("1012113_TEACH_THE_PIGS_A_LESSON"))
               } else {
                  cm.sendOk(I18nMessage.from("1012113_TELL_PARTY_LEADER"))
                  cm.dispose()
               }
            } else if (status == 2) {
               cm.getEventInstance().startEventTimer(5 * 60000)
               cm.getEventInstance().warpEventTeam(910010200)
               cm.dispose()
            }
         } else if (cm.getPlayer().getMap().getId() == 910010200) { //Bonus map
            if (status == 0) {
               cm.sendYesNo(I18nMessage.from("1012113_EXIT_BONUS"))
            } else {
               cm.warp(910010400)
               cm.dispose()
            }
         } else if (cm.getPlayer().getMap().getId() == 910010300) { //Exit map
            if (status == 0) {
               cm.sendOk(I18nMessage.from("1012113_EXIT_EXIT"))
            } else {
               cm.warp(100000200)
               cm.dispose()
            }
         }
      }
   }
}

NPC1012113 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012113(cm: cm))
   }
   return (NPC1012113) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }