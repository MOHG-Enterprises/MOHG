package MOHG.radar;

import robocode.*;
import robocode.util.Utils;
import MOHG.MOHG;

public class RadarHandler {
    
    private MOHG robot;

    public RadarHandler(MOHG robot) {
        this.robot = robot;
    }

    public void trackRadar(ScannedRobotEvent e) {
        double radarTurn = robot.getHeadingRadians() + ((ScannedRobotEvent) e).getBearingRadians() - robot.getRadarHeadingRadians();
        robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
    }
}
