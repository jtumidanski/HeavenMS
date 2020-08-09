package net.server;

import java.awt.Point;

public record AbsoluteMovementData(Point position, Byte stance) implements MovementData {
}
