package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2124 {
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
            if (!qm.haveItem(4031619, 1)) {
               qm.sendOk(I18nMessage.from("2124_BRING_ME_THE_BOX"))
            } else {
               qm.gainItem(4031619, (short) -1)
               qm.sendOk(I18nMessage.from("2124_OH_YOU_BROUGHT"))
               qm.forceCompleteQuest()
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }
}

Quest2124 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2124(qm: qm))
   }
   return (Quest2124) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}