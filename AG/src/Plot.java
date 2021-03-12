
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JFrame;

public class Plot extends JFrame {

    public Plot() {
        setSize(300, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //setVisible(true);
        setResizable(false);

    }

    public void paint(Graphics g) {
        String s = "Ola";
        char c[] = {'M', 'u', 'n', 'd', 'o', '!'};
        byte b[] = {'M', 'u', 'n', 'd', 'o', '!'};

        g.setFont(new Font("Serif", Font.BOLD, 16));
        g.drawString(s,
                50, 30); // x = 50, y = 30
        g.drawChars(c,
                0, 5, 70, 50); // 5 elementos da lista, a partir do primeiro
        g.drawBytes(b,
                5, 1, 90, 80); // 1 elemento da lista, a partir do sexto
        /*
        double[][] funcao = new double[601][601];
        float[][] plano = new float[601][601];
        g.drawRect(50, 50, 601, 601);
        funcao = Ag.matrizF6(601, 5);

        for (int i = 0; i < plano.length; i++) {
            for (int j = 0; j < plano[0].length; j++) {
                plano[i][j] = (float) (0.6666667 - (funcao[i][j] * 0.6666667));
                //System.out.print(plano[i][j]+" ");
            }
            //System.out.println("");
        }

        for (int i = 0; i < plano.length; i++) {
            for (int j = 0; j < plano[0].length; j++) {
                g.setColor(Color.getHSBColor(plano[i][j], 1.0f, 1.0f));
                g.drawLine(50 + i, 50 + j, 50 + i, 50 + j);
            }
        }*/
    }

    public static void main(String[] args) {
        Plot p = new Plot();
        p.setVisible(true);
    }

}
