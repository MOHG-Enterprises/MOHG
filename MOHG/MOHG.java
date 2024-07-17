package MOHG;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

public class MOHG extends AdvancedRobot {
    private static final double velocidadeBala = 20; // Velocidade dos tiros do MOHG, ajustada para balanceamento
    int distancia = 300;
    
    public void run() {

        // galera, tudo que voces atualizarem do código, botem comentários para entender oq cada código faz, blz?

        setAdjustRadarForGunTurn(true); // radar independente da arma
        setBodyColor(Color.BLACK);
        setGunColor(Color.BLACK)
        setRadarColor(Color.BLACK);
        setBulletColor(Color.BLACK);
        setScanColor(Color.BLACK);
        while (true) {
            turnRadarRight(Double.POSITIVE_INFINITY); // gira o radar pra direita infinito
            execute(); // executa todos os comandos pendentes
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
                double enemyX = getX() + Math.sin(absoluteBearing) * e.getDistance(); // pega o X do inimigo com base no calculo do target (calculo internet)
                double enemyY = getY() + Math.cos(absoluteBearing) * e.getDistance(); // pega o Y do inimigo com base no calculo do target (calculo internet)

                double predictX = enemyX + Math.sin(enemyHeading) * enemyVelocity * (e.getDistance() / velocidadeBala); // faz o calculo para descobrir qual o X futuro (calculo internet)
                double predictY = enemyY + Math.cos(enemyHeading) * enemyVelocity * (e.getDistance() / velocidadeBala); // faz o calculo para descobrir qual o Y futuro (calculo internet)

                double angleToFuture = Utils.normalRelativeAngle(Math.atan2(predictX - getX(), predictY - getY())); // ângulo da posição futura (calculo internet)
                gunTurn = Utils.normalRelativeAngle(angleToFuture - getGunHeadingRadians()); // ajusta a posição da arma para o movimento do inimigo (calculo internet)
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
            if (e.getDistance() < distancia) { // um tipo de manobra em que eu pensei para em vez de seguir o inimigo, desviar quando ele estiver em 300px de distância, vai abaixando este valor conforme bate na parede.
                double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
                double evasiveAngle = Math.toRadians(10) * Math.sin(getTime() / 10.0); // (calculo internet)
                double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - getHeadingRadians());
                setTurnRightRadians(Utils.normalRelativeAngle(absoluteBearing + Math.PI / 2 - getHeadingRadians())); // partes de um monte de calculo que eu vi
                setAhead(100);
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

        execute(); // executa todos os comandos pendentes
    }

    public void onWin(WinEvent event) {
        while (true) {
            setTurnRight(Double.POSITIVE_INFINITY); // robo gira ate o fim do round
            setBodyColor(Color.getHSBColor((float) Math.random(), 1, 1)); // Muda a cor do corpo do tanque
            execute(); // Executa os comandos pendentes
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // caso seja atingido por uma bala, acontecerá isso:
        setBack(100); // "dodge" do mohg
        execute();
    }

    public void onHitWall(HitWallEvent e) {
        setTurnRight(Utils.normalRelativeAngle(getHeadingRadians() - e.getBearingRadians() + Math.PI / 2));
        setBack(100); // se move pra trás apenas para que seja lido e logo em cima para frente novamente
        distancia = distancia - 10; // para desbugar da parede e conseguir chegar mais perto do inimigo (não funciona, a distancia realmente é reduzida mas ele da 30 vezes na parede antes de chegar mais perto, ou seja, a distancia vai pra 100 e ainda não da pra chegar mais perto.)
        execute(); // executa todos os comandos pendentes
    }
}