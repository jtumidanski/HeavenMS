package quest
import tools.I18nMessage


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
            qm.sendNext(I18nMessage.from("3360_COME_ON"))
            qm.dispose()
            return
         }

         if (status == 0) {
            qm.sendNext(I18nMessage.from("3360_FINALLY"))
         } else if (status == 1) {
            qm.sendAcceptDecline(I18nMessage.from("3360_ALL_RIGHT"))
         } else if (status == 2) {
            pass = generateString()
            qm.sendOk(I18nMessage.from("3360_KEY_CODE").with(pass))
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