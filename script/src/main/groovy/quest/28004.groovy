package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest28004 {
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
            if (qm.getPlayer().getLevel() > 50) {
               qm.forceCompleteQuest()
               qm.dispose()
               return
            }

            qm.sendNext(I18nMessage.from("28004_HERE_IS_OUR_PLAN"))
         } else if (status == 1) {
            qm.sendAcceptDecline(I18nMessage.from("28004_MOVE_FORWARD"))
         } else if (status == 2) {
            int level = qm.getPlayer().getLevel()

            qm.warp(level <= 30 ? 889100000 : (level <= 40 ? 889100010 : 889100020))
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest28004 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest28004(qm: qm))
   }
   return (Quest28004) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}