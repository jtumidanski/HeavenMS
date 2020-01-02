package quest


import scripting.quest.QuestActionManager

class Quest20001 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendNext("Hello, #h #. I formally welcome you to the Cygnus Knights. My name is Neinheart Von Rubistein, the Head Tactician for the young Empress. I will be seeing you often from here on out, so I suggest you remember my name. Haha...")
         } else if (status == 1) {
            qm.sendNextPrev("I understand that you didn't have enough time and exposure to figure out what you really need to do as a Cygnus Knight. I will explain it to you in detail, one by one. I will explain where you are, who the young Empress is, and what our duties are...")
         } else if (status == 2) {
            qm.sendNextPrev("You're standing on an island called Ereve, the only land that's governed by the young Empress that also happens to float in the air. Yes, we're floating in the air as we speak. We stay here out of necessity, but it usually works as a ship that floats all over Maple World, for the sake of the young Empress...")
         } else if (status == 3) {
            qm.sendNextPrev("The young Empress is indeed the ruler of Maple World, the one and only governor of this world. What? You've never heard of such a thing? Ahhh, that's understandable. The young Empress may govern this world, but she's not a dictator that looms over everyone. She uses Ereve as a way for her to oversee the world as an observer without having to be too hands-on. That's how it usually is, anyway...")
         } else if (status == 4) {
            qm.sendNextPrev("But situations arise every now and then where she'll have to take control. The evil Black Magician has been showing signs of resurrection all over the world. The very king of destruction that threatened to destroy the world as we know it is trying to reappear into our lives.")
         } else if (status == 5) {
            qm.sendNextPrev("The problem is, no one is aware of it. It's been so long since the Black Magician disappeared, that people have become used to peace in the world, not necessarily knowing what to do if a crisis like this reaches. If this keeps up, our world will be in grave danger in no time.")
         } else if (status == 6) {
            qm.sendNextPrev("That's when the young Empress decided to step forward and take control of this potential crisis before it revealed itself. She decided to create a group of Knights that will prevent the Black Magician from being fully resurrected. I'm sure you know of what happens afterwards since you volunteered to become a Knight yourself.")
         } else if (status == 7) {
            qm.sendNextPrev("Our duties are simple. We need to make ourselves more powerful; much more powerful than the state we're in right now, so that when the Black Magician returns, we'll battle him and eliminate him once and for all before he puts the whole world in grave danger. That is our goal, our mission, and therefore yours as well")
         } else if (status == 8) {
            qm.sendAcceptDecline("This is the basic overview of this situation. Understood?")
         } else if (status == 9) {
            if (qm.isQuestCompleted(20001)) {
               qm.gainExp(40)
               qm.gainItem(1052177, (short) 1) // fancy noblesse robe
            }
            qm.forceStartQuest()
            qm.forceCompleteQuest()
            qm.sendNext("I'm glad you understand what I've told you but... did you know? Based on your current level, you won't be able to face the Black Magician. Heck you won't be able to face off his disciple's slave's monster's pet's dummy! Are you sure you are ready to protect Maple World like that?")
         } else if (status == 10) {
            qm.sendNextPrev("You may be a member of the Cygnus Knights, but that doesn't mean you're a knight. Forget being the official knight. You're not even a Knight-in-Training, yet. A lot of time will pass where you will just sit around here, doing paperwork for the Cygnus Knights, but...")
         } else if (status == 11) {
            qm.sendNextPrev("But then again, no one is born strong, anyway. The Empress also prefers that she creates an environment where a string of powerful knights can be nurtured and created, as opposed to finding a supernaturally-gifted knight. For now, you'll have to become a Knight-in-Training, and make yourself much more powerful so you'll become useful later on. We'll talk about the duties of being a Cygnus Knight once you reach that level of competency.")
         } else if (status == 12) {
            qm.sendPrev("Take the portal on the left side and go straight, and you'll head towards #b Training Forest I # . There, you'll find the training instructor for the Knights, Kiku. The next time I see you, I'd like for you to be at least at level 10.")
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20001 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20001(qm: qm))
   }
   return (Quest20001) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}