package MOHG.gun;

import robocode.*;
import robocode.util.Utils;
import MOHG.MOHG;

public class GunHandler {
    
    private MOHG robot;
    private static final double bulletSpeed = 20;

    public GunHandler(MOHG robot) {
        this.robot = robot;
    }

    public void aimAndFire(ScannedRobotEvent e) {
        double danoMohg = Math.min(400 / e.getDistance(), 3);
        double absoluteBearing = robot.getHeadingRadians() + e.getBearingRadians();
        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - robot.getGunHeadingRadians());

        if (e.getVelocity() != 0) {
            double[] futurePosition = predictEnemyPosition(e, absoluteBearing);
            double angleToFuture = Utils.normalRelativeAngle(Math.atan2(futurePosition[0] - robot.getX(), futurePosition[1] - robot.getY()));
            gunTurn = Utils.normalRelativeAngle(angleToFuture - robot.getGunHeadingRadians());
        }

        robot.setTurnGunRightRadians(gunTurn);
        robot.setFire(danoMohg);
    }

    private double[] predictEnemyPosition(ScannedRobotEvent e, double absoluteBearing) {
        double enemyHeading = e.getHeadingRadians();
        double enemyVelocity = e.getVelocity();
        double enemyX = robot.getX() + Math.sin(absoluteBearing) * e.getDistance();
        double enemyY = robot.getY() + Math.cos(absoluteBearing) * e.getDistance();

        double predictX = enemyX + Math.sin(enemyHeading) * enemyVelocity * (e.getDistance() / bulletSpeed);
        double predictY = enemyY + Math.cos(enemyHeading) * enemyVelocity * (e.getDistance() / bulletSpeed);

        return new double[]{predictX, predictY};
    }
}
