package elevador.io.github;

import java.util.List;
import java.util.Random;

public class GeradorPassageiros extends Thread {
    private Predio predio;
    private List<Passageiro> listaParaRenderizar;
    private Random random = new Random();
    private int quantidadeAndares;
    private boolean ativo = true;
    private MainGame mainGame;

    public GeradorPassageiros(Predio predio, List<Passageiro> listaParaRenderizar, int andares, MainGame mainGame) {
        this.predio = predio;
        this.listaParaRenderizar = listaParaRenderizar;
        this.quantidadeAndares = andares;
        this.mainGame = mainGame;
    }

    @Override
    public void run() {
        while (ativo) {
            try {
                int tempoEspera = 1000 + random.nextInt(3000);
                Thread.sleep(tempoEspera);

                int origem = random.nextInt(quantidadeAndares);
                int destino = random.nextInt(quantidadeAndares);

                while (destino == origem) {
                    destino = random.nextInt(quantidadeAndares);
                }

                Passageiro p = new Passageiro(origem, destino, predio);
                
                try {
                    mainGame.mutexLista.acquire();
                    listaParaRenderizar.add(p);
                } catch(Exception e){} finally {
                    mainGame.mutexLista.release();
                }

                p.start();
                System.out.println("Gerador: Novo passageiro criado " + origem + " -> " + destino);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void parar() {
        this.ativo = false;
    }
}