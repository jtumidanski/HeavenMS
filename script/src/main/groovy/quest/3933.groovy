package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest3933 {
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
            qm.sendNext(I18nMessage.from("3933_DID_NOT_THINK_YOU_WOULD_BE"))
         } else if (status == 1) {
            qm.sendAcceptDecline(I18nMessage.from("3933_TRULY_SEE_YOUR_STRENGTH"))
         } else if (status == 2) {
            qm.sendNext(I18nMessage.from("3933_LIKE_YOUR_CONFIDENCE"))
         } else if (status == 3) {
            if (qm.getWarpMap(926000000).getCharacters().size() > 0) {
               qm.sendOk(I18nMessage.from("3933_SOMEONE_CURRENTLY_IN_MAP"))
               qm.dispose()
            } else {
               qm.warp(926000000, "st00")
               qm.forceStartQuest()
               qm.dispose()
            }
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest3933 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3933(qm: qm))
   }
   return (Quest3933) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}