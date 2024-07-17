package MOHG;

import robocode.*;
import robocode.util.Utils;

public class MOHG extends AdvancedRobot {
    private static final double velocidadeBala = 20; // Velocidade dos tiros do MOHG, ajustada para balanceamento
    private boolean emMovimento = false; // Variável para controlar o estado de movimento do robô
    
    public void run() {

        // galera, tudo que voces atualizarem do código, botem comentários para entender oq cada código faz, blz?
        
        setAdjustRadarForGunTurn(true); // radar independente da arma
        while (true) {
            turnRadarRight(Double.POSITIVE_INFINITY); // gira o radar pra direita infinito
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // tracking do radar
        double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

        // força do tiro
        double danoMohg = Math.min(400 / e.getDistance(), 3); // dano que o mohg dá preservando pela distancia.

        // aim do mohg
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());

        if (e.getVelocity() != 0) {
            // Calcular a posição futura do inimigo
            double enemyHeading = e.getHeadingRadians(); // pega a direção do inimigo
            double enemyVelocity = e.getVelocity(); // pega a velocidade do inimigo
            double enemyX = getX() + Math.sin(absoluteBearing) * e.getDistance(); // pega o X do inimigo com base no calculo do target (calculo internet)
            double enemyY = getY() + Math.cos(absoluteBearing) * e.getDistance(); // pega o Y do inimigo com base no calculo do target (calculo internet)

            double predictX = enemyX + Math.sin(enemyHeading) * enemyVelocity * (e.getDistance() / velocidadeBala); // faz o calculo para descobrir qual o X futuro (calculo internet)
            double predictY = enemyY + Math.cos(enemyHeading) * enemyVelocity * (e.getDistance() / velocidadeBala); // fazocalculoparadescobrirqualoYfuturo(calculointernet)

            double angleToFuture = Utils.normalRelativeAngle(Math.atan2(predictX - getX(), predictY - getY()));
            gunTurn = Utils.normalRelativeAngle(angleToFuture - getGunHeadingRadians()); // ajusta a posição da arma
                                                                                         // para o movimento do inimigo
                                                                                         // (calculo internet)
        }

        setTurnGunRightRadians(gunTurn); // se nao for diferente de 0, apenas muda a arma pra está direção e fixa
        setFire(danoMohg); // atira preservando energia com base na distância, como visto antes.

        if (e.getDistance() < 250) { // um tipo de manobra em que eu pensei para em vez de seguir o inimigo, desviar
            // quando ele estiver em 250px de distância
            setTurnRightRadians(Utils.normalRelativeAngle(absoluteBearing + Math.PI / 2 - getHeadingRadians()));
            setAhead(100); // move para longe do inimigo
        } else {
            // Verificar se está próximo da parede
            checkNearWall();

            // Movimenta-se em ziguezague para desviar de balas
            double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - getHeadingRadians());
            setTurnRightRadians(angleToEnemy);

            // Evitar bater nas paredes
            if (nearWall) {
                setTurnRight(90); // Gira 90 graus para a direita
                setAhead(100); // Move-se 100 pixels para frente
            } else {
                // Alternar a direção do movimento para criar o padrão de ziguezague
                if (movingRight) {
                    setTurnRight(30); // Gira 30 graus para a direita
                } else {
                    setTurnLeft(30); // Gira 30 graus para a esquerda
                }
                setAhead(100 * moveDirection); // Move-se 100 pixels para frente ou para trás

                // Alternar a direção do ziguezague
                movingRight = !movingRight;
            }
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // quando atingido por um robo, move para trás para quebrar a "leitura" dele.
        moveDirection = -moveDirection; // Inverter a direção do movimento
        setAhead(50 * moveDirection); // Move-se 50 pixels na nova direção
    }

    // Verifica se está próximo da parede
    private void checkNearWall() {
        double margin = 50; // Margem para evitar as paredes
        double battlefieldWidth = getBattleFieldWidth();
        double battlefieldHeight = getBattleFieldHeight();

        if (getX() <= margin || getX() >= battlefieldWidth - margin || getY() <= margin
                || getY() >= battlefieldHeight - margin) {
            nearWall = true;
        } else {
            nearWall = false;
        }
    }
}