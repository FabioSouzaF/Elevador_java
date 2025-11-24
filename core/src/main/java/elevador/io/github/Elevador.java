package elevador.io.github;
import com.badlogic.gdx.math.Vector2;

public class Elevador extends Thread {
    private Predio predio;
    private boolean portaAberta = false;
    public int andarAtual = 0;
    public Vector2 position = new Vector2(100, 0); 
    
    
    private int passageirosABordo = 0; 

    public Elevador(Predio predio) {
        this.predio = predio;
        this.position.x = predio.X_ELEVADOR;
    }

    public void run() {
        while(true) {
            try {
                predio.esperaChamada.acquire(); 
                Integer destino = predio.obterProximaChamada();
                
                if (destino != null) {
                    moverParaAndar(destino);
                    abrirPorta(); 

                    int qtsSair = predio.qtsQueremSairNesteAndar(andarAtual);
                    
                    if (qtsSair > 0) {
                        System.out.println("ELEVADOR: " + qtsSair + " passageiros vão descer.");

                        predio.esperaNoAndar[andarAtual].release(qtsSair);
                        
                        
                        for (int i = 0; i < qtsSair; i++) {
                            predio.passageiroEntrouOuSaiu.acquire();
                            registrarSaida(); 
                            predio.confirmarSaida(andarAtual); 
                        }
                    }

                    int vagasLivres = predio.CAPACIDADE_MAXIMA - passageirosABordo;
                    int pessoasEsperando = predio.esperaNoAndar[andarAtual].getQueueLength();

                    int quantosEntram = Math.min(vagasLivres, pessoasEsperando);
                    
                    if (quantosEntram > 0) {
                        System.out.println("ELEVADOR: " + quantosEntram + " passageiros vão entrar.");
                        
                        predio.esperaNoAndar[andarAtual].release(quantosEntram);
                        
                        for (int i = 0; i < quantosEntram; i++) {
                            predio.passageiroEntrouOuSaiu.acquire();
                            passageirosABordo++;
                        }
                    }
                    
                    fecharPorta(); 
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    private void moverParaAndar(int destino) throws InterruptedException {
        float yDestino = destino * predio.ALTURA_ANDAR;
        
        while (Math.abs(position.y - yDestino) > 4) {
            if (position.y < yDestino) position.y += 4; 
            else position.y -= 4;
            Thread.sleep(10);
        }
        position.y = yDestino; 
        this.andarAtual = destino;
    }

    private void abrirPorta() throws InterruptedException {
        Thread.sleep(300); 
        portaAberta = true;
    }

    private void fecharPorta() throws InterruptedException {
        Thread.sleep(300); 
        portaAberta = false;
    }
    
    public synchronized void registrarSaida() {
        passageirosABordo--;
        System.out.println("Passageiro saiu. Lotação atual: " + passageirosABordo);
    }
    
    public boolean isPortaAberta() { return portaAberta; }
}