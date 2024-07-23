package Beggers.radar;

import robocode.*;
import robocode.util.Utils;
import Beggers.Beggers;

public class RadarHandler {
    
    private Beggers robot;

    public RadarHandler(Beggers robot) {
        this.robot = robot;
    }

    public void trackRadar(ScannedRobotEvent e) {
        double radarTurn = robot.getHeadingRadians() + ((ScannedRobotEvent) e).getBearingRadians() - robot.getRadarHeadingRadians();
        robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
    }
}
