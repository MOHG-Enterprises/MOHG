package MOHG;

import robocode.*;
import robocode.util.Utils;

public class MOHG extends AdvancedRobot {
    private static final double velocidadeBala = 20; // Velocidade dos tiros do MOHG, ajustada para balanceamento
    private boolean emMovimento = false; // Variável para controlar o estado de movimento do robô
    
    public void run() {

        // galera, tudo que voces atualizarem do código, botem comentários para entender oq cada código faz, blz?

        // gira o radar infinitamente ate encontrar um inimigo
        while (true) {
            if (!emMovimento) {
                movimentoAleatorio(); // Executa movimento aleatório se não estiver em movimento
            }
            turnRadarRight(Double.POSITIVE_INFINITY); // Gira o radar continuamente para a direita
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // tracking do radar achado na internet
        double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

        // aim do mohg
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());
        setTurnGunRightRadians(gunTurn);

        // força do tiro
        double danoMohg = Math.min(400 / e.getDistance(), 3); // dano que o mohg dá preservando pela distancia.
        setFire(danoMohg);
        // Executa movimento aleatório após cada tiro
        if (!emMovimento) {
            movimentoAleatorio();
        }

    }

        public void onHitByBullet(HitByBulletEvent e) {
        // quando atingido por um robo, move para trás para quebrar a "leitura" dele.
        back(50); // 50px para trás.
    }

}