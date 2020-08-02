package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest3382 {
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
            if (qm.haveItem(4001159, 25) && qm.haveItem(4001160, 25) && !qm.haveItemWithId(1122010, true)) {
               if (qm.canHold(1122010)) {
                  qm.gainItem(4001159, (short) -25)
                  qm.gainItem(4001160, (short) -25)
                  qm.gainItem(1122010, (short) 1)

                  qm.sendOk(I18nMessage.from("3382_THANK_YOU"))
               } else {
                  qm.sendNext(I18nMessage.from("3382_EQUIP_SLOT_NEEDED"))
                  return
               }
            } else if (qm.haveItem(4001159, 10) && qm.haveItem(4001160, 10)) {
               if (qm.canHold(2041212)) {
                  qm.gainItem(4001159, (short) -10)
                  qm.gainItem(4001160, (short) -10)
                  qm.gainItem(2041212, (short) 1)

                  qm.sendOk(I18nMessage.from("3382_THANK_YOU_LONG"))
               } else {
                  qm.sendNext(I18nMessage.from("3382_USE_SLOT_NEEDED"))
                  return
               }
            } else {
               qm.sendNext(I18nMessage.from("3382_I_NEED_AT_LEAST"))
               return
            }

            qm.forceCompleteQuest()
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }
}

Quest3382 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3382(qm: qm))
   }
   return (Quest3382) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}