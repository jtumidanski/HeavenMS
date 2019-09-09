package quest


import scripting.quest.QuestActionManager

class Quest2316 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("Why did you even ask if you were going to say no to this?#")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("I think i've heard of a potion that breaks these kinds of barriers. I think it's called #bKiller Mushroom Spores#k? Hmmm... outside, you'll find the Mushroom Scholar #bScarrs#k waiting outside. #bScarrs#k is an expert on mushrooms, so go talk to him.")
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk("I am confident #kScarrs#k will do everything to help you.")
      } else if (status == 2) {
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
         qm.sendOk("Ah, so you're the explorer people were talking about. I'm #bScarrs, the Royal Mushroom Scholar#k representing the Kingdom of Mushroom. So you need some #kKiller Mushroom Spores#k?")
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(4200)
         qm.sendOk("#kKiller Mushroom Spores#k... I think i've heard of them before...")
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2316 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2316(qm: qm))
   }
   return (Quest2316) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}