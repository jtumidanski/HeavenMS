package quest
import tools.I18nMessage

import client.MapleMount
import scripting.quest.QuestActionManager

class Quest20527 {
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
            MapleMount mount = qm.getPlayer().getMount()

            if (mount != null && mount.level() >= 3) {
               qm.forceCompleteQuest()
               qm.sendNext(I18nMessage.from("20527_HOW_TO_TRAIN"))
            } else {
               qm.sendNext(I18nMessage.from("20527_TRAIN_A_BIT_MORE"))
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20527 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20527(qm: qm))
   }
   return (Quest20527) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}