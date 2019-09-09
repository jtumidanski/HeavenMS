package quest


import scripting.quest.QuestActionManager

class Quest3354 {
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
            qm.sendAcceptDecline("I have a request for you. Can you ask #bMaed#k for a potion of my devise? Obviously, don't mention I have asked you that, that would be a problem. #bKeeny#k got an illness due to the contact with the Huroids, this have bothering me so much I couldn't give progress on my researches... Please #rbring her the potion#k, so that I could feel better and start making progress. I'm counting on you.")
         } else if (status == 1) {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest3354 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3354(qm: qm))
   }
   return (Quest3354) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}