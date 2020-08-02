package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2325 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }

      if (status == 0) {
         qm.sendNext(I18nMessage.from("2325_I_AM_SCARED"))
      } else if (status == 1) {
         qm.sendNextPrev("Don't be afraid, #b#p1300005##k sent me here.",  (byte) 2)
      } else if (status == 2) {
         qm.forceCompleteQuest()
         qm.gainExp(6000)
         qm.sendOk(I18nMessage.from("2325_THANK_YOU"))
      } else if (status == 3) {
         qm.dispose()
      }
   }
}

Quest2325 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2325(qm: qm))
   }
   return (Quest2325) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}