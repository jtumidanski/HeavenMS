package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Small Tree Stump
	Map(s): 		Victoria Road - Top of the Tree That Grew
	Description: 	Maybe it's Arwen!
*/


class NPC1032111 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if(mode == -1 || (mode == 0 && status == 0)){
         cm.dispose()
         return
      }
      else if(mode == 0)
         status--
      else
         status++


      if(status == 0){
         if(cm.isQuestStarted(20716)){
            if(!cm.hasItem(4032142)){
               if(cm.canHold(4032142)){
                  cm.gainItem(4032142, (short) 1)
                  cm.sendOk("You bottled up some of the clear tree sap.  #i4032142#")
               }
               else
                  cm.sendOk("Make sure you have a free spot in your ETC inventory.")
            }
            else
               cm.sendOk("A never ending flow of sap is coming from this small tree stump.")
         }
         else
            cm.sendOk("A never ending flow of sap is coming from this small tree stump.")
      }
      else if(status == 1){
         cm.dispose()
      }
   }
}

NPC1032111 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032111(cm: cm))
   }
   return (NPC1032111) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }