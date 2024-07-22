package MOHG;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

import MOHG.movement.MovementHandler;

public class MOHG extends AdvancedRobot {
    private static final double bulletSpeed = 20; // Speed of MOHG's bullets, adjusted for balance
    private boolean wallState = true; // State to determine wall avoidance direction
    private ScannedRobotEvent lastScannedEvent; // Store the last scanned robot event
    int distance = 300; // Distance to maintain from enemy

    private MovementHandler movementHandler;

    public void run() {
        // Make MOHG dark
        setBodyColor(Color.BLACK);
        setGunColor(Color.BLACK);
        setRadarColor(Color.BLACK);
        setBulletColor(Color.BLACK);
        setScanColor(Color.BLACK); 

        movementHandler = new MovementHandler(this);

        setAdjustRadarForGunTurn(true); // Allow radar to move independently from the gun
        while (true) {
            turnRadarRight(Double.POSITIVE_INFINITY); // Turn the radar to the right indefinitely
            execute(); // Execute all pending commands
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        lastScannedEvent = e; // Store the scanned robot event
        if (getEnergy() > 2.0) {
            // Radar tracking
            double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
            setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

            // Fire power calculation based on distance
            double danoMohg = Math.min(400 / e.getDistance(), 3);

            // Aiming calculation
            double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
            double gunTurn = Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());

            if (e.getVelocity() != 0) {
                // Predict the future position of the enemy
                double enemyHeading = e.getHeadingRadians(); // Enemy's direction
                double enemyVelocity = e.getVelocity(); // Enemy's velocity
                double enemyX = getX() + Math.sin(absoluteBearing) * e.getDistance(); // Calculate enemy's X position
                double enemyY = getY() + Math.cos(absoluteBearing) * e.getDistance(); // Calculate enemy's Y position

                double predictX = enemyX + Math.sin(enemyHeading) * enemyVelocity * (e.getDistance() / bulletSpeed); // Predict future X
                double predictY = enemyY + Math.cos(enemyHeading) * enemyVelocity * (e.getDistance() / bulletSpeed); // Predict future Y

                double angleToFuture = Utils.normalRelativeAngle(Math.atan2(predictX - getX(), predictY - getY())); // Calculate angle to future position
                gunTurn = Utils.normalRelativeAngle(angleToFuture - getGunHeadingRadians()); // Adjust gun turn for future position
            }

            setTurnGunRightRadians(gunTurn); // Turn the gun to the calculated angle
            setFire(danoMohg); // Fire with calculated power
        }

        if (getEnergy() <= 2.0) {
            // Evasive maneuver if energy is low
            double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
            double evasiveAngle = Math.toRadians(10) * Math.sin(getTime() / 10.0); // Evasive angle calculation
            double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - getHeadingRadians());

            setTurnRightRadians(angleToEnemy + evasiveAngle); // Turn right with evasive angle
            setAhead(Double.POSITIVE_INFINITY); // Move forward indefinitely
        } else {
            if (e.getDistance() < distance) { // Avoid enemy if within specified distance
                movementHandler.move(e, true, wallState);
            } else {
                // Zigzag movement to avoid being hit
                double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
                double evasiveAngle = Math.toRadians(10) * Math.sin(getTime() / 10.0); // Evasive angle calculation
                double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - getHeadingRadians());

                setTurnRightRadians(angleToEnemy + evasiveAngle); // Turn right with evasive angle
                setAhead(Double.POSITIVE_INFINITY); // Move forward indefinitely in a zigzag pattern
            }
        }

        execute(); // Execute all pending commands
    }

    public void onHitWall(HitWallEvent e) {
        wallState = !wallState; // Toggle wall state
        movementHandler.move(lastScannedEvent, wallState, wallState);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // Actions when hit by a bullet
        setAdjustRadarForGunTurn(true); // Allow radar to move independently from the gun
        // Radar tracking
        double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

        // Aiming calculation
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());

        setTurnRightRadians(Utils.normalRelativeAngle(getHeadingRadians() - e.getBearingRadians() + Math.PI / 2)); // Turn right perpendicular to the bullet direction
        setBack(20); // Move back slightly
        setAhead(100); // Move forward to dodge
    }

    public void onWin(WinEvent event) {
        while (true) {
            setTurnRight(Double.POSITIVE_INFINITY); // Spin indefinitely until the round ends
            setBodyColor(Color.getHSBColor((float) Math.random(), 1, 1)); // Change the tank's body color randomly
            execute(); // Execute pending commands
        }
    }
}
