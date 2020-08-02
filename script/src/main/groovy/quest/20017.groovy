package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20017 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0 && type > 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         if (status == 2) {
            qm.sendNext(I18nMessage.from("20017_NOTHING_TO_WORRY_ABOUT"))
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("20017_WELCOME"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("20017_CALLED_PIYOS"))
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("20017_NO_MONSTERS_IN_EREVE"))
      } else if (status == 3) {
         qm.sendAcceptDecline(I18nMessage.from("20017_SEEM_PREPARED"))
      } else if (status == 4) {
         qm.guideHint(12)
         qm.forceStartQuest(20020)
         qm.forceCompleteQuest(20100)
         qm.forceStartQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20017 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20017(qm: qm))
   }
   return (Quest20017) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}