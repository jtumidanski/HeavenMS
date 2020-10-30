package net.server.channel.handlers;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import constants.skills.Gunslinger;
import constants.skills.NightWalker;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.GrenadeEffectPacket;
import net.server.channel.packet.reader.GrenadeEffectReader;
import tools.MasterBroadcaster;
import tools.packet.attack.ThrowGrenade;

public class GrenadeEffectHandler extends AbstractPacketHandler<GrenadeEffectPacket> {
   @Override
   public Class<GrenadeEffectReader> getReaderClass() {
      return GrenadeEffectReader.class;
   }

   @Override
   public void handlePacket(GrenadeEffectPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      Point position = new Point(packet.x(), packet.y());
      switch (packet.skillId()) {
         case NightWalker.POISON_BOMB:
         case Gunslinger.GRENADE:
            int skillLevel = chr.getSkillLevel(packet.skillId());
            if (skillLevel > 0) {
               MasterBroadcaster.getInstance().sendToAllInMapRange(chr.getMap(), new ThrowGrenade(chr.getId(), position, packet.keyDown(), packet.skillId(), skillLevel), chr, position);
            }
            break;
         default:
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.UNHANDLED_EVENT, "The skill id: " + packet.skillId() + " is not coded in " + this.getClass().getName() + ".");
      }
   }
}