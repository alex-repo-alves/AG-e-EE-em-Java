
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JFrame;
import static javax.swing.WindowConstants.*;

public class EEReal extends JFrame {

    public static IndividuoReal[] pop;
    public static IndividuoReal melhor;
    public static int pior;
    public static double[] fitness;
    public static double[] media;
    public static double[] melhores;
    public static double[] desvioPadrao;
    public static double tc; //taxa de cruzamento
    public static double tm; // taxa de mutação
    public static int geracoes;
    public static int tamInd;
    public static int tamPop;
    public static boolean elitismo;
    public static boolean estEstacionario;
    public static double gap;
    public static int lambda;
    public static double[] sigma2;
    public static int multiplicador;
    public static int ro;

    public static IndividuoReal[] currentPop;

    public EEReal() {
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }

    public static void main(String[] args) {
        geracoes = 100;
        multiplicador = 10;
        tamInd = 5;
        tamPop = 100;
        lambda = 300;
        ro = 2;
        media = new double[geracoes];
        melhores = new double[geracoes];
        desvioPadrao = new double[geracoes];
        sigma2 = new double[tamInd];

        iniciaPop(tamPop, tamInd);

        calculaAptidaoPop(pop, tamInd);

        media[0] = getMediaAritmetica();
        melhores[0] = melhor.fitness;
        desvioPadrao[0] = getDesvioPadrao();

        for (int i = 2; i <= geracoes; i++) {
            mutacao(cruzamento());
            ordenaDecPop();
            selecao();
            calculaAptidaoPop(pop, tamInd);
            media[i - 1] = getMediaAritmetica();
            melhores[i - 1] = melhor.fitness;
            desvioPadrao[i - 1] = getDesvioPadrao();

            /*
            cruzamento(elitismo, 3); //com mutação
            
             */
        }

        imprimeDados();
        System.out.println(melhor.fitness);

        //new EEReal();
    }

    public static void imprimeDados() {
        //System.out.println("Fitness médio");
        for (int i = 0; i < geracoes; i++) {
            System.out.print((media[i] + " ").replaceAll("\\.", ","));
        }
        System.out.println("");
        for (int i = 0; i < geracoes; i++) {
            System.out.print((melhores[i] + " ").replaceAll("\\.", ","));
        }
        System.out.println("");
        for (int i = 0; i < geracoes; i++) {
            System.out.print((desvioPadrao[i] + " ").replaceAll("\\.", ","));
        }
        System.out.println("\n");
    }

    public static void selecao() {
        IndividuoReal[] novaPop = new IndividuoReal[tamPop];
        
        for (int i = 0; i < tamPop; i++) {
            novaPop[i] = pop[i];
        }
        pop = novaPop;
    }

    public static void mutacao() {
        IndividuoReal[] novaPop = new IndividuoReal[lambda];
        int a = 0;
        int x = 1;
        for (int i = 0; i < lambda; i++) {
            novaPop[i] = new IndividuoReal(tamInd);
            for (int j = 0; j < tamInd; j++) {

                novaPop[i].cromossomo[j] = (new Random().nextGaussian() * sigma2[j]) + pop[a].cromossomo[j];
            }

            a++;

            if (i + 1 == tamPop * x) {
                a = 0;
                x++;
            }

        }
        pop = novaPop;
        //System.out.println(pop.length);
        calculaAptidaoPop(pop, tamInd);

    }

    public static void mutacao(IndividuoReal[] filhos) {
        IndividuoReal[] novaPop = new IndividuoReal[lambda];
        for (int i = 0; i < lambda; i++) {
            novaPop[i] = filhos[i];
            for (int j = 0; j < tamInd; j++) {

                novaPop[i].cromossomo[j] = (new Random().nextGaussian() * sigma2[j]) + filhos[i].cromossomo[j];
            }

        }
        pop = novaPop;
        calculaAptidaoPop(pop, tamInd);

    }

    public static IndividuoReal[] cruzamento() {
        IndividuoReal[] pais = new IndividuoReal[ro];
        IndividuoReal[] filhos = new IndividuoReal[lambda];

        for (int i = 0; i < lambda; i++) {
            double mediaPais[] = new double[tamInd];
            for (int j = 0; j < tamInd; j++) {
                mediaPais[j] = 0;
            }
            for (int j = 0; j < ro; j++) {
                pais[j] = pop[(int) Math.round(Math.random())];
                for (int k = 0; k < tamInd; k++) {
                    mediaPais[k] = mediaPais[k] + pais[j].cromossomo[k];
                }
            }
            filhos[i] = new IndividuoReal(tamInd);
            for (int k = 0; k < tamInd; k++) {
                
                filhos[i].cromossomo[k] = mediaPais[k] / ro;
            }

        }
        return filhos;
    }

    public static double getMediaAritmetica() {
        double total = 0;
        for (int counter = 0; counter < fitness.length; counter++) {
            total = total + fitness[counter];
        }
        return total / fitness.length;
    }

    public static void ordenaDecPop() {
        IndividuoReal aux;

        for (int i = 0; i < pop.length; i++) {
            for (int j = 0; j < pop.length; j++) {
                if (pop[i].fitness > pop[j].fitness) {
                    aux = pop[i];
                    pop[i] = pop[j];
                    pop[j] = aux;
                }
            }
        }
    }

    public static double[] calculaAptidaoPop(IndividuoReal[] pop, int tamId) {
        melhor = new IndividuoReal(tamId);
        fitness = new double[pop.length];
        double var[];

        for (int i = 0; i < pop.length; i++) {
            var = pop[i].cromossomo.clone();
            fitness[i] = calculaFitness(var[0], var[1], var[2], var[3], var[4]);
            pop[i].fitness = fitness[i];
            if (i > 0) {
                if (fitness[i] > calculaFitness(melhor.cromossomo[0], melhor.cromossomo[1], melhor.cromossomo[2], melhor.cromossomo[3], melhor.cromossomo[4])) {
                    melhor = pop[i];
                }
                if (fitness[i] < calculaFitness(pop[pior].cromossomo[0], pop[pior].cromossomo[1], pop[pior].cromossomo[2], pop[pior].cromossomo[3], pop[pior].cromossomo[4])) {
                    pior = i;
                }
            } else {
                melhor = pop[i];
                pior = i;
            }
        }

        return fitness;
    }

    public static void iniciaPop(int quantInd, int tamInd) {
        pop = new IndividuoReal[quantInd];
        for (int i = 0; i < quantInd; i++) {
            pop[i] = new IndividuoReal(tamInd);
            //pop[i].ind = "";

            for (int j = 0; j < tamInd; j++) {
                if (i == 0) {
                    sigma2[j] = Math.random() * multiplicador;
                }
                if (Math.random() <= 0.5) {
                    pop[i].cromossomo[j] = Math.random() * -100;
                } else {
                    pop[i].cromossomo[j] = Math.random() * 100;
                }
            }
        }
    }

    public static double calculaFitness(double x1, double x2, double x3, double x4, double x5) {
        double f6 = calculaF6(x1, x2) + calculaF6(x2, x3) + calculaF6(x3, x4) + calculaF6(x4, x5) + calculaF6(x5, x1);
        return f6;
    }

    public static double calculaF6(double x, double y) {
        double f6 = 0.5 - (Math.pow((Math.sin(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)))), 2) - 0.5) / Math.pow((1 + 0.001 * (Math.pow(x, 2) + Math.pow(y, 2))), 2);
        return f6;
    }

    public static double getDesvioPadrao() {
        return Math.sqrt(getVariancia());
    }

    public static double getVariancia() {
        double p1 = 1 / Double.valueOf(fitness.length - 1);
        double p2 = getSomaDosElementosAoQuadrado()
                - (Math.pow(getSomaDosElementos(), 2) / Double
                .valueOf(fitness.length));
        return p1 * p2;
    }

    private static double getSomaDosElementosAoQuadrado() {
        double total = 0;
        for (int counter = 0; counter < fitness.length; counter++) {
            total += Math.pow(fitness[counter], 2);
        }
        return total;
    }

    private static double getSomaDosElementos() {
        double total = 0;
        for (int counter = 0; counter < fitness.length; counter++) {
            total += fitness[counter];
        }
        return total;
    }

    // @Override
    /* public void paint(Graphics g) {
        int tamMat = 601;
        double maxXY = 50;
        double[][] funcao = new double[tamMat][tamMat];
        float[][] plano = new float[tamMat][tamMat];
        g.drawRect(49, 49, tamMat + 1, tamMat + 1);
        funcao = EEReal.matrizF6(tamMat, maxXY);

        for (int i = 0; i < tamMat; i++) {
            for (int j = 0; j < 50; j++) {
                g.setColor(Color.getHSBColor((float) ((0.6666667 / tamMat) * i), 1.0f, 1.0f));
                g.drawLine(tamMat + 100 + j, 50 + i, tamMat + 100 + j, 50 + i);
            }
        }

        for (int i = 0; i < plano.length; i++) {
            for (int j = 0; j < plano[0].length; j++) {
                plano[i][j] = (float) (0.6666667 - (funcao[i][j] * 0.6666667));
            }
        }

        for (int i = 0; i < plano.length; i++) {
            for (int j = 0; j < plano[0].length; j++) {
                g.setColor(Color.getHSBColor(plano[i][j], 1.0f, 1.0f));
                g.drawLine(50 + i, 50 + j, 50 + i, 50 + j);
            }
        }

        for (int a = 0; a < pop.length; a++) {
            if (retornaXY(pop[a].cromossomo)[0] > -maxXY && retornaXY(pop[a].cromossomo)[0] < maxXY
                    && retornaXY(pop[a].cromossomo)[1] > -maxXY && retornaXY(pop[a].cromossomo)[1] < maxXY) {
                g.setColor(Color.BLACK);
                g.fillOval(50 + (int) Math.round(((tamMat / (maxXY * 2)) * retornaXY(pop[a].cromossomo)[0]) + 300.5) - 5, 50 + (int) Math.round(((tamMat / (maxXY * 2)) * -retornaXY(pop[a].cromossomo)[1]) + 300.5) - 5, 10, 10);
            }

        }
        if (retornaXY(melhor.cromossomo)[0] > -maxXY && retornaXY(melhor.cromossomo)[0] < maxXY
                && retornaXY(melhor.cromossomo)[1] > -maxXY && retornaXY(melhor.cromossomo)[1] < maxXY) {
            g.setColor(Color.WHITE);
            g.fillOval(50 + (int) Math.round(((tamMat / (maxXY * 2)) * retornaXY(melhor.cromossomo)[0]) + 300.5) - 5, 50 + (int) Math.round(((tamMat / (maxXY * 2)) * -retornaXY(melhor.cromossomo)[1]) + 300.5) - 5, 10, 10);
        }

    }
     */
}
