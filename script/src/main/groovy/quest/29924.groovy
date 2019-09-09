package quest


import scripting.quest.QuestActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Quest29924 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (qm.getPlayer().getLevel() >= 10 && ((qm.getPlayer().getJob().getId() / 100) | 0) == 21) {
         if (!qm.haveItem(1142129)) {
            if (qm.canHold(1142129)) {
               qm.gainItem(1142129, (short) 1)
            } else {
               qm.dispose()
               return
            }
         }

         String medalname = qm.getMedalName()
         MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.PINK_TEXT, "<" + medalname + "> has been awarded.")
         qm.earnTitle("<" + medalname + "> has been awarded.")

         qm.forceStartQuest()
         qm.forceCompleteQuest()
      }

      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (qm.getPlayer().getLevel() >= 10 && ((qm.getPlayer().getJob().getId() / 100) | 0) == 21) {
         if (!qm.haveItem(1142129)) {
            if (qm.canHold(1142129)) {
               qm.gainItem(1142129, (short) 1)
            } else {
               qm.dispose()
               return
            }
         }

         String medalname = qm.getMedalName()
         MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.PINK_TEXT, "<" + medalname + "> has been awarded.")
         qm.earnTitle("<" + medalname + "> has been awarded.")

         qm.forceStartQuest()
         qm.forceCompleteQuest()
      }

      qm.dispose()
   }
}

Quest29924 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest29924(qm: qm))
   }
   return (Quest29924) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}