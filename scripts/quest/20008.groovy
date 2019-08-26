package quest


import scripting.quest.QuestActionManager

class Quest20008 {
   QuestActionManager qm
   int status = -1
   int choice1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         qm.dispose()
      }
      else if (mode > 0)
         status++
      if (status == 0)
         qm.sendSimple("Are you ready to take on a mission? If you can't pass this test, then you won't be able to call yourself a real Knight. Are you sure you can do this? If you are afraid to do this, let me know. I won't tell Neinheart. \r\n #L0#I'll try this later.#l \r\n #L1#I'm not afraid. Let's do this.#l")
      else if (status == 1) {
         if (selection == 0) {
            qm.sendNext("If you call yourself a Knight, then do not hesitate. Show everyone how much courage you have in you.")
            qm.dispose()
         } else if (selection == 1) {
            choice1 = selection
            qm.sendSimple("I'm glad you didn't run away, but... are you sure you want to become a Knight-in-Training? What I am asking is whether you're okay with being a Cygnus Knight, and therefore being tied to the Empress at all times? She may be an Empress, but she's also still just a kid. Are you sure you can fight for her? I won't let Neinheart know so just tell me what you really feel. \r\n #L2#If the Empress wants peace in the Maple World, then I'm down for whatever.#l \r\n #L3#As long as I can become a knight I'll endure whatever #l")
            qm.forceStartQuest()
            qm.forceCompleteQuest()
         }
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20008 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20008(qm: qm))
   }
   return (Quest20008) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}