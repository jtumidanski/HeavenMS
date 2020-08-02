package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21734 {
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
            qm.sendNext(I18nMessage.from("21734_HELLO"))
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
            qm.sendNext(I18nMessage.from("21734_YOU_MADE_IT"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("21734_NOW_SAFE_AND_SOUND"))
         } else if (status == 2) {
            qm.sendNext(I18nMessage.from("21734_REWARD"))
         } else if (status == 3) {
            qm.forceCompleteQuest()
            qm.gainExp(12500)
            qm.teachSkill(21100005, (byte) 0, (byte) 20, -1) // combo drain
            qm.dispose()
         }
      }
   }
}

Quest21734 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21734(qm: qm))
   }
   return (Quest21734) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}