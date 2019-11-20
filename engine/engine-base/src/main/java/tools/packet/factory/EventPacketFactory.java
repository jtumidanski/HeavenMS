package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.event.CoconutHit;
import tools.packet.event.CoconutScore;
import tools.packet.event.HitSnowBall;
import tools.packet.event.RollSnowBall;
import tools.packet.event.SnowBallMessage;

public class EventPacketFactory extends AbstractPacketFactory {
   private static EventPacketFactory instance;

   public static EventPacketFactory getInstance() {
      if (instance == null) {
         instance = new EventPacketFactory();
      }
      return instance;
   }

   private EventPacketFactory() {
      Handler.handle(RollSnowBall.class).decorate(this::rollSnowBall).register(registry);
      Handler.handle(HitSnowBall.class).decorate(this::hitSnowBall).size(7).register(registry);
      Handler.handle(SnowBallMessage.class).decorate(this::snowballMessage).size(7).register(registry);
      Handler.handle(CoconutScore.class).decorate(this::coconutScore).size(6).register(registry);
      Handler.handle(CoconutHit.class).decorate(this::hitCoconut).size(7).register(registry);
   }

   protected void rollSnowBall(MaplePacketLittleEndianWriter writer, RollSnowBall packet) {
      if (packet.enterMap()) {
         writer.skip(21);
      } else {
         writer.write(packet.state());// 0 = move, 1 = roll, 2 is down disappear, 3 is up disappear
         writer.writeInt(packet.firstSnowmanHP() / 75);
         writer.writeInt(packet.secondSnowmanHP() / 75);
         writer.writeShort(packet.firstSnowBallPosition());//distance snowball down, 84 03 = max
         writer.write(-1);
         writer.writeShort(packet.secondSnowBallPosition());//distance snowball up, 84 03 = max
         writer.write(-1);
      }
   }

   protected void hitSnowBall(MaplePacketLittleEndianWriter writer, HitSnowBall packet) {
      writer.write(packet.what());
      writer.writeInt(packet.damage());
   }

   /**
    * Sends a Snowball Message<br>
    * <p>
    * Possible values for <code>message</code>:<br> 1: ... Team's snowball has
    * passed the stage 1.<br> 2: ... Team's snowball has passed the stage
    * 2.<br> 3: ... Team's snowball has passed the stage 3.<br> 4: ... Team is
    * attacking the snowman, stopping the progress<br> 5: ... Team is moving
    * again<br>
    */
   protected void snowballMessage(MaplePacketLittleEndianWriter writer, SnowBallMessage packet) {
      writer.write(packet.team());// 0 is down, 1 is up
      writer.writeInt(packet.message());
   }

   protected void coconutScore(MaplePacketLittleEndianWriter writer, CoconutScore packet) {
      writer.writeShort(packet.firstTeam());
      writer.writeShort(packet.secondTeam());
   }

   protected void hitCoconut(MaplePacketLittleEndianWriter writer, CoconutHit packet) {
      if (packet.spawn()) {
         writer.writeShort(-1);
         writer.writeShort(5000);
         writer.write(0);
      } else {
         writer.writeShort(packet.id());
         writer.writeShort(1000);//delay till you can attack again!
         writer.write(packet.theType()); // What action to do for the coconut.
      }
   }
}