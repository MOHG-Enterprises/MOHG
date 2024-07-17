package MOHG;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

public class MOHG extends AdvancedRobot {
    private static final double velocidadeBala = 20; // velocidade dos tiros do mohg, botei 20 para ficar balanceado
	private ScannedRobotEvent lastScannedEvent; 
    
    public void run() {
        setAdjustRadarForGunTurn(true); // radar independente da arma
        while (true) {
            turnRadarRight(Double.POSITIVE_INFINITY); // gira o radar pra direita infinito
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
			lastScannedEvent = e;
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
				// talvez tenha ficado um pouco repetitivo esse código porém foi o unico jeito que consegui, criei a mesmas variáveis 3 vezes cada uma.
				double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
				double evasiveAngle = Math.toRadians(10) * Math.sin(getTime() / 10.0); // (calculo internet)
				double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - getHeadingRadians());
		
				setTurnRightRadians(angleToEnemy + evasiveAngle);
			} else {
				if (e.getDistance() < 250) { // um tipo de manobra em que eu pensei para em vez de seguir o inimigo, desviar quando ele estiver em 250px de distância
					Movement(e, true);
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
	
	public void Movement(ScannedRobotEvent e, boolean goRight) {
	    double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
	    double evasiveAngle = Math.toRadians(10) * Math.sin(getTime() / 10.0); // ajuste o ângulo evasivo conforme necessário
	    double angleToEnemy = Utils.normalRelativeAngle(absoluteBearing - getHeadingRadians());
	    
	    if (goRight) {
	        setTurnRightRadians(Utils.normalRelativeAngle(absoluteBearing + Math.PI / 2 - getHeadingRadians()));
	    } else {
			setBack(Double.POSITIVE_INFINITY);
	        setTurnLeftRadians(Utils.normalRelativeAngle(absoluteBearing + Math.PI / 2 - getHeadingRadians()));
	    }
	}
	
	public void onHitWall(HitWallEvent e) {

		setBodyColor(Color.PINK);		
		Movement(lastScannedEvent, false);
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
        setBack(20); // se move pra trás apenas para que seja lido e logo em cima para frente novamente
    	setAhead(100); // "dodge" do mohg
	    setBodyColor(Color.RED);
    }
}
