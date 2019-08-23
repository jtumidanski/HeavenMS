package quest


import scripting.quest.QuestActionManager

class Quest21719 {
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
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext("Aren't you the one that used to be in #m101000000# until not too long ago? I finally found you! Do you know how long it took for me to finally find you?", (byte) 8)
      } else if (status == 1) {
         qm.sendNextPrev("Who are you?", (byte) 2)
      } else if (status == 2) {
         qm.sendAcceptDecline("Me? If you want to know, stop by my cave. I'll even send you an invitation. You'll be directly sent to my cave as soon as you accept. Look forward to seeing you there.")
      } else if (status == 3) {
         qm.forceCompleteQuest()
         qm.warp(910510200, 0)
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest21719 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21719(qm: qm))
   }
   return (Quest21719) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}