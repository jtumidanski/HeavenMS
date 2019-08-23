package quest


import scripting.quest.QuestActionManager

class Quest8224 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("Okay, then. See you around.")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("Hey traveler, come here! I am Taggrin, leader of the Raven Ninja Clan. We are mercenaries currently under the payload of the New Leaf City county. Our job here is to hunt down those creatures that have been lurking around here these days. Are you interested to make a little errand for us? Of course, the pay off will be advantageous for both parties.")
      } else if (status == 1) {
         qm.sendOk("Ok. I need you to hunt down #bthose fake trees#k in the forest, and collect 50 of their drops as proof that you made your part on this.")
         qm.forceStartQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest8224 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8224(qm: qm))
   }
   return (Quest8224) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}