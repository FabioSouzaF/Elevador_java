package elevador.io.github;
import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;

public class Predio {
    public Semaphore mutex = new Semaphore(1); 
    public Semaphore esperaChamada = new Semaphore(0);
    public Semaphore[] esperaNoAndar; 
    public Semaphore passageiroEntrouOuSaiu = new Semaphore(0);
    public Elevador elevadorRef; 

    public Queue<Integer> chamadas = new LinkedList<>();
    
    public final int CAPACIDADE_MAXIMA = 3; 
    public final int ALTURA_ANDAR = 180;    
    
    public final int X_ELEVADOR = 5;
    public final int X_PLATAFORMA = 300; 
    public final int ESPACO_ENTRE_PESSOAS = 40; 

    private int[] pessoasNoAndar; 
    
    
    private int[] pessoasParaSair;

    public Predio(int andares) {
        esperaNoAndar = new Semaphore[andares];
        pessoasNoAndar = new int[andares]; 
        pessoasParaSair = new int[andares]; 
        
        for(int i=0; i<andares; i++) {
            esperaNoAndar[i] = new Semaphore(0);
        }
    }
    
    public void registrarDestinoPassageiro(int andarDestino) {
        try {
            mutex.acquire();
            pessoasParaSair[andarDestino]++;
            mutex.release();
        } catch (Exception e) {}
    }

    public int qtsQueremSairNesteAndar(int andar) {
        int qtd = 0;
        try {
            mutex.acquire();
            qtd = pessoasParaSair[andar];
            mutex.release();
        } catch (Exception e) {}
        return qtd;
    }
    
    public void confirmarSaida(int andar) {
        try {
            mutex.acquire();
            if (pessoasParaSair[andar] > 0) pessoasParaSair[andar]--;
            mutex.release();
        } catch (Exception e) {}
    }
    
    public void adicionarChamada(int andar) {
        try {
            mutex.acquire();
            
            
            if (!chamadas.contains(andar)) {
                chamadas.add(andar);
            }
            mutex.release();
            esperaChamada.release(); 
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public Integer obterProximaChamada() {
        Integer andar = null;
        try {
            mutex.acquire();
            if (!chamadas.isEmpty()) {
                andar = chamadas.poll(); 
            }
            mutex.release();
        } catch (InterruptedException e) { e.printStackTrace(); }
        return andar;
    }
    
    public int entrarNaFilaVisual(int andar) {
        int posicao = 0;
        try {
            mutex.acquire();
            posicao = pessoasNoAndar[andar];
            pessoasNoAndar[andar]++;
            mutex.release();
        } catch (Exception e) {}
        return posicao;
    }

    public void sairDaFilaVisual(int andar) {
        try {
            mutex.acquire();
            if(pessoasNoAndar[andar] > 0) pessoasNoAndar[andar]--;
            mutex.release();
        } catch (Exception e) {}
    }
}