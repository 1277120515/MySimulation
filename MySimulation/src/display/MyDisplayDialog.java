/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package display;

import coverage.OneDayCoverage;
import coverage.iostruct.PositionVelocityOutput;
import coverage.iostruct.ProcessResource;
import coverage.iostruct.SatelliteInput;
import coverage.util.CoorTrans;
import coverage.util.Time;
import gov.nasa.worldwind.WorldWind;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwindx.applications.worldwindow.core.Constants;
import gov.nasa.worldwindx.applications.worldwindow.features.swinglayermanager.ActiveLayersPanel;
import gov.nasa.worldwindx.applications.worldwindow.core.Controller;
import java.awt.Color;
import java.util.*;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author ZZL
 */
public class MyDisplayDialog extends JDialog {
    
    Controller controller;
    WorldWindow wwd;
    RenderableLayer displayLayer;
    
    public MyDisplayDialog(Controller c, Frame owner) {
        super(owner);
        this.controller=c;
        this.wwd = c.getWWd();
        
        displayLayer=(RenderableLayer)wwd.getModel().getLayers().getLayerByName("三维显示");

       // wwd.getModel().getLayers().add(displayLayer);
        //ActiveLayersPanel activeLayersPanel=(ActiveLayersPanel)controller.getRegisteredObject(Constants.FEATURE_ACTIVE_LAYERS_PANEL);

        InitDialog();
        
        
        
        InitSatellitePos();
        InitScene();

        InitTimer();
    }

    private Box makePanel1() {
        Box box;
        JButton btn;
        JPanel panel;

        Box resbox = Box.createVerticalBox();

        box = Box.createHorizontalBox();

        btn = new JButton("开始");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StartAnimation();
            }
        });
        box.add(btn);

        btn = new JButton("暂停");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SuspendAnimation();
            }
        });
        box.add(btn);

        btn = new JButton("重置");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResetAnimation();
            }
        });
        box.add(btn);

        btn = new JButton("加速");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangeSpeed(1);
            }
        });
        box.add(btn);
        

        
        btn = new JButton("减速");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
ChangeSpeed(-1);
            }
        });
        box.add(btn);

        panel = new JPanel();
        panel.add(box);
        resbox.add(panel);

        box = Box.createHorizontalBox();

        btn = new JButton("前进1S");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangeFrame(1);
            }
        });
        box.add(btn);
        btn = new JButton("后退1S");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangeFrame(-1);
            }
        });
        box.add(btn);
        btn = new JButton("前进10S");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangeFrame(10);
            }
        });
        box.add(btn);
        btn = new JButton("后退10S");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangeFrame(-10);
            }
        });
        box.add(btn);

        panel = new JPanel();
        panel.add(box);
        resbox.add(panel);

        return resbox;

    }

    JTextField txtStartTime;
    JTextField txtEndTime;
    JTextField txtTle;
    JTextField txtSwingAngle;
    JTextField txtFov;
    JTextField txtCurrentTime;

    String startTime = "2015-10-01 12:00:00";
    String endTime = "2015-10-01 12:01:00";
    String tle = "1 26619U 00075A   16293.73279326 -.00000071  00000-0 -49450-5 0  9993;2 26619  97.8636 328.0037 0010174 143.8408 216.3455 14.64311646848348";
    String swingAngle = "0";
    String fov = "20";

    private Box makePanel2() {
        Box box;
        JButton btn;
        JLabel label;
        JPanel panel;

        txtStartTime = new JTextField(startTime);
        txtEndTime = new JTextField(endTime);
        txtTle = new JTextField(tle);
        txtSwingAngle = new JTextField(swingAngle);
        txtFov = new JTextField(fov);
        txtCurrentTime=new JTextField();

        Box resbox = Box.createVerticalBox();
        
        box = Box.createHorizontalBox();
        box.add(new JLabel("实时时间："));
        txtCurrentTime.setEditable(false);
        box.add(txtCurrentTime);
        resbox.add(box);

        box = Box.createHorizontalBox();
        box.add(new JLabel("开始时间："));
        box.add(txtStartTime);
        resbox.add(box);

        box = Box.createHorizontalBox();
        box.add(new JLabel("结束时间："));
        box.add(txtEndTime);
        resbox.add(box);

        box = Box.createHorizontalBox();
        box.add(new JLabel("TLE根数："));
        box.add(txtTle);
        resbox.add(box);

        box = Box.createHorizontalBox();
        box.add(new JLabel("侧摆角："));
        box.add(txtSwingAngle);
        resbox.add(box);

        box = Box.createHorizontalBox();
        box.add(new JLabel("视场角："));
        box.add(txtFov);
        resbox.add(box);

        panel = new JPanel();
        btn = new JButton("确认");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangePara();
            }
        });
        panel.add(btn);
        resbox.add(panel);

        return resbox;

    }

    private void InitDialog() {
        this.setTitle("三维显示");
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(300, 400));
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.NORTH, makePanel1());
        this.add(BorderLayout.SOUTH, makePanel2());
        this.pack();
        this.setLocationRelativeTo(null);
    }
    
    private PositionVelocityOutput[] pvo;
    
    private void InitSatellitePos() {
        HashMap<String, String> tleMap = ProcessResource.ReadTle("src\\resource\\tle.txt");
        String satelliteXml = "zy02c.xml";
        SatelliteInput sli = ProcessResource.ReadSatellite("src\\resource\\satellite\\" + satelliteXml, tleMap);
        String startTimeStr = "2014-08-01 00:00:00.000";
        String endTimeStr = "2014-08-02 00:15:00.000";
        
        startTimeStr = startTime;
        endTimeStr=endTime;
        sli.satElement=tle;
        
        pvo = OneDayCoverage.calPosition(sli, new Time(startTimeStr), new Time(endTimeStr), 1);
    }
    
    Ellipsoid satellite3d;
    Polygon sensorTriangle3d;
    //SurfacePolygon stripe3d;
    
    void InitScene() {
        satellite3d = this.CreateSatellite();
        sensorTriangle3d = this.CreateSensorTriangle();
        
        RefreshScene();
    }
    
    int animationStep = 0;
    
    private void RefreshScene() {
        
        int i = animationStep;
        double[] r1;
        double[] v1;
        double[] LonLatLeft;
        double[] LonLatRight;
        double swingAngle = Double.parseDouble(this.swingAngle);
        double fov = Double.parseDouble(this.fov);
        
        r1 = new double[]{pvo[i].x_J2000C, pvo[i].y_J2000C, pvo[i].z_J2000C};
        v1 = new double[]{pvo[i].vx_J2000C, pvo[i].vy_J2000C, pvo[i].vz_J2000C};
        LonLatLeft = CoorTrans.getScanLatLon(r1, v1, swingAngle + fov / 2, 0);     //左侧 前一分钟 扫描边界与地球交点经纬度
        LonLatRight = CoorTrans.getScanLatLon(r1, v1, swingAngle - fov / 2, 0);    //右侧 前一分钟 扫描边界与地球交点经纬度
        
        Position satellitePos = Position.fromDegrees(pvo[i].Lat, pvo[i].Lon, pvo[i].Alt);
        Position leftPos = Position.fromDegrees(LonLatLeft[1], LonLatLeft[0], 0);
        Position rightPos = Position.fromDegrees(LonLatRight[1], LonLatRight[0], 0);
        
        displayLayer.removeAllRenderables();

        

        //显示传感器三角
        ArrayList<Position> posList = new ArrayList<Position>();
        posList.add(satellitePos);
        posList.add(leftPos);
        posList.add(rightPos);
        sensorTriangle3d.setOuterBoundary(posList);
        displayLayer.addRenderable(sensorTriangle3d);
        
        //显示卫星
        satellite3d.moveTo(satellitePos);
        displayLayer.addRenderable(satellite3d);

        
        
        //显示条带
        if (animationStep != 0) {
            ArrayList<Position> leftPosList = new ArrayList<Position>();
            ArrayList<Position> rightPosList = new ArrayList<Position>();
            for (int index = 0; index <= animationStep; index++) {
                r1 = new double[]{pvo[index].x_J2000C, pvo[index].y_J2000C, pvo[index].z_J2000C};
                v1 = new double[]{pvo[index].vx_J2000C, pvo[index].vy_J2000C, pvo[index].vz_J2000C};
                LonLatLeft = CoorTrans.getScanLatLon(r1, v1, swingAngle + fov / 2, 0);     //左侧 前一分钟 扫描边界与地球交点经纬度
                LonLatRight = CoorTrans.getScanLatLon(r1, v1, swingAngle - fov / 2, 0);    //右侧 前一分钟 扫描边界与地球交点经纬度

                leftPosList.add(Position.fromDegrees(LonLatLeft[1], LonLatLeft[0], 0));
                rightPosList.add(Position.fromDegrees(LonLatRight[1], LonLatRight[0], 0));
            }
            
            for (int index = 0; index+1 <= animationStep; index++) {
                SurfacePolygon stripe3d=this.CreateStripe();
                ArrayList<Position> stripe3dPosList=new ArrayList<Position>();
                stripe3dPosList.add(leftPosList.get(index));
                stripe3dPosList.add(leftPosList.get(index+1));
                stripe3dPosList.add(rightPosList.get(index+1));
                stripe3dPosList.add(rightPosList.get(index));
                stripe3d.setOuterBoundary(stripe3dPosList);    
                displayLayer.addRenderable(stripe3d);
            }

             Collections.reverse(leftPosList);
            ArrayList<Position> stripePosList = new ArrayList<Position>(leftPosList);
            stripePosList.addAll(rightPosList);

            SurfacePolyline stripeOutline3d = this.CreateStripePolyLine();
            stripeOutline3d.setLocations(stripePosList);

            displayLayer.addRenderable(stripeOutline3d);

        }

        //显示扫描线
        SurfacePolyline stripeScanLine3d = this.CreateScanLine();
        posList.remove(0);
        stripeScanLine3d.setLocations(posList);
        displayLayer.addRenderable(stripeScanLine3d);

        wwd.redraw();
    }

    
    int isAnimation = 0;
    int AnimationSpeed=1;
    void StartAnimation() {
        isAnimation = 1;
    }

    void SuspendAnimation() {
        isAnimation = 0;
    }

    void ResetAnimation() {
        animationStep = 0;
        isAnimation = 0;
        RefreshScene();
    }

    void ChangeFrame(int n) {
        if ((animationStep + n < pvo.length) && (0 <= animationStep + n)) {
            animationStep+=n;
            RefreshScene();
            this.txtCurrentTime.setText(pvo[animationStep].Time.toBJTime());
        }
    }

    void ChangeSpeed(int s)//1 ,-1
    {
        if (s == 1) {
            if (AnimationSpeed * 2 <= 16) {
                AnimationSpeed *= 2;
            }
        }
        if (s == -1) {
            if (AnimationSpeed / 2 >= 1) {
                AnimationSpeed /= 2;
            }
        }
    }

    void ChangePara() {
        animationStep = 0;
        isAnimation = 0;
        
        startTime = txtStartTime.getText();
        endTime = txtEndTime.getText();
        tle = txtTle.getText();
        swingAngle = txtSwingAngle.getText();
        fov = txtFov.getText();
        txtCurrentTime.setText("");
        
        InitSatellitePos();
        
        RefreshScene();
    }

    private void InitTimer() {
        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (isAnimation == 1) {
                    ChangeFrame(AnimationSpeed);
                    RefreshScene();
                }
            }
        };
        int period = 1000;//循环间隔：毫秒
        timer.schedule(timerTask, 0 * 1000, period);
    }
    
    Ellipsoid CreateSatellite() {
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setEnableLighting(true);
        attrs.setDrawInterior(true);
        attrs.setInteriorMaterial(Material.PINK);
        attrs.setInteriorOpacity(1);

        Ellipsoid sp = new Ellipsoid(Position.ZERO,200*1000,200*1000,200*1000);
        sp.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        sp.setAttributes(attrs);
        sp.setHighlightAttributes(attrs);
        return sp;
        
    }
    
    Polygon CreateSensorTriangle() {
        Polygon pg = new Polygon();
        pg.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setDrawInterior(true);
        attrs.setInteriorMaterial(Material.GREEN);
        attrs.setInteriorOpacity(0.1);
        attrs.setDrawOutline(true);
        attrs.setOutlineMaterial(Material.YELLOW);
        attrs.setOutlineWidth(2);
        attrs.setOutlineOpacity(0.9);
        pg.setAttributes(attrs);
        pg.setHighlightAttributes(attrs);
        
        return pg;
    }
    
    SurfacePolygon CreateStripe() {
        SurfacePolygon sp = new SurfacePolygon();
        
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setDrawInterior(true);
        attrs.setInteriorMaterial(new Material(new Color(255, 128, 10)));
        attrs.setInteriorOpacity(0.3);
        attrs.setDrawOutline(false);
        attrs.setOutlineMaterial(new Material(new Color(128, 0, 0)));
        attrs.setOutlineWidth(2);
        attrs.setOutlineOpacity(0.9);
        sp.setAttributes(attrs);
        sp.setHighlightAttributes(attrs);
        
        return sp;
    }
    
    SurfacePolyline CreateStripePolyLine() {
        SurfacePolyline sp = new SurfacePolyline();
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setDrawOutline(true);
        attrs.setOutlineMaterial(new Material(new Color(100, 0, 0)));
        attrs.setOutlineWidth(2);
        attrs.setOutlineOpacity(0.9);
        sp.setAttributes(attrs);
        sp.setHighlightAttributes(attrs);
        return sp;
    }
    
    SurfacePolyline CreateScanLine() {
        SurfacePolyline sp = new SurfacePolyline();
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setDrawOutline(true);
        attrs.setOutlineMaterial(new Material(new Color(255, 0, 0)));
        attrs.setOutlineWidth(4);
        attrs.setOutlineOpacity(1);
        sp.setAttributes(attrs);
        sp.setHighlightAttributes(attrs);
        return sp;
    }
    
}
