package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21748 {
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
            qm.sendNext(I18nMessage.from("21748_COME_BACK_HOME"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("21748_FINE_IMPROVEMENT"))
         } else if (status == 2) {
            qm.gainExp(20000)
            qm.teachSkill(21100002, (byte) 0, (byte) 30, -1) // final charge

            qm.forceCompleteQuest()

            qm.dispose()
         }
      }
   }
}

Quest21748 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21748(qm: qm))
   }
   return (Quest21748) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}