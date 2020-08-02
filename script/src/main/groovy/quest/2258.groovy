package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2258 {
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
            qm.sendAcceptDecline(I18nMessage.from("2258_RUMORS_LIKE_WILDFIRE"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("2258_KILL_MEERKATS"))
         } else if (status == 2) {
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
            qm.sendNext(I18nMessage.from("2258_YOU_DID_IT"))
         } else if (status == 1) {
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest2258 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2258(qm: qm))
   }
   return (Quest2258) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}