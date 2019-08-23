package quest


import scripting.quest.QuestActionManager

class Quest8225 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("Okay, then. See you around.")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("Hey, partner. Now that you make part of the Raven Claws team, I have a task for you. Are you up now?")
      } else if (status == 1) {
         qm.sendOk("Very well. To prove your valor among our ranks, you must first pass on a little challenge: you have to be able to move extraordinaly well around here, known of all secrets these woods holds. Trace a #bmap of the Phantom Forest#k, then come talk to me. I shall then evaluate if you're worth to be with us.")
         qm.forceStartQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest8225 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8225(qm: qm))
   }
   return (Quest8225) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}