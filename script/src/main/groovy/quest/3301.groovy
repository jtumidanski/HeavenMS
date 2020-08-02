package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest3301 {
   QuestActionManager qm
   int status = -1
   int[] oreArray

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
            oreArray = getOreArray()

            if (oreArray.length > 0) {
               qm.sendSimple("Oh, looks like someone's ready to make a deal. You want to join Zenumist so badly, huh? I really don't understand you, but that's just fine. What will you give me in return?\r\n" + getOreString(oreArray))
            } else {
               qm.sendOk(I18nMessage.from("3301_NO_ORE_NO_DEAL"))
               qm.dispose()
            }
         } else if (status == 1) {
            if (!qm.haveItem(oreArray[selection], 2)) {
               qm.sendNext(I18nMessage.from("3301_NO_ORE_NO_DEAL_2"))
               qm.dispose()
               return
            }
            qm.gainItem(oreArray[selection], (short) -2) // Take 2 ores
            qm.sendNext(I18nMessage.from("3301_WAIT_FOR_A_WHILE"))
            qm.forceCompleteQuest()
         } else if (status == 2) {
            qm.dispose()
         }
      }
   }

   def getOreArray() {
      int[] ores = []
      int y = 0
      for (int x = 4020000; x <= 4020008; x++) {
         if (qm.haveItem(x, 2)) {
            ores[y] = x
            y++
         }
      }
      return ores
   }

   static def getOreString(int[] ids) { // Parameter 'ids' is just the array of getOreArray()
      String theString = "#b"
      String extra
      for (int x = 0; x < ids.length; x++) {
         extra = "#L" + x + "##t" + ids[x] + "##l\r\n"
         theString += extra
      }
      theString += "#k"
      return theString
   }
}

Quest3301 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3301(qm: qm))
   }
   return (Quest3301) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}