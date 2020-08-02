package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22504 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("22504_THIS_IS_NOT_GOING_TO_WORK"))
      } else if (status == 1) {
         qm.sendNextPrev("#bBut I don't. It's not like age has anything to do with this...", (byte) 2)
      } else if (status == 2) {
         qm.sendAcceptDecline(I18nMessage.from("22504_MORE_EXPERIENCED"))
      } else if (status == 3) {
         if (mode == 0) {
            qm.sendNext(I18nMessage.from("22504_BETTER_LOOK_FOR_SOMEONE_OLDER_AND_WISER"))
         } else {
            qm.forceStartQuest()
            qm.sendNext(I18nMessage.from("22504_ASK_DAD"))
         }
      } else if (status == 4) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest22504 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22504(qm: qm))
   }
   return (Quest22504) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}