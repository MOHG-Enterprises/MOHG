package MOHG;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

import MOHG.movement.MovementHandler;
import MOHG.radar.RadarHandler;
import MOHG.gun.GunHandler;

public class MOHG extends AdvancedRobot {
    private static final double bulletSpeed = 20; // Speed of MOHG's bullets, adjusted for balance
    private boolean wallState = true; // State to determine wall avoidance direction
    private ScannedRobotEvent lastScannedEvent; // Store the last scanned robot event
    int distance = 300; // Distance to maintain from enemy

    private MovementHandler movementHandler;
    private RadarHandler radarHandler;
    private GunHandler gunHandler;

    public void run() {
        // Make MOHG dark
        setBodyColor(Color.BLACK);
        setGunColor(Color.BLACK);
        setRadarColor(Color.BLACK);
        setBulletColor(Color.BLACK);
        setScanColor(Color.BLACK);

        movementHandler = new MovementHandler(this);
        radarHandler = new RadarHandler(this);
        gunHandler = new GunHandler(this);

        setAdjustRadarForGunTurn(true); // Allow radar to move independently from the gun
        while (true) {
            turnRadarRight(Double.POSITIVE_INFINITY); // Turn the radar to the right indefinitely
            execute(); // Execute all pending commands
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        lastScannedEvent = e; // Store the scanned robot event
        if (getEnergy() > 2.0) {
            radarHandler.trackRadar(e);
            gunHandler.aimAndFire(e);
        } else {
            evasiveManeuver(e);
        }

        if (e.getDistance() < distance) {
            movementHandler.move(e, true, wallState);
        } else {
            evasiveMovement(e);
        }

        execute(); // Execute all pending commands
    }

    private void evasiveManeuver(ScannedRobotEvent e) {
        double absoluteBearing = getHeadingRadians() + ((ScannedRobotEvent) e).getBearingRadians();
        double evasiveAngle = Math.toRadians(10) * Math.sin(getTime() / 10.0);
        double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - getHeadingRadians());

        setTurnRightRadians(angleToEnemy + evasiveAngle);
        setAhead(Double.POSITIVE_INFINITY);
    }

    private void evasiveMovement(ScannedRobotEvent e) {
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double evasiveAngle = Math.toRadians(10) * Math.sin(getTime() / 10.0);
        double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - getHeadingRadians());

        setTurnRightRadians(angleToEnemy + evasiveAngle);
        setAhead(Double.POSITIVE_INFINITY);
    }

    public void onHitWall(HitWallEvent e) {
        wallState = !wallState; // Toggle wall state
        movementHandler.move(lastScannedEvent, wallState, wallState);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        radarHandler.trackRadar(lastScannedEvent);
        evasiveManeuver(lastScannedEvent);
    }

    public void onWin(WinEvent event) {
        while (true) {
            setTurnRight(Double.POSITIVE_INFINITY); // Spin indefinitely until the round ends
            setBodyColor(Color.getHSBColor((float) Math.random(), 1, 1)); // Change the tank's body color randomly
            execute(); // Execute pending commands
        }
    }
}
