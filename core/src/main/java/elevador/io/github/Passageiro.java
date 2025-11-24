package elevador.io.github;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import java.util.Random;

public class Passageiro extends Thread {
    private int andarOrigem;
    private int andarDestino;
    private Predio predio;
    
    public Vector2 position;
    public Color cor; 
    
    public boolean dentroDoElevador = false;
    public boolean chegouAoDestino = false;
    
    
    private final float X_RUA = 1300; 

    public Passageiro(int origem, int destino, Predio predio) {
        this.andarOrigem = origem;
        this.andarDestino = destino;
        this.predio = predio;
        
        
        this.position = new Vector2(X_RUA, origem * predio.ALTURA_ANDAR);
        
        
        Random r = new Random();
        this.cor = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1);
    }

    public void run() {
        try {
            int lugarNaFila = predio.entrarNaFilaVisual(andarOrigem);
            float xFila = predio.X_PLATAFORMA + (lugarNaFila * predio.ESPACO_ENTRE_PESSOAS);

            caminharAte(xFila);
            
            predio.adicionarChamada(andarOrigem);
            predio.esperaNoAndar[andarOrigem].acquire();
            
            predio.sairDaFilaVisual(andarOrigem); 
            caminharAte(predio.X_ELEVADOR);
            dentroDoElevador = true;

            predio.registrarDestinoPassageiro(andarDestino);

            predio.adicionarChamada(andarDestino);

            predio.passageiroEntrouOuSaiu.release();

            predio.esperaNoAndar[andarDestino].acquire();

            dentroDoElevador = false;

            this.position.y = andarDestino * predio.ALTURA_ANDAR; 

            caminharAte(predio.X_PLATAFORMA);
            predio.passageiroEntrouOuSaiu.release();

            caminharAte(900 + (andarDestino * 50)); 
            chegouAoDestino = true;

        } catch (InterruptedException e) { e.printStackTrace(); }
    }
    
    private void caminharAte(float xDestino) throws InterruptedException {
        float velocidade = 3.0f; 
        
        
        while (Math.abs(position.x - xDestino) > 2) {
            if (position.x < xDestino) {
                position.x += velocidade;
            } else {
                position.x -= velocidade;
            }
            Thread.sleep(16); 
        }
        position.x = xDestino; 
    }
}