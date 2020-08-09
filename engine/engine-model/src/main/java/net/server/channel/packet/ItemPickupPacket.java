package net.server.channel.packet; 
import java.awt.Point;

import net.server.MaplePacket;

public record ItemPickupPacket(Integer timestamp, Point characterPosition, Integer objectId) implements MaplePacket {
}
