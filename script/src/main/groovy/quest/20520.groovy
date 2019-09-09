package quest


import scripting.quest.QuestActionManager

class Quest20520 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.dispose()
            return
         } else {
            qm.dispose()
            return
         }
      }

      if (status == 0) {
         qm.sendNext("Wow, you have already reach Level 50, yet why are you still walking around like that? I mean, you've reached Level 50, but you are still walking around with your own feet. That's unusual behavior for a Knight like you.")
      } else if (status == 1) {
         qm.sendAcceptDecline("Well, I suppose it's up to you, but by doing that, you also risk marring the pride and honor of the Empress. This is why I am here to give you a helpful pointer. It's called #bMonster Riding#k. Of course you're interested in this, right?")
      } else if (status == 2) {
         qm.forceStartQuest()
         qm.forceCompleteQuest()
         qm.sendOk("There's a special mount that only the Cygnus Knights can enjoy. If you are interested, visit #bEreve#k. I will give you more information on it.")
      } else if (status == 3) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20520 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20520(qm: qm))
   }
   return (Quest20520) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}