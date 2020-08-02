package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21735 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
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
            qm.sendNext(I18nMessage.from("21735_DELIVER_THE_GEM"))
         } else if (status == 1) {
            if (!qm.canHold(4032323, 1)) {
               qm.sendNext(I18nMessage.from("21735_FREE_ETCH_SPACE"))
               qm.dispose()
               return
            }

            if (!qm.haveItem(4032323, 1)) {
               qm.gainItem(4032323, (short) 1)
            }
            qm.forceStartQuest()
            qm.dispose()
         }
      }
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
            if (qm.haveItem(4032323, 1)) {
               qm.sendNext(I18nMessage.from("21735_GEM_WILL_BE_SAFER"))
            } else {
               qm.dispose()
            }
         } else if (status == 1) {
            qm.gainItem(4032323, (short) -1)
            qm.gainExp(6037)
            qm.forceCompleteQuest()

            qm.dispose()
         }
      }
   }
}

Quest21735 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21735(qm: qm))
   }
   return (Quest21735) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}