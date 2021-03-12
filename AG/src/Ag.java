
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.*;

public class Ag extends JFrame {

    public static Individuo[] pop;
    public static Individuo melhor;
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
    
    public static Individuo[] currentPop;


    public Ag() {
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }

    public static void main(String[] args) {
        tc = 0.75;
        tm = 0.01;
        geracoes = 100;
        tamInd = 28;
        tamPop = 100;
        elitismo = true;
        estEstacionario = false;
        gap = 0.40;
        media = new double[geracoes];
        melhores = new double[geracoes];
        desvioPadrao = new double[geracoes];

        iniciaPop(tamPop, tamInd);

        calculaAptidaoPop(pop, tamInd);

        media[0] = getMediaAritmetica();
        melhores[0] = melhor.fitness;
        desvioPadrao[0] = getDesvioPadrao();

        for (int i = 2; i <= geracoes; i++) {
            cruzamento(elitismo,2); //com mutação
            calculaAptidaoPop(pop, tamInd);
            media[i - 1] = getMediaAritmetica();
            melhores[i - 1] = melhor.fitness;
            desvioPadrao[i - 1] = getDesvioPadrao();
        }

        imprimeDados();

        new Ag();

    }

    public static void imprimeDados() {
        //System.out.println("Fitness médio");
        for (int i = 0; i < tamPop; i++) {
            System.out.print(media[i] + " ");
        }
        System.out.println("");
        for (int i = 0; i < tamPop; i++) {
            System.out.print(melhores[i] + " ");
        }
        System.out.println("");
        for (int i = 0; i < tamPop; i++) {
            System.out.print(desvioPadrao[i] + " ");
        }
        System.out.println("\n");
    }

    public static void mutacao() {
        for (int i = 0; i < tamPop; i++) {
            for (int j = 0; j < tamInd; j++) {
                if (Math.random() < tm) {
                    if (pop[i].ind.charAt(j) == '0') {
                        pop[i].ind = pop[i].ind.substring(0, j) + '1' + pop[i].ind.substring(j + 1);
                    } else {
                        pop[i].ind = pop[i].ind.substring(0, j) + '0' + pop[i].ind.substring(j + 1);
                    }
                }
            }
        }
    }

    public static void cruzamento(boolean elistismo, int cruza) {
        Individuo[] filhos = new Individuo[pop.length];
        int pontoCorte = 0;
        int pontoCorte2 = 0;
        Individuo pai1, pai2;
        int stop = pop.length;
        currentPop = pop;

        if (estEstacionario) {
            stop = (int) Math.round(gap * pop.length);
        }

        for (int i = 0; i < pop.length; i = i + 2) {
            filhos[i] = new Individuo(pop[i].tamanho);
            filhos[i + 1] = new Individuo(pop[i + 1].tamanho);
            pai1 = roleta(pop, fitness);
            pai2 = roleta(pop, fitness);
            if (Math.random() < tc) {
                switch (cruza) {
                    case 1:
                        pontoCorte = (int) Math.round(Math.random() * pai1.tamanho);
                        filhos[i].ind = pai1.ind.substring(0, pontoCorte) + pai2.ind.substring(pontoCorte);
                        filhos[i + 1].ind = pai2.ind.substring(0, pontoCorte) + pai1.ind.substring(pontoCorte);
                        break;
                    case 2:
                        while (pontoCorte == pontoCorte2) {
                            pontoCorte = (int) Math.round(Math.random() * pai1.tamanho);
                            pontoCorte2 = (int) Math.round(Math.random() * pai1.tamanho);
                            if (pontoCorte > pontoCorte2) {
                                int aux = pontoCorte;
                                pontoCorte = pontoCorte2;
                                pontoCorte2 = aux;
                            }
                        }
                        filhos[i].ind = pai1.ind.substring(0, pontoCorte) + pai2.ind.substring(pontoCorte, pontoCorte2) + pai1.ind.substring(pontoCorte2);
                        filhos[i + 1].ind = pai2.ind.substring(0, pontoCorte) + pai1.ind.substring(pontoCorte, pontoCorte2) + pai2.ind.substring(pontoCorte2);
                        break;
                    case 3:
                        filhos[i].ind = "";
                        filhos[i + 1].ind = "";
                        for (int j = 0; j < tamInd; j++) {
                            int a = (int) Math.round(Math.random());
                            switch (a) {
                                case 0:
                                    filhos[i].ind = filhos[i].ind + pai1.ind.charAt(j);
                                    filhos[i + 1].ind = filhos[i + 1].ind + pai2.ind.charAt(j);
                                    break;
                                case 1:
                                    filhos[i].ind = filhos[i].ind + pai2.ind.charAt(j);
                                    filhos[i + 1].ind = filhos[i + 1].ind + pai1.ind.charAt(j);
                                    break;
                            }
                        }
                        break;
                }

            } else {
                filhos[i] = pai1;
                filhos[i + 1] = pai2;
            }
        }

        pop = filhos;
        mutacao();
        calculaAptidaoPop(pop, tamInd);
        if (elistismo) {
            pop[pior] = melhor;
        }
        if (estEstacionario) {
            ordenaPop();
            pop = currentPop;
            for (int i = stop; i < pop.length; i++) {
                pop[i] = filhos[i - stop];
            }
        }
    }

    public static double getMediaAritmetica() {
        double total = 0;
        for (int counter = 0; counter < fitness.length; counter++) {
            total += fitness[counter];
        }
        return total / fitness.length;
    }

    public static void ordenaPop() {
        Individuo aux;

        for (int i = 0; i < currentPop.length; i++) {
            for (int j = 0; j < currentPop.length; j++) {
                if (currentPop[i].fitness > currentPop[j].fitness) {
                    aux = currentPop[i];
                    currentPop[i] = currentPop[j];
                    currentPop[j] = aux;
                }
            }
        }
        pop = currentPop;
    }
    public static void ordenaDecPop() {
        Individuo aux;

        for (int i = 0; i < pop.length; i++) {
            for (int j = 0; j < pop.length; j++) {
                if (pop[i].fitness < pop[j].fitness) {
                    aux = pop[i];
                    pop[i] = pop[j];
                    pop[j] = aux;
                }
            }
        }
    }
    
    public static void normaliza(){
        ordenaDecPop();
        
    }

    public static double[] calculaAptidaoPop(Individuo[] pop, int tamId) {
        melhor = new Individuo(tamId);
        fitness = new double[pop.length];
        double var[];

        for (int i = 0; i < pop.length; i++) {
            var = retornaXY(pop[i].ind);
            fitness[i] = calculaFitness(var[0], var[1]);
            pop[i].fitness = fitness[i];
            if (i > 0) {
                if (fitness[i] > calculaFitness(retornaXY(melhor.ind)[0], retornaXY(melhor.ind)[1])) {
                    melhor = pop[i];
                }
                if (fitness[i] < calculaFitness(retornaXY(pop[pior].ind)[0], retornaXY(pop[pior].ind)[1])) {
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
        pop = new Individuo[quantInd];
        for (int i = 0; i < quantInd; i++) {
            pop[i] = new Individuo(tamInd);
            pop[i].ind = "";
            for (int j = 0; j < tamInd; j++) {
                int a = (int) Math.round(Math.random());

                switch (a) {
                    case 0:
                        pop[i].ind = pop[i].ind + 0;
                        break;
                    case 1:
                        pop[i].ind = pop[i].ind + 1;
                        break;
                }
            }
        }
    }

    public static Individuo roleta(Individuo[] pop, double[] fitness) {
        double[] somaAptidao = new double[pop.length];
        double soma = 0;
        for (int i = 0; i < fitness.length; i++) {
            somaAptidao[i] = soma + fitness[i];
            soma = somaAptidao[i];
        }

        double valor = Math.random() * soma;
        Individuo sorteado = null;

        for (int i = 0; i < somaAptidao.length; i++) {
            if (i > 0) {
                if (valor <= somaAptidao[i] && valor > somaAptidao[i - 1]) {
                    sorteado = pop[i];
                    i = somaAptidao.length;
                }
            } else {
                if (valor <= somaAptidao[i]) {
                    sorteado = pop[i];
                    i = somaAptidao.length;
                }
            }
        }
        return sorteado;
    }

    public static double[] retornaXY(String cromossomo) {
        String x = cromossomo.substring(0, cromossomo.length() / 2);
        String y = cromossomo.substring(cromossomo.length() / 2);

        int a = convertBinaryToDecimal(x);
        int b = convertBinaryToDecimal(y);

        double xy[] = new double[2];

        xy[0] = a * (200 / (Math.pow(2, x.length()) - 1)) - 100;

        xy[1] = b * (200 / (Math.pow(2, y.length()) - 1)) - 100;

        return xy;
    }

    public static double calculaFitness(double x, double y) {
        double f6 = 0.5 - (Math.pow((Math.sin(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)))), 2) - 0.5) / Math.pow((1 + 0.001 * (Math.pow(x, 2) + Math.pow(y, 2))), 2);
        return f6;
    }

    public static double[][] matrizF6(int tamMatriz, double maxXY) {// tamMatriz deve ser ímpar por causa do 0
        double unidade = maxXY / ((tamMatriz - 1) / 2);
        int x = 0;
        int y = ((tamMatriz - 1) / 2);
        double pior = 100;

        double[][] matriz = new double[tamMatriz][tamMatriz];
        for (int i = 0; i < tamMatriz; i++) {
            x = -((tamMatriz - 1) / 2);
            for (int j = 0; j < tamMatriz; j++) {
                matriz[i][j] = calculaFitness(x * unidade, y * unidade);
                x++;
                if (matriz[i][j]<pior){
                    pior = matriz[i][j];
                }
            }
            y--;
        }
        System.out.println("Pior: "+pior);
        return matriz;
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

    public static int convertBinaryToDecimal(String binaryNumber) {
        int decimalNumber = 0;
        for (int i = 0; i < binaryNumber.length(); i++) {
            decimalNumber = (int) (decimalNumber + ((Integer.parseInt(binaryNumber.charAt(i) + "")) * Math.pow(2, binaryNumber.length() - (i + 1))));
        }
        return decimalNumber;
    }

    @Override
    public void paint(Graphics g) {
        int tamMat = 601;
        double maxXY = 10;
        double[][] funcao = new double[tamMat][tamMat];
        float[][] plano = new float[tamMat][tamMat];
        g.drawRect(49, 49, tamMat + 1, tamMat + 1);
        funcao = Ag.matrizF6(tamMat, maxXY);

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
            if (retornaXY(pop[a].ind)[0] > -maxXY && retornaXY(pop[a].ind)[0] < maxXY
                    && retornaXY(pop[a].ind)[1] > -maxXY && retornaXY(pop[a].ind)[1] < maxXY) {
                g.setColor(Color.BLACK);
                g.fillOval(50 + (int) Math.round(((tamMat / (maxXY * 2)) * retornaXY(pop[a].ind)[0]) + 300.5) - 5, 50 + (int) Math.round(((tamMat / (maxXY * 2)) * -retornaXY(pop[a].ind)[1]) + 300.5) - 5, 10, 10);
            }

        }
        if (retornaXY(melhor.ind)[0] > -maxXY && retornaXY(melhor.ind)[0] < maxXY
                && retornaXY(melhor.ind)[1] > -maxXY && retornaXY(melhor.ind)[1] < maxXY) {
            g.setColor(Color.WHITE);
            g.fillOval(50 + (int) Math.round(((tamMat / (maxXY * 2)) * retornaXY(melhor.ind)[0]) + 300.5) - 5, 50 + (int) Math.round(((tamMat / (maxXY * 2)) * -retornaXY(melhor.ind)[1]) + 300.5) - 5, 10, 10);
        }

    }

}
