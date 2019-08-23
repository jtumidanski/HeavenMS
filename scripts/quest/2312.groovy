package quest


import scripting.quest.QuestActionManager

class Quest2312 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("Hmmm... you must be unsure of your combat skills. We'll be here waiting for you, so come see us when you're ready.")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("We need your help, noble explorer. Our kingdom is currently facing a big threat, and we are in desperate need of a courageous explorer willing to fight for us, and that's how you ended up here. Please understand, though, that since we need place our faith in you, we'll have to test your skills first before we can stand firmly behind you. Will it be okay for you to do this for us?")
      }
      if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk("Keep moving forward, and you'll see #bRenegade Spores#k, the Spores that turned their backs on the Kingdom of Mushroom. We'd appreciate it if you can teach them a lesson or two, and bring back #b50 Mutated Spores#k in return.")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendOk("Did you teach those Renegade Spores a lesson?")
      }
      if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(11500)
         qm.gainItem(4000499, (short) -50)
         qm.sendOk("That was amazing. I apologize for doubting your abilities. Please save our Kingdom of Mushroom from this crisis!")
         qm.dispose()
      }
   }
}

Quest2312 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2312(qm: qm))
   }
   return (Quest2312) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}