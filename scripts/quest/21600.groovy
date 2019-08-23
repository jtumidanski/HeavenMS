package quest


import scripting.quest.QuestActionManager

class Quest21600 {
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
            qm.sendNext("Hey, Aran. You seem pretty strong, since that time from when you got freed from the glacier. Suitable enough to #bride a wolf#k, if you ask me.")
         } else if (status == 1) {
            qm.sendAcceptDecline("Picked your interest, huh? Very well, first you must make your way to #bAqua#k, there is a person there who makes #rfood for wolf cubs#k. Bring one portion to me, and I shall deem you able to tame and take care of one. What do you say, will you try for it?")
         } else if (status == 2) {
            qm.sendNext("Alright. The one you must meet is #bNanuke#k, she is on top of a #rsnowy whale#k, somewhere in the ocean. Good luck!")
            qm.forceStartQuest()

            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21600 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21600(qm: qm))
   }
   return (Quest21600) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}