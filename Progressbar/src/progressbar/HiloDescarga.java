package progressbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class HiloDescarga implements Runnable {

    private final FrmPrincipal frmPrincipal;
    private final String uri;
    private final String target;

    public HiloDescarga(FrmPrincipal frmPrincipal, String uri, String target) {
        this.frmPrincipal = frmPrincipal;
        this.uri = uri;
        this.target = target;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(uri);
            URLConnection urlCon = url.openConnection();

            File file2 = new File(url.toString());
            File file = new File((target != null) ? target + "/" + file2.getName() : file2.getName());

            try (FileOutputStream fos = new FileOutputStream(file)) {
                InputStream is = urlCon.getInputStream();
                byte[] array = new byte[1000];
                int leido = is.read(array);

                double maximo = urlCon.getContentLength();
                frmPrincipal.getBarra().setMinimum(0);
                frmPrincipal.getBarra().setMaximum((int)Math.round(maximo));
                frmPrincipal.getBarra().setValue(0);

                double actual = 0;

                while (leido > 0) {
                    fos.write(array, 0, leido);
                    leido = is.read(array);
                    frmPrincipal.getBarra().setValue((int)Math.round(actual));
                    actual += leido;
                    frmPrincipal.getBarra().setString("Descargado " + (int)Math.round(actual/maximo * 100) + "%");
                }

                frmPrincipal.getBarra().setValue((int)Math.round(maximo));
                frmPrincipal.getBarra().setString("Descargado 100%");
                fos.close();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(HiloDescarga.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(frmPrincipal, "Ingrese una URL válida", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            Logger.getLogger(HiloDescarga.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(frmPrincipal, "No se pudo descargar el archivo: "  + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
