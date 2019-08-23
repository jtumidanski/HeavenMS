package quest


import scripting.quest.QuestActionManager

class Quest21740 {
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
            qm.sendNext("The Orbis seal has been stolen by the Black Wings? Hmm, that has gone awry. Go tell #bLilin#k about this, she must have something in mind on this situation.")
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
            qm.sendNext("Oh, hi #h0#! You won't believe what I just uncovered. It's one of your lost skills... What, the seal of Orbis got stolen by the Black Wings? Oh my...")
         } else if (status == 1) {
            qm.sendNext("For now, let me teach you the #bCombo Smash#k, with it you will be able to deal massive amount of damage to many monsters at once. We will need to use it if we want to stand a chance against the Black Wings now, so don't forget it!")
         } else {
            qm.teachSkill(21100004, (byte) 0, (byte) 20, -1) // combo smash

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest21740 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21740(qm: qm))
   }
   return (Quest21740) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}