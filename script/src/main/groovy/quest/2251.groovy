package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2251 {
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
            if (!qm.haveItem(4032399, 20)) {
               qm.sendOk(I18nMessage.from("2251_BRING_ME"))
            } else {
               qm.gainItem(4032399, (short) -20)
               qm.sendOk(I18nMessage.from("2251_YOU_BROUGHT_ME"))
               qm.gainExp(8000)
               qm.forceCompleteQuest()
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }
}

Quest2251 getQuest() {
   QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
   getBinding().setVariable("quest", new Quest2251(qm: qm))
   return (Quest2251) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}