package quest


import scripting.quest.QuestActionManager

class Quest3360 {
   QuestActionManager qm
   int status = -1
   String pass

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
            qm.sendNext("Come on, hurry up. Get your pen and paper out if you're not that smart!")
            qm.dispose()
            return
         }

         if (status == 0) {
            qm.sendNext("Oh! Finally you have come! I'm glad you are here in time. I have the master key for you to open the secret passage! Hahahaha! Isn't it amazing? Say it amazing!")
         } else if (status == 1) {
            qm.sendAcceptDecline("All right, now, this key is very long and complex. I need you to memorize it very well. I won't say again, so you'd better write it down somewhere. Are you ready?")
         } else if (status == 2) {
            pass = generateString()
            qm.sendOk("The key code is #b" + pass + "#k. Got that? Put the key into the door of the secret passage, and you will be able to walk around the passage freely.")
         } else if (status == 3) {
            qm.forceStartQuest()
            qm.setQuestProgress(3360, pass)
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
   }

   static def generateString() {
      String theString = ""
      String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
      int randomNumber
      for (int i = 0; i < 10; i++) {
         randomNumber = Math.floor(Math.random() * chars.length()).intValue()
         theString += chars.substring(randomNumber, randomNumber + 1)
      }
      return theString
   }
}

Quest3360 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3360(qm: qm))
   }
   return (Quest3360) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}