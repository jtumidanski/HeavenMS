package npc
import tools.I18nMessage

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201110 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      switch (cm.getPlayer().getMapId()) {
         case 610030500:
            cm.sendOk(I18nMessage.from("9201110_ONLY_GET_TO_WITH_HASTE"))
            break
         case 610030000:
            cm.sendOk(I18nMessage.from("9201110_ABILITY_TO_BLEND"))
            break
         case 610030530:
            if (cm.isAllReactorState(6108004, 1)) {
               EventInstanceManager eim = cm.getEventInstance()
               int stgStatus = eim.getIntProperty("glpq5_room")
               int jobNiche = cm.getPlayer().getJob().getJobNiche()

               if ((stgStatus >> jobNiche) % 2 == 0) {
                  if (cm.canHold(4001256, 1)) {
                     cm.gainItem(4001256, (short) 1)
                     cm.sendOk(I18nMessage.from("9201110_GOOD_JOB"))

                     stgStatus += (1 << jobNiche)
                     eim.setIntProperty("glpq5_room", stgStatus)
                  } else {
                     cm.sendOk(I18nMessage.from("9201110_MAKE_ETC_ROOM"))
                  }
               } else {
                  cm.sendOk(I18nMessage.from("9201110_ALREADY_BEEN_RETRIEVED"))
               }
            } else {
               cm.sendOk(I18nMessage.from("9201110_DESTROY_ALL"))
            }
            break
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201110 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201110(cm: cm))
   }
   return (NPC9201110) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }