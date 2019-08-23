package quest


import scripting.quest.QuestActionManager

class Quest3927 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }


         if (qm.getQuestProgress(3927) == 0) {    // didn't find the wall yet, eh?
            qm.sendOk("Did you find the wall? Look closely, the wall is more near than you think!")
            qm.dispose()
            return
         }

         if (status == 0) {
            qm.sendSimple("Did you find the wall?\r\n#L0##b I did, but... I have no idea what it's talking about.#l")
         } else if (status == 1) {
            qm.sendSimple("What did it say?\r\n#L0##b 'If I had an iron hammer and a dagger, a bow and an arrow...'#l\r\n#L1# 'Byron S2 Sirin'#l\r\n#L2# 'Ahhh I forgot.'")
         } else if (status == 2) {
            if (selection == 0) {
               qm.sendOk("If I had an iron hammer and a dagger... a bow and an arrow... what does that mean? Do you want me to tell you? I don't know myself. It's something you should think about. If you need a clue... it would go something like... a weapon is just an item... until someone uses it...?")
            } else if (selection == 1) {
               qm.sendOk("Man, Jiyur wrote on the wall again! Arrgh!!")
               qm.dispose()
            } else {
               qm.sendOk("What? You forgot? Do you remember where it was written?")
               qm.dispose()
            }
         } else if (status == 3) {
            qm.gainExp(1000)
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest3927 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3927(qm: qm))
   }
   return (Quest3927) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}