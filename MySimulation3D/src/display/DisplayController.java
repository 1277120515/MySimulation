/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package display;


import coverage.util.Time;
import gov.nasa.worldwind.layers.RenderableLayer;
import com.vividsolutions.jts.geom.MultiPolygon;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.ScreenRelativeAnnotation;
import gov.nasa.worldwind.render.SurfacePolygon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.*;
import javax.swing.JOptionPane;
import simulationpanel.RegionManagerClass;

/**
 *
 * @author ZZL
 */
public class DisplayController
{
    public boolean isShowSatelliteOrbit = false;    //是否显示卫星轨道
    public boolean isShowSensor = false;            //是否显示传感器三角
    public boolean isShowGroundRegion = false;      //是否显示地面任务区域
    public boolean isShowMaxCourageRange = false;   //是否显示最大拍摄范围
    public boolean isShowCourageRange = false;      //是否显示当前传感器覆盖范围
    public boolean isShowSatellite = false;         //是否显示卫星

    public ArrayList<SatelliteElem> satelliteElemList;  //卫星列表
    public MultiPolygon ground;                         //地面任务区域

    /**
     * 拍摄场景开始时间
     */
    public Time startTime;

    /**
     *拍摄场景结束时间
     */
    public Time endTime;

    private RenderableLayer displayLayer;//WWD 3D仿真图层
    WorldWindow wwd;

    /**
     *
     * @param wwd WorldWind
     * @param layer WWD 3D仿真图层
     */
    public DisplayController(WorldWindow wwd, RenderableLayer layer)
    {
        displayLayer = layer;
        this.wwd = wwd;

        InitTimer();//初始化定时器
    }

    /**
     * 当前时间 
     */
    public Time currentTime;

    private boolean isRun = false;//是否处于仿真进行状态 true：正在进行仿真 false：暂停
    private int speed = 1; //仿真速度  

    //定时器初始化，每秒执行一次
    private void InitTimer()
    {
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                //该函数每秒执行一次
                if (isRun == true)          //通过查询isRun的值，判断是否切换帧
                {
                    ChangeFrame(speed);     //切换帧
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 1000);      //定时间隔1秒
    }

    /**
     *
     * @param s 仿真速度
     */
    public void SetSpeed(int s)
    {
        speed = s;
    }

    /**
     *
     * @return 仿真速度
     */
    public int GetSpeed()
    {
        return speed;
    }

    /**
     * 仿真开始
     */
    public void Start()
    {
        isRun = true;
    }

    /**
     * 仿真暂停
     */
    public void Suspend()
    {
        isRun = !isRun;
    }

    /**
     * 仿真重置。返回初始状态
     */
    public void Reset()
    {
        isRun = false;
        speed = 1;
        currentTime = startTime.clone();
        display();
    }

    /**
     *
     * @param second
     */
    public void ChangeFrame(int second)
    {
        if (second >= 0)
        {
            currentTime.addSeconds(second);
            if (currentTime.after(endTime))
            {
                currentTime.addSeconds(-second);    //当前时间减少second秒
            }
        } else
        {
            currentTime.addSeconds(second);
            if (currentTime.before(startTime))
            {
                currentTime.addSeconds(-second);//当前时间增加second秒
            }
        }
        display();
    }

    /**
     * 下一帧
     */
    public void NextFrame()
    {
        ChangeFrame(1);
    }

    /**
     * 上一帧
     */
    public void LastFrame()
    {
        ChangeFrame(-1);
    }

     /**
     * 检验startTime currentTime endTime是否合法，应满足：
     * DisplayController.startTime == SatelliteElem.startTime <= DisplayController.currentTime <= DisplayController.endTime == SatelliteElem.EndTime
     */
    private boolean CheckTime()
    {
        if (startTime.afterOrEqual(endTime))
        {
            JOptionPane.showMessageDialog(null, "DisplayController.startTime应早于DisplayController.endTime！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        if (currentTime.after(endTime) || currentTime.before(startTime))
        {
            JOptionPane.showMessageDialog(null, "currentTime不应早于DisplayController.startTime，不应晚于DisplayController.endTime！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        for (SatelliteElem satelliteElem : satelliteElemList)
        {
            if (!(satelliteElem.startTime.equal(this.startTime) && satelliteElem.endTime.equal(this.endTime)))
            {
                JOptionPane.showMessageDialog(null, "satelliteElem的起止时间应与DisplayController的起止时间相同！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            for (ShotUnit shotUnit : satelliteElem.shotUnitList)
            {
                if (shotUnit.startTime.before(satelliteElem.startTime) || shotUnit.endTime.after(satelliteElem.endTime))
                {
                    JOptionPane.showMessageDialog(null, "shotUnit.startTime不应早于satelliteElem.startTime，shotUnit.endTime不应晚于satelliteElem.endTime！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
                if (shotUnit.startTime.afterOrEqual(shotUnit.endTime))
                {
                    JOptionPane.showMessageDialog(null, "shotUnit.startTime应早于satelliteElem.endTime！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

     /**
     * 显示场景
     */
    private void display()
    {
        if (CheckTime() == false)//判断几个时间是否合法
        {
            return;
        }
        displayLayer.removeAllRenderables();//去除上一帧的所有物体

        //显示地面任务区域
        if (isShowGroundRegion == true)
        {
            SurfacePolygon[] spArray = RegionManagerClass.MultiPolygon2SurfacePolygon(ground);
            for (int i = 0; i < spArray.length; i++)
            {
                displayLayer.addRenderable(spArray[i]);
            }
        }

        if (satelliteElemList != null && satelliteElemList.size() > 0)
        {
            for (SatelliteElem satelliteElem : satelliteElemList)
            {

                //遍历每一颗卫星
                //是否显示卫星轨迹
                if (isShowSatelliteOrbit == true)
                {
                    satelliteElem.ShowOrbit(displayLayer, currentTime);
                }

                //是否显示卫星和名称
                if (isShowSatellite == true)
                {
                    satelliteElem.ShowSatellite(displayLayer, currentTime);
                }

                //是否显示传感器最大覆盖范围
                if (isShowMaxCourageRange == true)
                {
                    satelliteElem.ShowMaxCourageRange(displayLayer, currentTime);
                }

                //是否显示条带
                if (isShowCourageRange == true)
                {
                    satelliteElem.ShowCourageRange(displayLayer, currentTime);
                }
                //是否显示传感器三角
                if (isShowSensor == true)
                {
                    satelliteElem.ShowMaxSensorTriangle(displayLayer, currentTime);
                    satelliteElem.ShowSensorTriangle(displayLayer, currentTime);
                }

            }
        }
        //重绘
        wwd.redraw();
    }
}
