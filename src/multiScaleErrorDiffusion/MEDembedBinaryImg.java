package multiScaleErrorDiffusion;

import ImgOperation.ImageOperation;
import publicClass.publicClass;

public class MEDembedBinaryImg extends publicClass {
   public static void main(String[] args) {
	   embedBinImgBaseMED();
   }
   
   //3张全黑图像，根据3张有意义图中像素值最大的位置，调整相应位置，生成误差扩散，基于误差图像，根据MED方法调整相应位置，接近
   //0.5的临界值。先不加三个临界条件。
   
   public static void embedBinImgBaseMED(){
	   int l = 3;
	   
	   //生成一个数组，保存图像名，最后一张为秘密2值图像	 
	   String[] imgNameArr = {"lena~8.bmp","baboon~8.bmp","barbara~8.bmp","BinaryLena~8.bmp"};
	   
	   double [][][] covImg = new double [l][h][w];
	   double [][][] binImg = new double [l][h][w];
	   double [][][] MarkImg = new double [l][h][w];//标记矩阵
	   double [][] secretImg = ImageOperation.readImg(imgNameArr[l]);
 	   
	   for(int m=0;m<l;m++){
		   covImg[m] = ImageOperation.readImg(imgNameArr[m]);
		   for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					binImg[m][i][j] = 0;
					MarkImg[m][i][j] = 0;
				}
			}
		   System.out.println("第"+m+"张载体图像");
		   MED.printBinImg(covImg[m],h,w);
		   System.out.println("第"+m+"张载体图像初始化的2值图像");
		   MED.printBinImg(binImg[m],h,w);
		   System.out.println();
	   }
	   
	   System.out.println("秘密图像如下：");
	   MED.printBinImg(secretImg,h,w);
	   
	   for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if(secretImg[i][j]==1){
					double maxPix = -1;
				    int imgCount = -1;
					for(int m=0;m<l;m++){
						if(covImg[m][i][j]>maxPix){
							maxPix = covImg[m][i][j];
							imgCount = m;
						}
					}
					binImg[imgCount][i][j] = 1;
					MarkImg[imgCount][i][j] = -1;
					//生成误差图像
					MED.ErrDifToOtherPixel(1, covImg[imgCount], MarkImg ,i, j);
					
					covImg[imgCount][i][j] = 0;//它为误差图像
					
					
					
				}//if结束			
			}
		}//for循环结束
	   
	   System.out.println("嵌入秘密图像的2值载体图图如下：");
	   for(int m=0;m<l;m++){
		   System.out.println("第"+m+"张嵌密2值图像");
		   MED.printBinImg(binImg[m],h,w);
		   System.out.println();
		   
		   System.out.println("第"+m+"张标记图像");
		   MED.printBinImg(MarkImg[m],h,w);
		   
		   System.out.println("第"+m+"张误差图像");
		   MED.printBinImg(covImg[m],h,w);		   		   		   
	   }
	   
	   
	   	  	   
	   //引入三张有意义灰度图像，大小为128
	   
	   //函数：参数为误差图像，标记图像，2值图像，进行MED调整
	   
	   
	   
	   
   }
}
