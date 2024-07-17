package MOHG;

import robocode.*;
import robocode.util.Utils;

public class MOHG extends AdvancedRobot {
    private static final double velocidadeBala = 20; // Velocidade dos tiros do MOHG, ajustada para balanceamento
    
    public void run() {

        // galera, tudo que voces atualizarem do código, botem comentários para entender oq cada código faz, blz?

        setAdjustRadarForGunTurn(true); // radar independente da arma
        while (true) {
            turnRadarRight(Double.POSITIVE_INFINITY); // gira o radar pra direita infinito
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if (getEnergy() > 2.0) {
            // tracking do radar
            double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
            setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

            // força do tiro
            double danoMohg = Math.min(400 / e.getDistance(), 3); // dano que o mohg dá preservando a distancia.

            // aim do mohg
            double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
            double gunTurn = Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());

            if (e.getVelocity() != 0) {
                // calcular a posição futura do inimigo
                double enemyHeading = e.getHeadingRadians(); // pega a direção do inimigo
                double enemyVelocity = e.getVelocity(); // pega a velocidade do inimigo
                double enemyX = getX() + Math.sin(absoluteBearing) * e.getDistance();
                double enemyY = getY() + Math.cos(absoluteBearing) * e.getDistance();

                double predictX = enemyX + Math.sin(enemyHeading) * enemyVelocity * (e.getDistance() / velocidadeBala);
                double predictY = enemyY + Math.cos(enemyHeading) * enemyVelocity * (e.getDistance() / velocidadeBala);

                double angleToFuture = Utils.normalRelativeAngle(Math.atan2(predictX - getX(), predictY - getY()));
                gunTurn = Utils.normalRelativeAngle(angleToFuture - getGunHeadingRadians());
            }

            setTurnGunRightRadians(gunTurn); // se nao for diferente de 0, apenas muda a arma pra está direção e fixa
            setFire(danoMohg); // atira preservando energia com base na distância, como visto antes.
        }

        if (getEnergy() <= 2.0) {
            // talvez tenha ficado um pouco repetitivo esse código porém foi o unico jeito
            // que consegui, criei a mesmas variáveis 3 vezes cada uma.
            double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
            double evasiveAngle = Math.toRadians(10) * Math.sin(getTime() / 10.0); // (calculo internet)
            double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - getHeadingRadians());

            setTurnRightRadians(angleToEnemy + evasiveAngle);
            setAhead(Double.POSITIVE_INFINITY);
        } else {
            if (e.getDistance() < 250) { // um tipo de manobra em que eu pensei para em vez de seguir o inimigo, desviar
                                         // quando ele estiver em 250px de distância
                double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
                double evasiveAngle = Math.toRadians(10) * Math.sin(getTime() / 10.0); // (calculo internet)
                double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - getHeadingRadians());
                setTurnRightRadians(Utils.normalRelativeAngle(absoluteBearing + Math.PI / 2 - getHeadingRadians()));
                setAhead(100); // se mexe para longe do inimigo
            } else {
                // para não ir reto até o inimigo e ele correr reto, ir desviando.
                double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
                double evasiveAngle = Math.toRadians(10) * Math.sin(getTime() / 10.0); // (calculo internet)
                double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - getHeadingRadians());

                // muda o angulo "impedindo" ser acertado
                setTurnRightRadians(angleToEnemy + evasiveAngle);
                setAhead(Double.POSITIVE_INFINITY); // move pra frente, porém em "zigzag"
            }
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // caso seja atingido por uma bala, acontecerá isso:
        setAdjustRadarForGunTurn(true); // radar independente da arma
        // tracking do radar
        double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

        // aim do mohg
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());
        setTurnRightRadians(Utils.normalRelativeAngle(getHeadingRadians() - e.getBearingRadians() + Math.PI / 2));
        back(20); // se move pra trás apenas para que seja lido e logo em cima para frente
        setAhead(100); // "dodge" do mohg
    }

    public void onHitWall(HitWallEvent e) {
        setAdjustRadarForGunTurn(true); // radar independente da arma
        // tracking do radar
        double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

        // aim do mohg
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());
        setBack(100); // se move pra trás apenas para que seja lido e logo em cima para frente
    }
}