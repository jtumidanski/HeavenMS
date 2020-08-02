package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21733 {
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
            qm.sendNext(I18nMessage.from("21733_UNDER_ATTACK"))
         } else {
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
            qm.sendNext(I18nMessage.from("21733_THANK_YOU"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("21733_I_WILL_TEACH_YOU"))
         } else if (status == 2) {
            qm.gainExp(8000)
            qm.teachSkill(21100000, (byte) 0, (byte) 20, -1) // polearm mastery

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest21733 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21733(qm: qm))
   }
   return (Quest21733) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}