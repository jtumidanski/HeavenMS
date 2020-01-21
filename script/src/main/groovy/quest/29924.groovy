package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage
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

         String medalName = qm.getMedalName()
         MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MEDAL_AWARDED").with(medalName))
         qm.earnTitle("<" + medalName + "> has been awarded.")

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

         String medalName = qm.getMedalName()
         MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MEDAL_AWARDED").with(medalName))
         qm.earnTitle("<" + medalName + "> has been awarded.")

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