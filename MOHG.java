package MOHG;

import robocode.*;

public class MOHG extends AdvancedRobot {
    public void run() {
        turnRadarRightRadians(Double.POSITIVE_INFINITY); 
        do {
            scan();
        } while (true);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double radarTurn = getHeadingRadians() + e.getBearingRadians()
                - getRadarHeadingRadians();
        double fireTurn = getHeadingRadians() + e.getBearingRadians()
                - getGunHeadingRadians();

        setTurnGunRightRadians(Utils.normalRelativeAngle(fireTurn));
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
    }
    // public void onFire(ScannedRobotEvent e) {
    // double fireTurn =
    // getHeadingRadians() + e.getBearingRadians()
    // - getGunHeadingRadians();
    // fire(1);

    // setTurnGunRightRadians(Utils.normalRelativeAngle(fireTurn));
    // }
}