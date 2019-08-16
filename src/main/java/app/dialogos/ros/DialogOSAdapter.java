package app.dialogos.ros;

import com.clt.dialog.client.StdIOConnectionChooser;
import com.clt.diamant.*;
import com.clt.diamant.gui.DocumentWindow;
import com.clt.diamant.gui.SingleDocumentWindow;
import com.clt.util.Misc;

import javax.swing.*;
import java.io.File;

public class DialogOSAdapter {
    private boolean headless = !System.getProperty("dialogos.showGUI", "false").equals("true");
    private File appDir = Misc.getApplicationDirectory();
    private Executer executor;
    private DocumentWindow<?> window;
    private SingleDocument sd;

    void loadDialog(String filename) {
        final File initialModel = new File(appDir, filename);
        if (!initialModel.isFile()) {
            throw new IllegalArgumentException("The specified dialog model does not exist.");
        }
        if (headless) {
            try {
                Document doc = new DocumentLoader(null).load(initialModel, null);
                assert doc instanceof SingleDocument : "File " + initialModel + " is not a dialog model.";
                sd = (SingleDocument) doc;
                this.executor = new Executer(null, false);
            } catch (Exception exn) {
                exn.printStackTrace();
            }
        } else {
            final Main app = new Main(appDir);
            window = app.openDocument(null, initialModel, null);
            assert window instanceof SingleDocumentWindow;
            sd = ((SingleDocumentWindow<?>) window).getDocument();
        }
    }

    void startDialog() {
        if (headless && executor != null &&
                sd.connectDevices(new StdIOConnectionChooser(), Preferences.getPrefs().getConnectionTimeout()))
        {
            new Thread(() -> {
                try {
                    sd.run(null, executor);
                } catch (Exception exn) {
                    exn.printStackTrace();
                }
            }, "DialogOSRos-startDialogThread").start();
        }
        if (!headless && window != null)
            SwingUtilities.invokeLater(() -> window.doCommand(SingleDocumentWindow.cmdRun));
    }

    void abortDialog() {
        if (headless && executor != null)
            executor.abort();
        if (!headless && window != null)
            window.doCommand(SingleDocumentWindow.cmdClose);
    }

}
