package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest3714 {
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
            if (!qm.haveItem(4001094, 1)) {
               qm.sendNext(I18nMessage.from("3714_YOU_DO_NOT_HAVE"))
               qm.dispose()
               return
            }

            if (qm.haveItem(2041200, 1)) {
               qm.sendOk(I18nMessage.from("3714_GROWN_BRIGHTER"))
               qm.dispose()
               return
            }

            qm.sendNext(I18nMessage.from("3714_YOU_HAVE_BROUGHT"))
         } else if (status == 1) {
            if (!qm.canHold(2041200, 1)) {
               qm.sendOk(I18nMessage.from("3714_MAKE_USE_ROOM"))
               qm.dispose()
               return
            }

            qm.forceCompleteQuest()
            qm.gainItem(4001094, (short) -1)
            qm.gainItem(2041200, (short) 1)
            qm.gainExp(42000)
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest3714 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3714(qm: qm))
   }
   return (Quest3714) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}