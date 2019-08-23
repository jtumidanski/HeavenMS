package quest


import scripting.quest.QuestActionManager

class Quest20710 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
         return
      } else if (mode == 0 && status == 0) {
         qm.sendOk("What? Are you declining the mission? Fine, do it like that. I'll just report it straight to #p1101002#.")
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }


      if (status == 0) {
         qm.sendAcceptDecline("You don't really instill confidence in me, but since you're a Cygnus Knight and all... and since no one else can go on a search right now... Okay, let me explain to you what this mission is about.")
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20710 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20710(qm: qm))
   }
   return (Quest20710) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}