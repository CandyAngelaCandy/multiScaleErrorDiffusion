package multiScaleErrorDiffusion;

import ImgOperation.ImageOperation;
import publicClass.publicClass;

public class MEDembedBinaryImg extends publicClass {
   public static void main(String[] args) {
	   embedBinImgBaseMED();
   }
   
   //3��ȫ��ͼ�񣬸���3��������ͼ������ֵ����λ�ã�������Ӧλ�ã����������ɢ���������ͼ�񣬸���MED����������Ӧλ�ã��ӽ�
   //0.5���ٽ�ֵ���Ȳ��������ٽ�������
   
   public static void embedBinImgBaseMED(){
	   int l = 3;
	   
	   //����һ�����飬����ͼ���������һ��Ϊ����2ֵͼ��	 
	   String[] imgNameArr = {"lena~8.bmp","baboon~8.bmp","barbara~8.bmp","BinaryLena~8.bmp"};
	   
	   double [][][] covImg = new double [l][h][w];
	   double [][][] binImg = new double [l][h][w];
	   double [][][] MarkImg = new double [l][h][w];//��Ǿ���
	   double [][] secretImg = ImageOperation.readImg(imgNameArr[l]);
 	   
	   for(int m=0;m<l;m++){
		   covImg[m] = ImageOperation.readImg(imgNameArr[m]);
		   for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					binImg[m][i][j] = 0;
					MarkImg[m][i][j] = 0;
				}
			}
		   System.out.println("��"+m+"������ͼ��");
		   MED.printBinImg(covImg[m],h,w);
		   System.out.println("��"+m+"������ͼ���ʼ����2ֵͼ��");
		   MED.printBinImg(binImg[m],h,w);
		   System.out.println();
	   }
	   
	   System.out.println("����ͼ�����£�");
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
					//�������ͼ��
					MED.ErrDifToOtherPixel(1, covImg[imgCount], MarkImg ,i, j);
					
					covImg[imgCount][i][j] = 0;//��Ϊ���ͼ��
					
					
					
				}//if����			
			}
		}//forѭ������
	   
	   System.out.println("Ƕ������ͼ���2ֵ����ͼͼ���£�");
	   for(int m=0;m<l;m++){
		   System.out.println("��"+m+"��Ƕ��2ֵͼ��");
		   MED.printBinImg(binImg[m],h,w);
		   System.out.println();
		   
		   System.out.println("��"+m+"�ű��ͼ��");
		   MED.printBinImg(MarkImg[m],h,w);
		   
		   System.out.println("��"+m+"�����ͼ��");
		   MED.printBinImg(covImg[m],h,w);		   		   		   
	   }
	   
	   
	   	  	   
	   //��������������Ҷ�ͼ�񣬴�СΪ128
	   
	   //����������Ϊ���ͼ�񣬱��ͼ��2ֵͼ�񣬽���MED����
	   
	   
	   
	   
   }
}
