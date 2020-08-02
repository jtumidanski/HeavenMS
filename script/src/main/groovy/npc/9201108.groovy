package npc
import tools.I18nMessage

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201108 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getPlayer().getMapId() == 610030500) {
         cm.sendOk(I18nMessage.from("9201108_LEGENDARY_CREATURE_AWAITS"))
         cm.dispose()
      } else if (cm.getPlayer().getMap().getId() == 610030000) {
         cm.sendOk(I18nMessage.from("9201108_MOST_FAMOUS_HEROES"))
         cm.dispose()
      } else if (cm.getPlayer().getMapId() == 610030540) {
         if (cm.getPlayer().getMap().countMonsters() == 0) {
            EventInstanceManager eim = cm.getEventInstance()
            int stgStatus = eim.getIntProperty("glpq5_room")
            int jobNiche = cm.getPlayer().getJob().getJobNiche()

            if ((stgStatus >> jobNiche) % 2 == 0) {
               if (cm.canHold(4001258, 1)) {
                  cm.gainItem(4001258, (short) 1)
                  cm.sendOk(I18nMessage.from("9201108_GOOD_JOB"))

                  stgStatus += (1 << jobNiche)
                  eim.setIntProperty("glpq5_room", stgStatus)
               } else {
                  cm.sendOk(I18nMessage.from("9201108_MAKE_ETC_ROOM"))
               }
            } else {
               cm.sendOk(I18nMessage.from("9201108_ALREADY_BEEN_RETRIEVED"))
            }
         } else {
            cm.sendOk(I18nMessage.from("9201108_ELIMINATE_ALL"))
         }
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201108 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201108(cm: cm))
   }
   return (NPC9201108) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }