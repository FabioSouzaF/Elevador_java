package elevador.io.github;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Gdx.graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

public class MainGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture imgElevadorFechado, imgElevadorAberto, imgPassageiro, imgChao;
    Predio predio;
    Elevador elevador;
    GeradorPassageiros gerador;
    int qtdAndares = 4;
    List<Passageiro> passageiros = new ArrayList<>();
    
    public Semaphore mutexLista = new Semaphore(1);

    @Override
    public void create() {
        batch = new SpriteBatch();
        imgElevadorFechado = new Texture("Elevador1.png"); 
        imgElevadorAberto = new Texture("Elevador3.png");
        imgPassageiro = new Texture("Passageiro.png");
        
        predio = new Predio(qtdAndares);
        elevador = new Elevador(predio);
        predio.elevadorRef = elevador;
        elevador.start(); 

        gerador = new GeradorPassageiros(predio, passageiros, qtdAndares, this);
        gerador.start();
        
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

        pixmap.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        pixmap.fill();
        imgChao = new Texture(pixmap);
        pixmap.dispose();
        
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.5f, 0.8f, 1, 1);
        batch.begin();
        
        for(int i=0; i< qtdAndares; i++) {
            
            float yBase = i * predio.ALTURA_ANDAR;

            batch.setColor(com.badlogic.gdx.graphics.Color.FOREST); 
            batch.draw(imgChao, 0, yBase, graphics.getWidth(), 20);

            batch.setColor(com.badlogic.gdx.graphics.Color.DARK_GRAY);
            batch.draw(imgChao, 0, yBase + predio.ALTURA_ANDAR - 5, Gdx.graphics.getWidth(), 5);
            
        }
        
        batch.setColor(0.2f, 0.2f, 0.2f, 1);
        batch.draw(imgChao, predio.X_ELEVADOR, 0, 140, qtdAndares * predio.ALTURA_ANDAR);
        
        batch.setColor(Color.WHITE); 
        Texture texElev = elevador.isPortaAberta() ? imgElevadorAberto : imgElevadorFechado;
        batch.draw(texElev, elevador.position.x, elevador.position.y);
        
        try {
            mutexLista.acquire();
            Iterator<Passageiro> iter = passageiros.iterator();
            
            while (iter.hasNext()) {
                Passageiro p = iter.next();
                
                if (p.chegouAoDestino && p.position.x >= predio.X_PLATAFORMA) {
                     iter.remove(); 
                     continue;
                }
                
                if (p.dentroDoElevador) {
                    p.position.y = elevador.position.y;

                    batch.setColor(1, 1, 1, 0.5f); 
                } else {
                    batch.setColor(Color.WHITE);
                }
                
                batch.draw(imgPassageiro, p.position.x, p.position.y);
                
                if (p.chegouAoDestino) {
                    iter.remove();
                    continue;
               }
            }
        } catch(Exception e){} finally {
            mutexLista.release();
        }
        batch.setColor(Color.WHITE);
        batch.end();
    }
    
    @Override
    public void dispose() {
        if (gerador != null) gerador.parar();
        batch.dispose();
        imgElevadorAberto.dispose();
        imgElevadorFechado.dispose();
        imgPassageiro.dispose();
    }
}