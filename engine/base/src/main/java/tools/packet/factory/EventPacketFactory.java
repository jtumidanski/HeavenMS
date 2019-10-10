package tools.packet.factory;

import net.opcodes.SendOpcode;
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
      registry.setHandler(RollSnowBall.class, packet -> this.rollSnowBall((RollSnowBall) packet));
      registry.setHandler(HitSnowBall.class, packet -> this.hitSnowBall((HitSnowBall) packet));
      registry.setHandler(SnowBallMessage.class, packet -> this.snowballMessage((SnowBallMessage) packet));
      registry.setHandler(CoconutScore.class, packet -> this.coconutScore((CoconutScore) packet));
      registry.setHandler(CoconutHit.class, packet -> this.hitCoconut((CoconutHit) packet));
   }

   protected byte[] rollSnowBall(RollSnowBall packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SNOWBALL_STATE.getValue());
      if (packet.enterMap()) {
         mplew.skip(21);
      } else {
         mplew.write(packet.state());// 0 = move, 1 = roll, 2 is down disappear, 3 is up disappear
         mplew.writeInt(packet.firstSnowmanHP() / 75);
         mplew.writeInt(packet.secondSnowmanHP() / 75);
         mplew.writeShort(packet.firstSnowBallPosition());//distance snowball down, 84 03 = max
         mplew.write(-1);
         mplew.writeShort(packet.secondSnowBallPosition());//distance snowball up, 84 03 = max
         mplew.write(-1);
      }
      return mplew.getPacket();
   }

   protected byte[] hitSnowBall(HitSnowBall packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.HIT_SNOWBALL.getValue());
      mplew.write(packet.what());
      mplew.writeInt(packet.damage());
      return mplew.getPacket();
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
   protected byte[] snowballMessage(SnowBallMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.SNOWBALL_MESSAGE.getValue());
      mplew.write(packet.team());// 0 is down, 1 is up
      mplew.writeInt(packet.message());
      return mplew.getPacket();
   }

   protected byte[] coconutScore(CoconutScore packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.COCONUT_SCORE.getValue());
      mplew.writeShort(packet.firstTeam());
      mplew.writeShort(packet.secondTeam());
      return mplew.getPacket();
   }

   protected byte[] hitCoconut(CoconutHit packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.COCONUT_HIT.getValue());
      if (packet.spawn()) {
         mplew.writeShort(-1);
         mplew.writeShort(5000);
         mplew.write(0);
      } else {
         mplew.writeShort(packet.id());
         mplew.writeShort(1000);//delay till you can attack again!
         mplew.write(packet.theType()); // What action to do for the coconut.
      }
      return mplew.getPacket();
   }
}