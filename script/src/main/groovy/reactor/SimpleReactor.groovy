package reactor

import scripting.reactor.ReactorActionManager

class SimpleReactor {
   ReactorActionManager rm

   def act() {
      rm.dropItems()
   }

   def hit() {
   }

   def touch() {
   }

   def release() {
   }
}
