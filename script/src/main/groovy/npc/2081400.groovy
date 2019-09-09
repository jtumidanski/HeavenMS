package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2081400 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (cm.getLevel() < 120 || Math.floor(cm.getJobId() / 100) != 4) {
               cm.sendOk("Please don't bother me right now, I am trying to concentrate.")
               cm.dispose()
            } else if (!cm.isQuestCompleted(6934)) {
               cm.sendOk("You have not yet passed my trials. I can not advance you until you do so.")
               cm.dispose()
            } else if (cm.getJobId() % 100 % 10 != 2) {
               cm.sendYesNo("You did a marvellous job passing my test. Are you ready to advance to your 4th job?")
            } else {
               cm.sendSimple("If I must, I can teach you the art of your class.\r\n#b#L0#Teach me the skills of my class.#l")
               //cm.dispose();
            }
         } else if (status == 1) {
            if (mode >= 1 && cm.getJobId() % 100 % 10 != 2) {
               if (cm.canHold(2280003, 1)) {
                  cm.changeJobById(cm.getJobId() + 1)
                  if (cm.getJobId() == 412) {
                     cm.teachSkill(4120002, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(4120005, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(4121006, (byte) 0, (byte) 10, -1)
                  } else if (cm.getJobId() == 422) {
                     cm.teachSkill(4220002, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(4220005, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(4221007, (byte) 0, (byte) 10, -1)
                  }
                  cm.gainItem(2280003, (short) 1)
               } else {
                  cm.sendOk("Please have one slot available on #bUSE#k inventory to receive a skill book.")
               }
            } else if (mode >= 1 && cm.getJobId() % 100 % 10 == 2) {
               if (cm.getJobId() == 412) {
                  if (cm.getPlayer().getSkillLevel(4121008) == 0) {
                     cm.teachSkill(4121008, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(4121004) == 0) {
                     cm.teachSkill(4121004, (byte) 0, (byte) 10, -1)
                  }
               } else if (cm.getJobId() == 422) {
                  if (cm.getPlayer().getSkillLevel(4221004) == 0) {
                     cm.teachSkill(4221004, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(4221001) == 0) {
                     cm.teachSkill(4221001, (byte) 0, (byte) 10, -1)
                  }
               }
               cm.sendOk("It is done. Leave me now.")
            }

            cm.dispose()
         }
      }
   }
}

NPC2081400 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2081400(cm: cm))
   }
   return (NPC2081400) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }