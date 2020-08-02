package npc
import tools.I18nMessage

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2112013 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      EventInstanceManager eim = cm.getEventInstance()
      String book = "stg1_b" + (cm.getNpcObjectId() % 26)

      int res = eim.getIntProperty(book)
      if (res > -1) {
         eim.setIntProperty(book, -1)

         if (res == 0) {  // mesos
            int mgain = 500 * cm.getPlayer().getMesoRate()
            cm.sendNext(I18nMessage.from("2112013_EARNED_MESOS").with(mgain))
            cm.gainMeso(mgain)
         } else if (res == 1) {  // exp
            int egain = 500 * cm.getPlayer().getExpRate()
            cm.sendNext(I18nMessage.from("2112013_EARNED_EXP").with(egain))
            cm.gainExp(egain)
         } else if (res == 2) {  // letter
            int letter = 4001130
            if (!cm.canHold(letter)) {
               cm.sendOk(I18nMessage.from("2112013_NO_INVENTORY"))
               cm.dispose()
               return
            }

            cm.gainItem(letter, (short) 1)
            cm.sendNext(I18nMessage.from("2112013_FOUND_A_LETTER"))
         } else if (res == 3) {  // pass
            cm.sendNext(I18nMessage.from("2112013_FOUND_A_TRIGGER"))

            eim.showClearEffect()
            eim.giveEventPlayersStageReward(1)
            eim.setIntProperty("statusStg1", 1)

            cm.getMap().getReactorByName("d00").hitReactor(cm.getClient())
         }
      } else {
         cm.sendNext(I18nMessage.from("2112013_NOTHING_HERE"))
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2112013 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2112013(cm: cm))
   }
   return (NPC2112013) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }