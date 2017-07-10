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
    	//��ά����תһά
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
    	
    	//��һάת��ά
    	  	        	
    	HashMap map=new HashMap();
    	for(int i=0;i<b.length;i++)
    	{
    	   map.put(b[i],i); //��ֵ���±����Map
    	}
    	
    	//����
    	ArrayList list=new ArrayList();
    	Arrays.sort(b); //��������
    	for(int i=0;i<b.length;i++)
    	{
    	   list.add(b[i]);
    	}
    	Collections.reverse(list); //��������,��Ϊ����
    	for(int i=0;i<list.size();i++)
    	{
    	  b[i]=(double)list.get(i);
    	
    	  //����ԭʼ�±�
    	  int index = 	(int) map.get(b[i]);	
    	  Point point = new Point(index/3,index%3);
    	  System.out.println(b[i]+"("+point.x+","+point.y+")");
    	}*/
    	
	}
    
    
    //ֻ����һ�Σ�ÿ��ֻȡ��һ�������±꣬ȡ����ɾ��
    public static int  sortArray(double [][] a){
    	
    	//��ά����תһά  	
    	double[] b = ImageOperation.matrixToArray(a);
    	
    	HashMap map=new HashMap();
    	for(int i=0;i<b.length;i++)
    	{
    	   map.put(b[i],i); //��ֵ���±����Map
    	}
    	
    	//����
    	ArrayList<HashMap<Integer, Double>> list=new ArrayList<HashMap<Integer, Double>>();
    	
    	Arrays.sort(b); //��������
    	
    	for(int i=0;i<b.length;i++)
    	{
    	   list.add((HashMap<Integer, Double>) new HashMap().put(i,b[i]));
    	}
    	
    	Collections.reverse(list); //��������,��Ϊ����
    	
    	
    	while(list.size()>0){
    		//����ԭʼ�±�
    	 HashMap<Integer, Double> maxPix = (HashMap<Integer, Double>) list.get(0);
    	 System.out.println(maxPix);
    	            
         Set<Integer> keySets = maxPix.keySet();              //��ȡ�� ��Set����  
       
         System.out.print("Map����");  
         for(Integer keySet:keySets){                    //���������  
             System.out.print(keySet+" ");  
         }  
    	 
    	 
    	 
    	 
    	 
      	 /* int index = 	maxPix.getKey();	
      	  Point point = new Point(index/3,index%3);
      	  System.out.println("ȡ���Ԫ�أ�"+maxPix+"("+point.x+","+point.y+")");*/
      	  
      	  
    		//System.out.println("ȡ���Ԫ�أ�"+list.get(0));
        	list.remove(0);
        	
    	}
    	
              	
    	return list.size();
    	
    }
    
    
}
