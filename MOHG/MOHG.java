package MOHG;

import robocode.*;
import java.awt.Color;
import MOHG.movement.MovementHandler;
import MOHG.radar.RadarHandler;
import MOHG.gun.GunHandler;

public class MOHG extends AdvancedRobot {
    private boolean wallState = true;
    private ScannedRobotEvent lastScannedEvent;
    int distance = 300;

    private MovementHandler movementHandler;
    private RadarHandler radarHandler;
    private GunHandler gunHandler;

    public void run() {
        movementHandler = new MovementHandler(this);
        radarHandler = new RadarHandler(this);
        gunHandler = new GunHandler(this);

        setAdjustRadarForGunTurn(true);

        setGunColor(Color.BLACK);
		setBulletColor(Color.BLACK);
		setRadarColor(Color.BLACK);
		setScanColor(Color.BLACK);
		setBodyColor(Color.BLACK);

        while (true) {
            turnRadarRight(Double.POSITIVE_INFINITY);
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        lastScannedEvent = e;
        if (getEnergy() > 2.0) {
            radarHandler.trackRadar(e);
            gunHandler.aimAndFire(e);
        } else {
            movementHandler.evasiveManeuver(e);
        }

        if (e.getDistance() < distance) {
            movementHandler.move(e, true, wallState);
        } else {
            movementHandler.evasiveMovement(e);
        }

        execute();
    }

    public void onHitWall(HitWallEvent e) {
        wallState = !wallState;
        movementHandler.move(lastScannedEvent, wallState, wallState);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        radarHandler.trackRadar(lastScannedEvent);
        movementHandler.evasiveManeuver(lastScannedEvent);
    }

    public void onWin(WinEvent event) {
        while (true) {
            setTurnRight(Double.POSITIVE_INFINITY);
            setBodyColor(Color.getHSBColor((float) Math.random(), 1, 1));
            execute();
        }
    }
}
