package multiScaleErrorDiffusion;
import java.awt.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import ImgOperation.ImageOperation;
import publicClass.publicClass;

public class test extends publicClass {
    public static void main(String[] args) {
    	double [][] img =ImageOperation.readImg("lena.bmp");
    	
    	double[][] a={
    			{8,5,3},
    			{6,2,1},
    			{7,9,3}
    	};
    	
    	//sortArray(img);
    	
    	sortArray(a);
    	
    	/*int count = 0;
    	for(int i=0;i<h;i++){
    		for(int j=0;j<w;j++){
    			//System.out.print(img[i][j]+" ");
    			if(img[i][j] == 1){
    				count++;
    			}
    		}
    	  //System.out.println();
    	}
    	System.out.println(count);*/
    	    	
    	/*
    	//二维数组转一维
    	double[][] a={
    			{8,5,4},
    			{6,2,1},
    			{7,9,3}
    	};
    	
    	
    	
    	double[] b = ImageOperation.matrixToArray(a);
    	for(int i=0;i<b.length;i++)
    	{
    		System.out.println(b[i]+" "+a[i/3][i%3]);

        }
    	
    	//将一维转二维
    	  	        	
    	HashMap map=new HashMap();
    	for(int i=0;i<b.length;i++)
    	{
    	   map.put(b[i],i); //将值和下标存入Map
    	}
    	
    	//排列
    	ArrayList list=new ArrayList();
    	Arrays.sort(b); //升序排列
    	for(int i=0;i<b.length;i++)
    	{
    	   list.add(b[i]);
    	}
    	Collections.reverse(list); //逆序排列,变为降序
    	for(int i=0;i<list.size();i++)
    	{
    	  b[i]=(double)list.get(i);
    	
    	  //查找原始下标
    	  int index = 	(int) map.get(b[i]);	
    	  Point point = new Point(index/3,index%3);
    	  System.out.println(b[i]+"("+point.x+","+point.y+")");
    	}*/
    	
	}
    
    
    //只运行一次，每次只取第一个数的下标，取完则删除
    public static int  sortArray(double [][] a){
    	
    	//二维数组转一维  	
    	double[] b = ImageOperation.matrixToArray(a);
    	
    	HashMap map=new HashMap();
    	for(int i=0;i<b.length;i++)
    	{
    	   map.put(b[i],i); //将值和下标存入Map
    	}
    	
    	//排列
    	ArrayList<HashMap<Integer, Double>> list=new ArrayList<HashMap<Integer, Double>>();
    	
    	Arrays.sort(b); //升序排列
    	
    	for(int i=0;i<b.length;i++)
    	{
    	   list.add((HashMap<Integer, Double>) new HashMap().put(i,b[i]));
    	}
    	
    	Collections.reverse(list); //逆序排列,变为降序
    	
    	
    	while(list.size()>0){
    		//查找原始下标
    	 HashMap<Integer, Double> maxPix = (HashMap<Integer, Double>) list.get(0);
    	 System.out.println(maxPix);
    	            
         Set<Integer> keySets = maxPix.keySet();              //获取键 的Set集合  
       
         System.out.print("Map键：");  
         for(Integer keySet:keySets){                    //迭代输出键  
             System.out.print(keySet+" ");  
         }  
    	 
    	 
    	 
    	 
    	 
      	 /* int index = 	maxPix.getKey();	
      	  Point point = new Point(index/3,index%3);
      	  System.out.println("取最大元素："+maxPix+"("+point.x+","+point.y+")");*/
      	  
      	  
    		//System.out.println("取最大元素："+list.get(0));
        	list.remove(0);
        	
    	}
    	
              	
    	return list.size();
    	
    }
    
    
}
