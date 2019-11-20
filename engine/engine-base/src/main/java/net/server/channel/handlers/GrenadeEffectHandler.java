/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.server.channel.handlers;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import constants.skills.Gunslinger;
import constants.skills.NightWalker;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.GrenadeEffectPacket;
import net.server.channel.packet.reader.GrenadeEffectReader;
import tools.FilePrinter;
import tools.MasterBroadcaster;
import tools.packet.attack.ThrowGrenade;

/*
 * @author GabrielSin
 */
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
            FilePrinter.printError(FilePrinter.UNHANDLED_EVENT, "The skill id: " + packet.skillId() + " is not coded in " + this.getClass().getName() + ".");
      }
   }
}