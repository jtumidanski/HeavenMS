package quest


import scripting.quest.QuestActionManager

class Quest21747 {
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
            qm.sendAcceptDecline("Who would have thought that the hero's successor would reappear after hundred of years...? Will you bring prosperity to Maple World or will you end its existence? I suppose it really doesn't matter. Alright, I'll tell you what I know about the Seal Stone of Mu Lung.")
         } else if (status == 1) {
            qm.sendNext("The Seal Stone of Mu Lung is located at the Sealed Temple. You will find the entrance deep inside the Mu Lung Temple. You can enter the Sealed Temple if you find the pillar with the word 'Entrance' wtritten on it. The password is: #bActions speak better than words#k. Maybe you will find the Shadow Knight there, as he probably is waiting for me there. I think the Hero's sucessor is more able to face him than myself, so prepare yourself.")
         } else {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
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
            qm.sendNext("So you have defeated the Shadow Knight. I have never doubted of your handiwork, and you handled the task well.")
         } else if (status == 1) {
            qm.sendNext("But yet, something made you unhappy. What could it be? ... No... Black Wings took away the Seal stone? I'm afraid nothing can be done anymore. I suggest you return to your group tactician, Tru is it?, and tell him about the situation now. Tell him about the loss here in Mu Lung. There's no time to lose, hurry!")
         } else if (status == 2) {
            qm.gainExp(16000)
            qm.forceCompleteQuest()

            qm.dispose()
         }
      }
   }
}

Quest21747 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21747(qm: qm))
   }
   return (Quest21747) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}