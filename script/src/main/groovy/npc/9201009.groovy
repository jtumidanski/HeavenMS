package npc


import scripting.AbstractPlayerInteraction
import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201009 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   EventInstanceManager eim
   boolean hasEngage
   boolean hasRing

   def start() {
      eim = cm.getEventInstance()
      if (eim == null) {
         cm.warp(680000000, 0)
         cm.dispose()
         return
      }

      if (cm.getMapId() == 680000200) {
         if (eim.getIntProperty("weddingStage") == 0) {
            cm.sendNext("The guests are gathering here right now. Please wait awhile, the ceremony will start soon enough.")
         } else {
            cm.warp(680000210, "sp")
            cm.sendNext("Pick your seat over here and good show!")
         }

         cm.dispose()
      } else {
         if (cm.getPlayer().getId() != eim.getIntProperty("groomId") && cm.getPlayer().getId() != eim.getIntProperty("brideId")) {
            cm.sendNext("Sorry, only the marrying couple should be talking to me right now.")
            cm.dispose()
            return
         }

         hasEngage = false
         for (int i = 4031357; i <= 4031364; i++) {
            if (cm.haveItem(i)) {
               hasEngage = true
               break
            }
         }

         int[] rings = [1112806, 1112803, 1112807, 1112809]
         hasRing = false
         for (int i = 0; i < rings.length; i++) {
            if (cm.getPlayer().haveItemWithId(rings[i], true)) {
               hasRing = true
            }
         }

         status = -1
         action((byte) 1, (byte) 0, 0)
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0) {
         cm.sendOk("Goodbye then.")
         cm.dispose()
         return
      } else if (mode == 1) {
         status++
      } else {
         status--
      }

      if (status == 0) {
         boolean hasGoldenLeaf = cm.haveItem(4000313)

         if (hasGoldenLeaf && hasEngage) {
            cm.sendOk("You can't leave yet! You need to click Pelvis Bebop and get his word before I can let you leave.")
            cm.dispose()
         } else if (hasGoldenLeaf && hasRing) {
            String[] choice = ["Go to the after party", "What should I be doing"]
            String msg = "What can I help you with?#b"
            for (int i = 0; i < choice.length; i++) {
               msg += "\r\n#L" + i + "#" + choice[i] + "#l"
            }
            cm.sendSimple(msg)
         } else {
            cm.sendNext("You don't seem to have a Gold Maple Leaf, engagement ring, or wedding ring. You must not belong here, so I will take you to Amoria.")
         }
      } else if (status == 1) {
         AbstractPlayerInteraction cmPartner = cm.getMap().getCharacterById(cm.getPlayer().getPartnerId()).getAbstractPlayerInteraction()

         switch (selection) {
            case 0:
               if (eim.getIntProperty("isPremium") == 1) {
                  eim.warpEventTeam(680000300)
                  cm.sendOk("Enjoy! Cherish your Photos Forever!")
                  if (cmPartner != null) {
                     cmPartner.npcTalk(cm.getNpc(), "Enjoy! Cherish your Photos Forever!")
                  }
               } else {    // skip the party-time (premium only)
                  eim.warpEventTeam(680000500)
                  cm.sendOk("Congratulations for the newly-wed! I will escort you to the exit.")
                  if (cmPartner != null) {
                     cmPartner.npcTalk(cm.getNpc(), "Congratulations for the newly-wed! I will escort you to the exit.")
                  }
               }

               cm.dispose()
               break

            case 1:
               cm.sendOk("The superstars must receive the word of Pelvis Bebop to be united. When you are ready you can click me to go to the after party.")
               cm.dispose()
               break

            default:
               cm.warp(680000000, 0)
               cm.dispose()
               break
         }
      }
   }
}

NPC9201009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201009(cm: cm))
   }
   return (NPC9201009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }