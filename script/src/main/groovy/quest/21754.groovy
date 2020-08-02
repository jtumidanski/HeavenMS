package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21754 {
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
            if (!qm.canHold(4032328, 1)) {
               qm.sendNext(I18nMessage.from("21754_NEED_AN_ETC_SPACE"))
               qm.dispose()
               return
            }

            qm.sendNext(I18nMessage.from("21754_TAKE_THIS"))
         } else if (status == 1) {
            qm.forceStartQuest()

            qm.gainItem(4032328, (short) 1)
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21754 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21754(qm: qm))
   }
   return (Quest21754) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}