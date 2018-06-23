/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package display;

import coverage.util.Time;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import java.awt.Color;
import java.awt.Insets;
import java.util.ArrayList;

/**
 *
 * @author ZZL
 */
public class SatelliteElem
{

    /**
     *卫星名称
     */
    public String satelliteName;

    /**
     *卫星运行开始时间
     */
    public Time startTime;

    /**
     *卫星运行停止时间
     */
    public Time endTime;

    /**
     *卫星位置数组。
     * 长度 = startTime与endTime间隔的秒数。
     * startTime与endTime之间的每一秒 对应 satellitePosArray中的一个元素。
     */
    public Position[] satellitePosArray;

    /**
     *卫星拍摄条带数组。
     */
    public ArrayList<ShotUnit> shotUnitList;

    /**
     *
     * @param layer 图层
     * @param currentTime 当前时间，应满足：startTime &lt= currentTime &lt= endTime
     * 
     */
    public void ShowSatellite(RenderableLayer layer, Time currentTime)
    {

        if (currentTime.afterOrEqual(startTime) && currentTime.beforeOrEqual(endTime))
        {
            int satelliteIndex = 0;

            for (Time tempTime = startTime.clone(); tempTime.beforeOrEqual(currentTime) && tempTime.beforeOrEqual(endTime); tempTime.addSeconds(1))
            {
                if (tempTime.after(currentTime))
                {
                    break;
                }
                satelliteIndex++;
            }
            satelliteIndex--;

            AnnotationAttributes attr = new AnnotationAttributes();
            attr.setBackgroundColor(new Color(0, 0, 0, 0));
            attr.setBorderColor(new Color(255, 255, 255, 128));
            attr.setTextColor(Color.yellow);
            attr.setBorderWidth(2);
            attr.setCornerRadius(5);
            attr.setInsets(new Insets(8, 8, 8, 8));
            attr.setHighlighted(false);
            attr.setLeaderGapWidth(10);
            GlobeAnnotation satelliteNameAnnotation = new GlobeAnnotation(satelliteName, satellitePosArray[satelliteIndex], attr);
            layer.addRenderable(satelliteNameAnnotation);
        }
    }

    /**
     *
     * @param layer 图层
     * @param currentTime 当前时间
     */
    public void ShowOrbit(RenderableLayer layer, Time currentTime)
    {
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setDrawInterior(false);
        attrs.setInteriorMaterial(new Material(DisplayConfig.orbitColor));
        attrs.setInteriorOpacity(0.1);
        attrs.setDrawOutline(true);
        attrs.setOutlineMaterial(new Material(DisplayConfig.orbitColor));
        attrs.setOutlineWidth(DisplayConfig.orbitWidth);
        attrs.setOutlineOpacity(1);

        ArrayList<Position> pathPositions = new ArrayList<Position>();
        int index = 0;
        for (Time tempTime = startTime.clone(); tempTime.beforeOrEqual(currentTime) && tempTime.beforeOrEqual(endTime); tempTime.addSeconds(1))
        {
            pathPositions.add(satellitePosArray[index]);
            index++;
        }

        Path path = new Path(pathPositions);
        path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        path.setHighlightAttributes(attrs);
        path.setAttributes(attrs);

        layer.addRenderable(path);
    }

    /**
     *
     * @param layer 图层 
     * @param currentTime 当前时间
     */
    public void ShowMaxSensorTriangle(RenderableLayer layer, Time currentTime)
    {
        ShowTriangle(layer, currentTime, true);
    }

    /**
     *
     * @param layer 图层 
     * @param currentTime 当前时间
     */
    public void ShowSensorTriangle(RenderableLayer layer, Time currentTime)
    {
        ShowTriangle(layer, currentTime, false);
    }
    /**
     *
     * @param layer 图层 
     * @param currentTime 当前时间
     * @param isMax 显示最大拍摄范围 还是显示实际拍摄范围
     */
    private void ShowTriangle(RenderableLayer layer, Time currentTime, boolean isMax)
    {
        //显示传感器三角
        if (currentTime.afterOrEqual(startTime) && currentTime.beforeOrEqual(endTime))
        {
            int satelliteIndex = 0;

            for (Time tempTime = startTime.clone(); tempTime.beforeOrEqual(currentTime) && tempTime.beforeOrEqual(endTime); tempTime.addSeconds(1))
            {
                if (tempTime.after(currentTime))
                {
                    break;
                }
                satelliteIndex++;
            }
            satelliteIndex--;

            for (ShotUnit shotUnit : shotUnitList)
            {
                if (currentTime.afterOrEqual(shotUnit.startTime) && currentTime.beforeOrEqual(shotUnit.endTime))
                {
                    int shotIndex = 0;
                    for (Time tempTime = shotUnit.startTime.clone(); tempTime.beforeOrEqual(currentTime) && tempTime.beforeOrEqual(shotUnit.endTime); tempTime.addSeconds(1))
                    {
                        if (tempTime.after(currentTime))
                        {
                            break;
                        }
                        shotIndex++;
                    }
                    shotIndex--;

                    Polygon pg = new Polygon();
                    pg.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);

                    ShapeAttributes attrs = new BasicShapeAttributes();
                    attrs.setDrawInterior(true);
                    attrs.setDrawOutline(true);
                    if (isMax == false)
                    {
                        attrs.setInteriorMaterial(new Material(DisplayConfig.sensorInnerColor));
                        attrs.setInteriorOpacity(DisplayConfig.sensorInnerOpacity);
                        attrs.setOutlineMaterial(new Material(DisplayConfig.sensorOutlineColor));
                        attrs.setOutlineOpacity(DisplayConfig.sensorOutlineOpacity);
                    } else
                    {
                        attrs.setInteriorMaterial(new Material(DisplayConfig.sensorMaxInnerColor));
                        attrs.setInteriorOpacity(DisplayConfig.sensorMaxInnerOpacity);
                        attrs.setOutlineMaterial(new Material(DisplayConfig.sensorMaxOutlineColor));
                        attrs.setOutlineOpacity(DisplayConfig.sensorMaxOutlineOpacity);
                    }
                    pg.setAttributes(attrs);
                    pg.setHighlightAttributes(attrs);

                    ArrayList<Position> posList = new ArrayList<Position>();
                    posList.add(satellitePosArray[satelliteIndex]);
                    if (isMax == false)
                    {
                        posList.add(shotUnit.leftPosArray[shotIndex]);
                        posList.add(shotUnit.rightPosArray[shotIndex]);
                    } else
                    {
                        posList.add(shotUnit.leftMaxPosArray[shotIndex]);
                        posList.add(shotUnit.rightMaxPosArray[shotIndex]);
                    }
                    pg.setOuterBoundary(posList);
                    layer.addRenderable(pg);

                    //显示扫描线
                    SurfacePolyline sp = new SurfacePolyline();
                    ShapeAttributes attrs2 = new BasicShapeAttributes();
                    attrs2.setDrawOutline(true);
                    if (isMax == false)
                    {
                        attrs2.setOutlineMaterial(new Material(DisplayConfig.scanLineColor));
                        attrs2.setOutlineWidth(DisplayConfig.scanLineWidth);
                        attrs2.setOutlineOpacity(DisplayConfig.scanLineOpacity);
                    } else
                    {
                        attrs2.setOutlineMaterial(new Material(DisplayConfig.scanMaxLineColor));
                        attrs2.setOutlineWidth(DisplayConfig.scanMaxLineWidth);
                        attrs2.setOutlineOpacity(DisplayConfig.scanMaxLineOpacity);
                    }
                    sp.setAttributes(attrs2);
                    sp.setHighlightAttributes(attrs2);

                    posList.remove(0);
                    sp.setLocations(posList);
                    layer.addRenderable(sp);
                }
            }

        }

    }

    /**
     *
     * @param layer 图层
     * @param currentTime 当前时间
     */
    public void ShowCourageRange(RenderableLayer layer, Time currentTime)
    {
        ShowRange(layer, currentTime, false);
    }

    /**
     *
     * @param layer 图层
     * @param currentTime 当前时间
     */
    public void ShowMaxCourageRange(RenderableLayer layer, Time currentTime)
    {

    }

        /**
     *
     * @param layer 图层 
     * @param currentTime 当前时间
     * @param isMax 显示最大拍摄范围 还是显示实际拍摄范围
     */
    private void ShowRange(RenderableLayer layer, Time currentTime, boolean isMax)
    {
        for (ShotUnit shotUnit : shotUnitList)
        {
            if (currentTime.afterOrEqual(shotUnit.startTime) && currentTime.beforeOrEqual(shotUnit.endTime))
            {

                int shotIndex = 0;
                for (Time tempTime = shotUnit.startTime.clone(); tempTime.beforeOrEqual(currentTime) && tempTime.beforeOrEqual(shotUnit.endTime); tempTime.addSeconds(1))
                {
                    if (tempTime.after(currentTime))
                    {
                        break;
                    }
                    shotIndex++;
                }
                shotIndex--;

                ArrayList<Position> stripeOutlineList = new ArrayList<Position>();
                for (int i = 0; i <= shotIndex; i++)
                {
                    stripeOutlineList.add(shotUnit.leftPosArray[i]);
                }
                for (int i = shotIndex; i >= 0; i--)
                {
                    stripeOutlineList.add(shotUnit.rightPosArray[i]);
                }
                SurfacePolygon stripeOutline = new SurfacePolygon();

                ShapeAttributes attrs = new BasicShapeAttributes();
                attrs.setDrawInterior(true);
                attrs.setInteriorMaterial(new Material(DisplayConfig.stripeInnerColor));
                attrs.setInteriorOpacity(DisplayConfig.stripeInnerOpacity);
                attrs.setDrawOutline(true);
                attrs.setOutlineMaterial(new Material(DisplayConfig.stripeOutlineColor));
                attrs.setOutlineWidth(DisplayConfig.stripeOutlineWidth);
                attrs.setOutlineOpacity(DisplayConfig.stripeOutlineOpacity);
                stripeOutline.setAttributes(attrs);
                stripeOutline.setHighlightAttributes(attrs);

                stripeOutline.setLocations(stripeOutlineList);

                layer.addRenderable(stripeOutline);

            }
            else if(currentTime.after(shotUnit.endTime))
            {
                ArrayList<Position> stripeOutlineList = new ArrayList<Position>();
                int len=shotUnit.leftPosArray.length;
                for (int i = 0; i <len; i++)
                {
                    stripeOutlineList.add(shotUnit.leftPosArray[i]);
                }
                for (int i = len-1; i >= 0; i--)
                {
                    stripeOutlineList.add(shotUnit.rightPosArray[i]);
                }
                SurfacePolygon stripeOutline = new SurfacePolygon();

                ShapeAttributes attrs = new BasicShapeAttributes();
                attrs.setDrawInterior(true);
                attrs.setInteriorMaterial(new Material(DisplayConfig.stripeInnerColor));
                attrs.setInteriorOpacity(DisplayConfig.stripeInnerOpacity);
                attrs.setDrawOutline(true);
                attrs.setOutlineMaterial(new Material(DisplayConfig.stripeOutlineColor));
                attrs.setOutlineWidth(DisplayConfig.stripeOutlineWidth);
                attrs.setOutlineOpacity(DisplayConfig.stripeOutlineOpacity);
                stripeOutline.setAttributes(attrs);
                stripeOutline.setHighlightAttributes(attrs);

                stripeOutline.setLocations(stripeOutlineList);

                layer.addRenderable(stripeOutline);
            }

        }
    }

}
