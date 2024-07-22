package MOHG.movement;

import robocode.*;
import robocode.util.Utils;
import MOHG.Main;

public class MovementHandler {
    private Main robot;

    public MovementHandler(Main robot) {
        this.robot = robot;
    }

    public void move(ScannedRobotEvent e, boolean goRight, boolean wallState) {
        double absoluteBearing = robot.getHeadingRadians() + e.getBearingRadians();
        double evasiveAngle = Math.toRadians(10) * Math.sin(robot.getTime() / 10.0);
        double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - robot.getHeadingRadians());

        if (goRight) {
            if (wallState) {
                robot.setAhead(Double.POSITIVE_INFINITY);
            }
            robot.setTurnRightRadians(Utils.normalRelativeAngle(absoluteBearing + Math.PI / 2 - robot.getHeadingRadians()));
        } else {
            robot.setBack(Double.POSITIVE_INFINITY);
            robot.setTurnLeftRadians(Utils.normalRelativeAngle(absoluteBearing + Math.PI / 2 - robot.getHeadingRadians()));
        }
    }
}
