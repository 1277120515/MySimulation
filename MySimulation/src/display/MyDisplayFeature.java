/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package display;

import gov.nasa.worldwindx.applications.worldwindow.core.Constants;
import gov.nasa.worldwindx.applications.worldwindow.core.Controller;
import gov.nasa.worldwindx.applications.worldwindow.core.Registry;
import gov.nasa.worldwindx.applications.worldwindow.features.AbstractFeature;
import gov.nasa.worldwindx.applications.worldwindow.util.Util;
import javax.swing.JDialog;
import javax.swing.SwingConstants;

/**
 *
 * @author ZZL
 */
public class MyDisplayFeature  extends AbstractFeature  {
    private static final String ICON_PATH = "gov/nasa/worldwindx/applications/worldwindow/images/timg.jpg";
    private JDialog dialog = null;

    public MyDisplayFeature(Registry registry) {
        super("·Â Õæ", Constants.FEATURE_GRATICULE, ICON_PATH, registry);

    }

    @Override
    public void initialize(Controller controller) {

        super.initialize(controller);
        if (dialog == null) {
            dialog = new MyDisplayDialog(this.controller ,this.controller.getFrame());
        }
        this.dialog.setResizable(false);
        this.addToToolBar();
    }

    @Override
    public boolean isOn() {
        return dialog.isVisible();
    }

    @Override
    public boolean isTwoState() {
        return true;
    }

    @Override
    public void turnOn(boolean tf) {
        if (tf) {
            Util.positionDialogInContainer(this.dialog, this.controller.getAppPanel().getJPanel(), SwingConstants.EAST, SwingConstants.NORTH);
//            this.controller.getLayerManager().scrollToLayer(null);
        }
        dialog.setVisible(tf);
    }

    @Override
    public String getName() {
        return null;
    }

}
