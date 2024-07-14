package MOHG;

import robocode.*;
import robocode.util.Utils;

public class MOHG extends AdvancedRobot {
    
    public void run() {

        // galera, tudo que voces atualizarem do código, botem comentários para entender oq cada código faz, blz?

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
        
        // atira tentando nao gastar toda energia na distancia
        double firePower = Math.min(400 / e.getDistance(), 3); // ajusta o fogo pela distancia ate o inimigo.
        setFire(firePower);

    }
}