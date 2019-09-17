package quest


import scripting.quest.QuestActionManager

class Quest2560 {
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
         qm.sendNext("Ooook! Ook! Ook!")
      } else if (status == 1) {
         qm.sendNextPrev("Well, that hit the spot, but... I still don't understand what happened. Where's the ship? Hey, do you know what happened to me?", (byte) 2)
      } else if (status == 2) {
         qm.sendAcceptDecline("Oook! (The monkey nods. Does he really know what's going on? Couldn't hurt to ask.)")
      } else if (status == 3) {
         if (mode == 0) {//decline
            qm.sendNext("Ook! Ook! (The monkey looks very dissatisfied.)")
         } else {
            qm.forceStartQuest()
            qm.dispose()
         }
      } else if (status == 4) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2560 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2560(qm: qm))
   }
   return (Quest2560) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}