package quest
import tools.I18nMessage


import constants.MapleInventoryType
import scripting.quest.QuestActionManager

class Quest3239 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (qm.haveItem(4031092, 10)) {
               if (qm.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                  qm.sendOk(I18nMessage.from("3239_WELL_DONE"))

               } else {
                  qm.sendOk(I18nMessage.from("3239_USE_SPACE_NEEDED"))

                  qm.dispose()
               }
            } else {
               qm.sendOk(I18nMessage.from("3239_RETURN_ME"))

               qm.dispose()
            }
         } else if (status == 1) {
            qm.gainItem(4031092, (short) -10)

            int rnd = Math.floor(Math.random() * 4).intValue()
            if (rnd == 0) {
               qm.gainItem(2040704, (short) 1)
            } else if (rnd == 1) {
               qm.gainItem(2040705, (short) 1)
            } else if (rnd == 2) {
               qm.gainItem(2040707, (short) 1)
            } else {
               qm.gainItem(2040708, (short) 1)
            }

            qm.gainExp(2700)
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest3239 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3239(qm: qm))
   }
   return (Quest3239) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}