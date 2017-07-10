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
	   
	   //只能读8位的灰度图像，输出数组为
	   public static double[][] readImg(String ImageName){
		   
			BufferedImage oImage=null;
			File file = new File(ImagePath+ImageName);//所读图像的路径
			try {
				oImage=ImageIO.read(file);
			} catch (IOException e) {
				e.getStackTrace();
			}
			
			WritableRaster oRaster=oImage.getRaster();
			int height=oRaster.getHeight();
			int width=oRaster.getWidth();
			
			
			double[] pixels=new double[height*width];
			oRaster.getPixels(0, 0, width, height, pixels);//以 double 数组形式返回指定像素的样本。  getPixels()函数以行读取
			/*double[][] ImgMatrix=arrayToMatrixCol(pixels, width, height);//一维数组转换成二维数组*/	
			double[][] ImgMatrix=arrayToMatrix(pixels, width, height);
			return ImgMatrix;
		}
	   
	   
	   /*将二维数组写成位深度为8位灰度图像,以行输入，以行输出*/
	   public static void writeImg(double[][] arry,String outputName,int bufferedImageType){
		  
		   int height=arry.length;
		   int width=arry[0].length;
		  /*System.out.println("width="+width);
		   System.out.println("height="+height);*/
		   
		   /*double [] pixel=matrixToArrayCol(arry);*/
		   
		   double [] pixel=matrixToArray(arry);
		   //自带函数只能以列输出
		   BufferedImage outImage=new BufferedImage(width,height, bufferedImageType);// 构造一个类型为预定义图像类型之一的 BufferedImage
		   WritableRaster outRaster=outImage.getRaster();//此类WritableRaster扩展了 Raste 以提供像素写入功能。
		   outRaster.setPixels(0, 0,width,height,pixel);//为每个数组元素包含一个样本的 double 型数组中的像素矩形设置所有样本
		  
			File f = new File(ImagePath + outputName);
			try{
				
				ImageIO.write(outImage, "bmp", f);
			   }catch(IOException e)
			   {
				   e.getStackTrace();
			   }
			
			int type = outImage.getType(); 
		    /*System.out.println("灰度图像："+outputName+"的类型："+type);*/
	   }
	   

	  //只能读24位的灰度图像
	   public static double [][] getGray(String ImageName) {

			BufferedImage bufferedImage = null;
			File file = new File(ImagePath+ImageName);//所读图像的路径
			
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
	   
	   
	  
	   /*将二维数组写成位深度为24位灰度图像*/
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
		    System.out.println("灰度图像："+outputName+"的类型："+type);
			return bufferedImage;
		
	}
	   
	   /*将二维数组写成二值图像*/
	   public static void printBufferedImage(double [][] EmbededMatrix,String NewImaName) {
			int height = EmbededMatrix.length;
			int width = EmbededMatrix[0].length;
			/*System.out.println("图像"+NewImaName+"宽度：");
			System.out.println(width);
			System.out.println("图像"+NewImaName+"高度：");
			System.out.println(height);*/
			double [] outResult=matrixToArray(EmbededMatrix);//二维数组转换为一维数组
			BufferedImage outImage=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);//构造一个类型为预定义图像类型之一的 BufferedImage
			WritableRaster outRaster=outImage.getRaster();//此类WritableRaster扩展了 Raste以提供像素写入功能。
		    outRaster.setPixels(0, 0,width,height, outResult);//为每个数组元素包含一个样本的 double 型数组中的像素矩形设置所有样本
			Iterator <ImageWriter> iter1=ImageIO.getImageWritersByFormatName("bmp");
			ImageWriter aWriter=iter1.next();
			try{
				  
				  //String imagePath = "E:\\labImg\\Img\\";
				  File outFile=new File(ImagePath + NewImaName + ".bmp");
				  ImageOutputStream oios=ImageIO.createImageOutputStream(outFile);
				  aWriter.setOutput(oios);
				  aWriter.write(new IIOImage(outImage,null,null));
				  System.out.println("图像"+NewImaName+"已生成!");
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
	   
	   public static double[] matrixToArrayCol(double[][] EmbededMatrix){//列优先顺序
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
