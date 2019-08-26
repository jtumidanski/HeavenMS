package quest


import scripting.quest.QuestActionManager

class Quest20200 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (status == 0 && mode == 0) {
            qm.sendNext("Hmmm... do you feel like you still have missions to take care of as a trainee? I commend your level of patience, but this has gone too far. Cygnus Knights is in dire need of new, more powerful knights.")
            qm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendAcceptDecline("#h0#? Wow, your level has sky-rocketed since the last time I saw you. You also look like you've taken care of a number of missions as well... You seem much more ready to move on now than the last time I saw you. What do you think? Are you interested in taking the #bKnighthood Exam#k? It's time for you to grow out of the Knight-in-Training and become a bonafide Knight, right?")
         } else if (status == 1) {
            qm.startQuest()
            qm.completeQuest()
            qm.sendOk("If you wish to take the Knighthood Exam, please come to Ereve. Each Chief Knight will test your abilities, and if you meet their standards, then you will officially become a Knight.")
         } else if (status == 2) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20200 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20200(qm: qm))
   }
   return (Quest20200) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}