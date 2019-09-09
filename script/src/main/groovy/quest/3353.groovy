package quest


import scripting.quest.QuestActionManager

class Quest3353 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
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
            qm.sendNext("I see. De Lang wants to stop the Huroids from causing more destruction, but the societies would like to get him on jail at once. So that's why he hid himself there.")
         } else if (status == 1) {
            qm.sendAcceptDecline("In that case, go there again and hear more details from De Lang, will you?")
         } else if (status == 2) {
            qm.warp(926120200, 1)

            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }
}

Quest3353 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3353(qm: qm))
   }
   return (Quest3353) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}