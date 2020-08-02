package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2186 {
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
            if (!qm.isQuestCompleted(2186)) {
               if (qm.haveItem(4031853)) {
                  if (qm.canHold(2030019)) {
                     qm.gainItem(4031853, (short) -1)
                     qm.gainExp(1700)
                     qm.gainItem(2030019, (short) 10)

                     qm.sendOk(I18nMessage.from("2186_FOUND_MY_GLASSES"))
                     qm.forceCompleteQuest()
                  } else {
                     qm.sendOk(I18nMessage.from("2186_NEED_USE_SLOT_FREE"))
                  }
               } else if (qm.haveItem(4031854) || qm.haveItem(4031855)) {
                  if (qm.canHold(2030019)) {
                     if (qm.haveItem(4031854)) {
                        qm.gainItem(4031854, (short) -1)
                     } else {
                        qm.gainItem(4031855, (short) -1)
                     }

                     qm.gainExp(1000)
                     qm.gainItem(2030019, (short) 5)

                     qm.sendOk(I18nMessage.from("2186_ALAS_I_WILL_TAKE_THEM"))
                     qm.forceCompleteQuest()
                  } else {
                     qm.sendOk(I18nMessage.from("2186_NEED_USE_SLOT_FREE"))
                  }
               }
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }
}

Quest2186 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2186(qm: qm))
   }
   return (Quest2186) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}