/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwindx.applications.worldwindow.features.swinglayermanager;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.ScreenRelativeAnnotation;
import gov.nasa.worldwindx.applications.worldwindow.core.*;
import gov.nasa.worldwindx.applications.worldwindow.core.layermanager.ActiveLayersManager;
import gov.nasa.worldwindx.applications.worldwindow.features.AbstractFeaturePanel;
import gov.nasa.worldwindx.applications.worldwindow.util.*;


import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.ArrayList;


/**
 * @author tag
 * @version $Id: ActiveLayersPanel.java 1171 2013-02-11 21:45:02Z dcollins $
 */
@SuppressWarnings("unchecked")
public class ActiveLayersPanel extends AbstractFeaturePanel implements ActiveLayersManager
{
    protected static final String TOOL_TIP = "可见图层";

    protected DefaultListModel model; // the list model
    protected ActiveLayersList jlist; // the associated JList

    private boolean on = false;

    public ActiveLayersPanel(Registry registry)
    {
        super("Layer List", Constants.FEATURE_ACTIVE_LAYERS_PANEL, new ShadedPanel(new BorderLayout()), registry);
    }

    @Override
    public void initialize(final Controller controller)
    {
        super.initialize(controller);

        this.model = new DefaultListModel();
        this.jlist = new ActiveLayersList(this.model);
        this.jlist.setBorder(new EmptyBorder(15, 15, 10, 10));
        this.jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        this.jlist.setToolTipText(TOOL_TIP);
        this.jlist.setAutoscrolls(true);

        JScrollPane scrollPane = new JScrollPane(jlist);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JPanel np = new JPanel(new BorderLayout(5, 5));
        np.setOpaque(false);
        np.add(scrollPane, BorderLayout.CENTER);

        PanelTitle panelTitle = new PanelTitle("活动图层", SwingConstants.CENTER);
        panelTitle.setToolTipText(TOOL_TIP);

        this.panel.add(panelTitle, BorderLayout.NORTH);
        this.panel.add(np, BorderLayout.CENTER);

        LayerList layerList = controller.getWWd().getModel().getLayers();

        RenderableLayer regionRenderableLayer = new RenderableLayer();
//        SurfaceImageLayer regionRenderableLayer = new SurfaceImageLayer();
        regionRenderableLayer.setName("地面任务区域");
        layerList.add(regionRenderableLayer);
        
        SurfaceImageLayer resultSurfaceImageLayer = new SurfaceImageLayer();
        resultSurfaceImageLayer.setName("区域覆盖结果");
        layerList.add(resultSurfaceImageLayer);
       
        RenderableLayer displayLayer = new RenderableLayer();
        displayLayer.setName("三维显示");
        layerList.add(displayLayer);

        layerList.getLayerByName("大气层").setEnabled(false);
        
        AnnotationAttributes attr = new AnnotationAttributes();
        ScreenRelativeAnnotation annotation = new ScreenRelativeAnnotation("", 0.01, 0.6);
        attr.setBackgroundColor(new Color(0.0f, 0, 0, 0.0f));
        attr.setBorderColor(new Color(0, 1.0f, 0, 1.0f));
        attr.setCornerRadius(0);
        attr.setImageSource("src\\resource\\annotation.png");
        attr.setLeader(AVKey.SHAPE_NONE);
        attr.setSize(new Dimension(150, 110));
        annotation.setAttributes(attr);
        AnnotationLayer layer=new AnnotationLayer();
        layer.addAnnotation(annotation);
        layerList.add(layer);

        LayerList layerList2 = new LayerList();
        try {
            layerList2.add(layerList.getLayerByName("世界底图"));
        } catch (Exception ex) {
            System.out.println("Can not File Layer世界底图");
        }
        try {
            layerList2.add(layerList.getLayerByName("世界影像[500m]"));
        } catch (Exception ex) {
            System.out.println("Can not File Layer世界影像[500m]");
        }
        try {
            layerList2.add(layerList.getLayerByName("中国影像[60m]"));
        } catch (Exception ex) {
            System.out.println("Can not File Layer中国影像[60m]");
        }
        try {
            layerList2.add(layerList.getLayerByName("中国省界"));
        } catch (Exception ex) {
            System.out.println("Can not File Layer中国省界");
        }
        try {
            layerList2.add(layerList.getLayerByName("中国市界"));
        } catch (Exception ex) {
            System.out.println("Can not File Layer中国市界");
        }
        try {
            layerList2.add(layerList.getLayerByName("中国县界"));
        } catch (Exception ex) {
            System.out.println("Can not File Layer中国县界");
        }
        
        try {
            layerList2.add(layerList.getLayerByName("图层示例"));
        } catch (Exception ex) {
            System.out.println("Can not File Layer图层示例");
        }
        
        
        try {
            layerList2.add(layerList.getLayerByName("大气层"));
        } catch (Exception ex) {
            System.out.println("Can not File Layer大气层");
        }
        try {
            layerList2.add(layerList.getLayerByName("地面任务区域"));
        } catch (Exception ex) {
            System.out.println("Can not File Layer地面任务区域");
        }
        try {
            layerList2.add(layerList.getLayerByName("区域覆盖结果"));
        } catch (Exception ex) {
            System.out.println("Can not File Layer区域覆盖结果");
        }
        
        try {
            layerList2.add(layerList.getLayerByName("三维显示"));
        } catch (Exception ex) {
            System.out.println("Can not File Layer 三维显示");
        }
        
        
        
        
        layerList = layerList2;

        fillModel(layerList);
        layerList.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                if (event.getSource() instanceof LayerList) // the layer list lost, gained or swapped layers
                {
                    refresh(event);
                    controller.redraw();
                }
                else if (event.getSource() instanceof Layer)
                {
                    jlist.repaint(); // just the state of the layer changed
                    controller.redraw();
                }
            }
        });

        ReorderListener listener = new ReorderListener(jlist);
        this.jlist.addMouseListener(listener);
        this.jlist.addMouseMotionListener(listener);

        final JPopupMenu popup = this.makePopupMenu();
        this.jlist.setInheritsPopupMenu(true);
        this.jlist.addMouseListener(new MouseInputAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (popup.isPopupTrigger(mouseEvent))
                    popup.show(panel, mouseEvent.getX(), mouseEvent.getY());
            }
        });
    }

    private JPopupMenu makePopupMenu()
    {
        JCheckBoxMenuItem showInternal = new JCheckBoxMenuItem("Show Internal Layers");
        showInternal.setSelected(false);
        showInternal.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                setIncludeInternalLayers(((JCheckBoxMenuItem) event.getSource()).isSelected());
            }
        });

        JPopupMenu pm = new JPopupMenu();
        pm.add(showInternal);

        return pm;
    }

    public boolean isIncludeInternalLayers()
    {
        return this.jlist.isIncludeInternalLayers();
    }

    public void setIncludeInternalLayers(boolean includeInternalLayers)
    {
        if (includeInternalLayers == this.jlist.isIncludeInternalLayers())
            return;

        this.jlist.setIncludeInternalLayers(includeInternalLayers);
        this.fillModel(this.controller.getActiveLayers());
    }

    @Override
    public boolean isTwoState()
    {
        return true;
    }

    @Override
    public boolean isOn()
    {
        return this.on;
    }

    @Override
    public void turnOn(boolean tf)
    {
        this.firePropertyChange("ShowLayerList", this.on, tf);
        this.on = !this.on;
    }

    protected void fillModel(LayerList layerList)
    {
        model.clear();

        for (Layer layer : layerList)
        {
            this.model.addElement(layer);
        }
    }

    protected void refresh(PropertyChangeEvent event)
    {
        // update the model
        if (event.getNewValue() instanceof LayerList)
            this.fillModel((LayerList) event.getNewValue());

        // scroll list to changed layer
        if (event.getOldValue() instanceof LayerList && event.getNewValue() instanceof LayerList)
        {
            java.util.List<Layer> delta = LayerList.getLayersAdded((LayerList) event.getOldValue(),
                (LayerList) event.getNewValue());
            if (delta.size() > 0)
                this.jlist.ensureIndexIsVisible(((LayerList) event.getNewValue()).indexOf(delta.get(delta.size() - 1)));
        }
    }

    public void updateLayerList(LayerList layerList)
    {
        if (layerList == null)
            layerList = this.controller.getWWd().getModel().getLayers();

        ArrayList<Layer> newList = new ArrayList<Layer>(layerList.size());
        for (int i = 0; i < this.model.size(); i++)
        {
            newList.add((Layer) this.model.get(i));
        }

        layerList.replaceAll(newList);
    }

    // Handles reordering of the World Wind layer list via this panel.
    protected class ReorderListener extends MouseAdapter
    {
        protected JList list;
        protected int hotspot = new JCheckBox().getPreferredSize().width;
        protected int pressIndex = 0;
        protected int releaseIndex = 0;

        public ReorderListener(JList list)
        {
            if (!(list.getModel() instanceof DefaultListModel))
            {
                throw new IllegalArgumentException("List must have a DefaultListModel");
            }
            this.list = list;
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            int index = list.locationToIndex(e.getPoint());
            pressIndex = (e.getX() > list.getCellBounds(index, index).x + hotspot) ? index : -1;
            if (pressIndex == -1)
            {
                Layer layer = (Layer) model.get(index);
                layer.setEnabled(!layer.isEnabled());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            releaseIndex = list.locationToIndex(e.getPoint());
            if (releaseIndex != pressIndex && releaseIndex != -1 && pressIndex != -1)
            {
                reorder();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            if (pressIndex != -1)
            {
                mouseReleased(e);
                pressIndex = releaseIndex;
            }
        }

        protected void reorder()
        {
            DefaultListModel model = (DefaultListModel) list.getModel();
            Object dragee = model.elementAt(pressIndex);
            model.removeElementAt(pressIndex);
            model.insertElementAt(dragee, releaseIndex);
            updateLayerList(null);
            controller.redraw();
        }
    }
}
