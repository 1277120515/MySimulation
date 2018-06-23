/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package display;

import java.awt.Color;

/**
 *
 * @author ZZL
 */
public class DisplayConfig
{
    public static Color orbitColor = Color.red;                 //轨道轨迹颜色
    public static double orbitWidth = 2;                        //轨道轨迹宽度

    public static Color sensorInnerColor = Color.green;         //传感器三角内部颜色
    public static double sensorInnerOpacity = 0.4;              //传感器三角内部透明度
    public static Color sensorOutlineColor = Color.green;       //传感器三角轮廓颜色
    public static double sensorOutlineOpacity = 1;              //传感器三角轮廓透明度
    public static Color scanLineColor = Color.RED;              //扫描线颜色
    public static double scanLineWidth = 4;                     //扫描线宽度
    public static double scanLineOpacity = 1;                   //扫描线透明度

    public static Color sensorMaxInnerColor = new Color(255, 97, 0);    //传感器最大覆盖范围三角内部颜色
    public static double sensorMaxInnerOpacity = 0.2;                   //传感器最大覆盖范围三角内部透明度
    public static Color sensorMaxOutlineColor =  new Color(255, 97, 0); //传感器最大覆盖范围三角轮廓颜色
    public static double sensorMaxOutlineOpacity = 0.5;                 //传感器最大覆盖范围三角轮廓透明度
    public static Color scanMaxLineColor =new Color(255, 97, 0);        //最大范围扫描线颜色
    public static double scanMaxLineWidth = 2;                          //最大范围扫描线宽度
    public static double scanMaxLineOpacity = 0.5;                      //最大范围扫描线透明度

    public static Color stripeInnerColor = Color.blue;      //条带内部颜色
    public static double stripeInnerOpacity = 0.4;          //条带内部透明度
    public static Color stripeOutlineColor = Color.orange;  //条带轮廓颜色
    public static double stripeOutlineOpacity = 1;          //条带轮廓透明度
    public static double stripeOutlineWidth = 2;            //条带轮廓宽度

}
