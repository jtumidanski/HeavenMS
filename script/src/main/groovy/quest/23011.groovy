package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest23011 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendYesNo(I18nMessage.from("23011_CAN_STILL_CHANGE_YOUR_MIND"))
      } else if (status == 1) {
         if (mode == 0) {
            qm.sendNext(I18nMessage.from("23011_THINK_CAREFULLY"))
         } else {
            if (!qm.isQuestCompleted(23011)) {
               qm.gainItem(1382100)
               qm.gainItem(1142242)
               qm.forceCompleteQuest()
               qm.changeJobById(3200)
               qm.getPlayer().showItemGain(1382100, 1142242)
            }
            qm.sendNext(I18nMessage.from("23011_WELCOME"))
         }
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("23011_I_WILL_BE_YOUR_TEACHER"))
      } else if (status == 3) {
         qm.dispose()
      }
   }
}

Quest23011 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest23011(qm: qm))
   }
   return (Quest23011) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}