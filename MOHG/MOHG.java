package MOHG;

import robocode.*;
import robocode.util.Utils;

public class MOHG extends AdvancedRobot {
    
    public void run() {
        // gira o radar infinitamente ate encontrar um inimigo
        setAdjustRadarForGunTurn(true); // *IMPORTANTE!!! deixa o radar independente da arma
        while (true) {
            turnRadarRight(Double.POSITIVE_INFINITY);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // crava a mira no inimigo
        double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
        
        // Calcula a virada da arma pra targetar o inimigo
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());
        setTurnGunRightRadians(gunTurn);
        
    }
}