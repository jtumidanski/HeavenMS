package quest
import tools.I18nMessage

import scripting.event.EventManager
import scripting.quest.QuestActionManager

class Quest2245 {
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
            EventManager em = qm.getEventManager("BalrogQuest")
            if (em == null) {
               qm.sendOk(I18nMessage.from("2245_SORRY"))
               return
            }

            if (!em.startInstance(qm.getPlayer())) {
               qm.sendOk(I18nMessage.from("2245_CURRENTLY_SOMEONE_IN_MAP"))
            } else {
               qm.forceStartQuest()
               qm.dispose()
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2245 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2245(qm: qm))
   }
   return (Quest2245) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}