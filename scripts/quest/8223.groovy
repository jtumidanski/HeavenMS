package quest


import scripting.quest.QuestActionManager

class Quest8223 {
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
         qm.sendAcceptDecline("Oh, Jack sent you here? Good timing, I'm planning alongside Jack and others to storm the Keep and retake it from the Twisted Masters what is ours by right. You seem ready to fight alongside us, right?")
      } else if (status == 1) {
         qm.sendOk("Great! Your mission now is to rack down some numbers of their army and weaken their defenses by all effects. Defeat 75 of each: Windraider, Firebrand and Nightshadow, then return to me to report.")
         qm.forceStartQuest()
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest8223 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8223(qm: qm))
   }
   return (Quest8223) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}