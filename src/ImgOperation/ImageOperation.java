package ImgOperation;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import publicClass.publicClass;

public class ImageOperation{

       
	   //static String ImagePath = "E:\\labImg\\Img\\";
	   static String ImagePath=publicClass.ImgPath;	  
	   
	   //ֻ�ܶ�8λ�ĻҶ�ͼ���������Ϊ
	   public static double[][] readImg(String ImageName){
		   
			BufferedImage oImage=null;
			File file = new File(ImagePath+ImageName);//����ͼ���·��
			try {
				oImage=ImageIO.read(file);
			} catch (IOException e) {
				e.getStackTrace();
			}
			
			WritableRaster oRaster=oImage.getRaster();
			int height=oRaster.getHeight();
			int width=oRaster.getWidth();
			
			
			double[] pixels=new double[height*width];
			oRaster.getPixels(0, 0, width, height, pixels);//�� double ������ʽ����ָ�����ص�������  getPixels()�������ж�ȡ
			/*double[][] ImgMatrix=arrayToMatrixCol(pixels, width, height);//һά����ת���ɶ�ά����*/	
			double[][] ImgMatrix=arrayToMatrix(pixels, width, height);
			return ImgMatrix;
		}
	   
	   
	   /*����ά����д��λ���Ϊ8λ�Ҷ�ͼ��,�������룬�������*/
	   public static void writeImg(double[][] arry,String outputName,int bufferedImageType){
		  
		   int height=arry.length;
		   int width=arry[0].length;
		  /*System.out.println("width="+width);
		   System.out.println("height="+height);*/
		   
		   /*double [] pixel=matrixToArrayCol(arry);*/
		   
		   double [] pixel=matrixToArray(arry);
		   //�Դ�����ֻ���������
		   BufferedImage outImage=new BufferedImage(width,height, bufferedImageType);// ����һ������ΪԤ����ͼ������֮һ�� BufferedImage
		   WritableRaster outRaster=outImage.getRaster();//����WritableRaster��չ�� Raste ���ṩ����д�빦�ܡ�
		   outRaster.setPixels(0, 0,width,height,pixel);//Ϊÿ������Ԫ�ذ���һ�������� double �������е����ؾ���������������
		  
			File f = new File(ImagePath + outputName);
			try{
				
				ImageIO.write(outImage, "bmp", f);
			   }catch(IOException e)
			   {
				   e.getStackTrace();
			   }
			
			int type = outImage.getType(); 
		    /*System.out.println("�Ҷ�ͼ��"+outputName+"�����ͣ�"+type);*/
	   }
	   

	  //ֻ�ܶ�24λ�ĻҶ�ͼ��
	   public static double [][] getGray(String ImageName) {

			BufferedImage bufferedImage = null;
			File file = new File(ImagePath+ImageName);//����ͼ���·��
			
			try {

				bufferedImage = ImageIO.read(file);

			} catch (IOException e) {

				e.printStackTrace();

			}

			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			double [][] imageGray = new double[width][height];
			double[] pixles=new double[width*height];

			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {

					pixles[i*width+j] = bufferedImage.getRGB(j, i)& 0xffffff;//

				}
			}
			
			imageGray=ImageOperation.arrayToMatrix(pixles, width, height);
			return imageGray;

		}
	   
	   
	  
	   /*����ά����д��λ���Ϊ24λ�Ҷ�ͼ��*/
	   public static BufferedImage print24GrayImg(double matrixGray[][],String outputName,int bufferedImageType) {
		   
		    
			int width = matrixGray.length;
			int height = matrixGray[0].length;
			BufferedImage bufferedImage = new BufferedImage(width, height,
					5);//
			double[][] imageMatrix = matrixGray;
			
			  for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {

					imageMatrix[i][j] = (int) matrixGray[i][j] << 16
							| (int) matrixGray[i][j] << 8 | (int) matrixGray[i][j];
					bufferedImage.setRGB(i, j, (int) imageMatrix[i][j]);
				 }
			  }
			
			  File f = new File(ImagePath + outputName);
				try{
					
					ImageIO.write(bufferedImage, "bmp", f);
				   }catch(IOException e)
				   {
					   e.getStackTrace();
				   }
				
		    int type = bufferedImage.getType(); 
		    System.out.println("�Ҷ�ͼ��"+outputName+"�����ͣ�"+type);
			return bufferedImage;
		
	}
	   
	   /*����ά����д�ɶ�ֵͼ��*/
	   public static void printBufferedImage(double [][] EmbededMatrix,String NewImaName) {
			int height = EmbededMatrix.length;
			int width = EmbededMatrix[0].length;
			/*System.out.println("ͼ��"+NewImaName+"��ȣ�");
			System.out.println(width);
			System.out.println("ͼ��"+NewImaName+"�߶ȣ�");
			System.out.println(height);*/
			double [] outResult=matrixToArray(EmbededMatrix);//��ά����ת��Ϊһά����
			BufferedImage outImage=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);//����һ������ΪԤ����ͼ������֮һ�� BufferedImage
			WritableRaster outRaster=outImage.getRaster();//����WritableRaster��չ�� Raste���ṩ����д�빦�ܡ�
		    outRaster.setPixels(0, 0,width,height, outResult);//Ϊÿ������Ԫ�ذ���һ�������� double �������е����ؾ���������������
			Iterator <ImageWriter> iter1=ImageIO.getImageWritersByFormatName("bmp");
			ImageWriter aWriter=iter1.next();
			try{
				  
				  //String imagePath = "E:\\labImg\\Img\\";
				  File outFile=new File(ImagePath + NewImaName + ".bmp");
				  ImageOutputStream oios=ImageIO.createImageOutputStream(outFile);
				  aWriter.setOutput(oios);
				  aWriter.write(new IIOImage(outImage,null,null));
				  System.out.println("ͼ��"+NewImaName+"������!");
			   }catch(IOException e){
				e.getStackTrace();
		       }
			
		 }
	   
	   public static double [][] arrayToMatrix(double [] m,int width ,int height ){
			double [][] result=new double [height][width];
			for(int i=0;i<height;i++){
				 for(int j=0;j<width;j++){
					 {
						 result[i][j]=m[i*width+j];
					 }
				 }
			}
					
			return result;
		}
	   
	   public static double [][] arrayToMatrixCol(double [] m,int width ,int height ){
			double [][] result=new double [width][height];
			for(int i=0;i<width;i++){
				 for(int j=0;j<height;j++){
					 {
						 result[i][j]=m[j*width+i];
					 }
				 }
			}
			
			
			return result;
		}
	   	   
	   public static double[] matrixToArray(double[][] EmbededMatrix){
		int p=EmbededMatrix.length*EmbededMatrix[0].length;
		double [] result=new double [p];
		for(int i=0;i<EmbededMatrix.length;i++){
			for(int j=0;j<EmbededMatrix[i].length;j++){
				
				{
				    result[i*EmbededMatrix[i].length+j]=EmbededMatrix[i][j];
				}
				
			}
		}
		return result;
	}
	   
	   public static double[] matrixToArrayCol(double[][] EmbededMatrix){//������˳��
			int p=EmbededMatrix.length*EmbededMatrix[0].length;
			double [] result=new double [p];
			for(int i=0;i<EmbededMatrix.length;i++){
				for(int j=0;j<EmbededMatrix[i].length;j++){
					
					{
					    result[i+j*EmbededMatrix.length]=EmbededMatrix[i][j];
					}
					
				}
			}
			return result;
		}

		public static int getWidth(String imageName){
			
			 File file = new File(ImagePath + imageName);
			 
			BufferedImage bufferedImage = null;

			try {

				bufferedImage = ImageIO.read(file);

			} catch (IOException e) {

				e.getStackTrace();

			}
			
			int width = bufferedImage.getWidth();		
			return width;
			
		}
		
		public static int getHeight(String imageName){
			
			File file = new File(ImagePath + imageName);
			
			BufferedImage bufferedImage = null;

			try {

				bufferedImage = ImageIO.read(file);

			} catch (IOException e) {

				e.getStackTrace();

			}
			
			int height = bufferedImage.getHeight();
			return height;
			
		}

		public static int getBufferedImageType(String imageName){
			
			File file = new File(ImagePath + imageName);
			
			BufferedImage bufferedImage = null;

			try {

				bufferedImage = ImageIO.read(file);

			} catch (IOException e) {

				e.printStackTrace();

			}
			
			return bufferedImage.getType();
			
		 }
		

	}
