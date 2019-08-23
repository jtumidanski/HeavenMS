package quest


import scripting.quest.QuestActionManager

class Quest21741 {
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
            qm.sendNext("Have you been advancing your levels? I found an interesting piece of information about the Black Wings. This time, you'll have to travel quite a bit. Do you know a town called #bMu Lung#k? You'll have to head there.")
         } else if (status == 1) {
            qm.sendAcceptDecline("Apparently, #bMr. Do#k in Mu Lung somehow met with the Black Wings. I don't know the details. Please go and find out why the Black Wings contacted Mr. Do and what exactly happened between them.")
         } else {
            qm.sendNext("Mr. Do is known to be curt, so you are going to have to remain patient while talking to him. Talk to him with the #bI heard you met the Shadow Knight of the Black Wings#k keyword.")

            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21741 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21741(qm: qm))
   }
   return (Quest21741) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}