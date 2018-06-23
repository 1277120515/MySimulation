/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package display;

import coverage.util.Time;
import gov.nasa.worldwind.geom.Position;

//条带，卫星每一次拍摄，传感器将会开关机一次，开关机之间进行拍摄将形成一个条带。

/**
 *
 * @author ZZL
 */

public class ShotUnit
{

    /**
     * 传感器开机时间
     */
    public Time startTime;            

    /**
     * 传感器关机时间
     */
    public Time endTime;   

    /**
     *卫星最大拍摄范围，左边界经纬度
     * 长度 = startTime与endTime间隔的秒数。
     */
    public Position[] leftMaxPosArray; 

    /**
     *卫星最大拍摄范围，右边界经纬度
     * 长度 = startTime与endTime间隔的秒数。
     */
    public Position[] rightMaxPosArray; 

    /**
     *卫星按照特定侧摆角进行拍摄，条带左边界经纬度。
     * 长度 = startTime与endTime间隔的秒数
     */
    public Position[] leftPosArray;    

    /**
     *卫星按照特定侧摆角进行拍摄，条带右边界经纬度。
     * 长度 = startTime与endTime间隔的秒数
     */
    public Position[] rightPosArray;   
}
//说明：leftMaxPosArray  rightMaxPosArray  leftPosArray rightPosArray这几个数组的长度 = startTime与endTime间隔的秒数

