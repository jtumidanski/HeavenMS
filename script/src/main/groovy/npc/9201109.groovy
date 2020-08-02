package npc
import tools.I18nMessage

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201109 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getPlayer().getMapId() == 610030500) {
         cm.sendOk(I18nMessage.from("9201109_QUALITY_WIZARD"))
         cm.dispose()
      } else if (cm.getPlayer().getMap().getId() == 610030000) {
         cm.sendOk(I18nMessage.from("9201109_EXCEPTIONALLY_SKILLED_SORCERER"))
         cm.dispose()
      } else if (cm.getPlayer().getMapId() == 610030521) {
         if (cm.getPlayer().getMap().countMonsters() == 0) {
            EventInstanceManager eim = cm.getEventInstance()
            int stgStatus = eim.getIntProperty("glpq5_room")
            int jobNiche = cm.getPlayer().getJob().getJobNiche()

            if ((stgStatus >> jobNiche) % 2 == 0) {
               if (cm.canHold(4001257, 1)) {
                  cm.gainItem(4001257, (short) 1)
                  cm.sendOk(I18nMessage.from("9201109_GOOD_JOB"))

                  stgStatus += (1 << jobNiche)
                  eim.setIntProperty("glpq5_room", stgStatus)
               } else {
                  cm.sendOk(I18nMessage.from("9201109_MAKE_ETC_SPACE"))
               }
            } else {
               cm.sendOk(I18nMessage.from("9201109_ALREADY_RETRIEVED"))
            }
         } else {
            cm.sendOk(I18nMessage.from("9201109_ELIMINATE_ALL"))
         }
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201109 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201109(cm: cm))
   }
   return (NPC9201109) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }