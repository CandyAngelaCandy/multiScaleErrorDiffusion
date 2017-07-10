package multiScaleErrorDiffusion;

import java.awt.Point;
import java.math.BigDecimal;

import ImgOperation.ImageOperation;
import publicClass.publicClass;

/*1.��ͼ���²����ֽ�Ϊ�������������Ϊһ������
 *2.������ײ㣬��Ѱ�ҿ������ֵ�������ɱ�Ǿ��󣬽���λ�ñ��Ϊtrue�����ɲв���󣬽������ɢ��
 *�в������Χ���أ�������λ�ñ��Ϊ0������������ײ��Ǹ�����ֵ�Ƿ�С��0.5����ֹת���� */
public class MED extends publicClass {

	public static int bSize = 2;// ��Ĵ�С
	public static int PyramidCount = (int) (Math.log(h * w) / Math.log(2 * 2));// �������㼶,����ֵΪ4����ʵ��ԭʼͼ��
	public static int[] sideLen = new int[PyramidCount + 1];
	
			
	public MED(){//���캯������ʼ��ÿ�������ͼ��ı߳�
		
		// �������鱣��ÿ��ͼ��߳�	
		sideLen[0] = h;//��0��Ϊ16����PyramidCount��Ϊ1
	    for (int c = 1; c < PyramidCount + 1; c++) {
			// �����i�������ͼ��Ĵ�С
			int ImgWidth = (int) Math.sqrt(h * w / (Math.pow((2 * 2), (c))));
			sideLen[c] = ImgWidth;// �����c��������ı߳�����0��߳�Ϊ16����4��߳�Ϊ1.
		}
	}
	
	//�����
	public static void main(String[] args) {
		//imgPyramid("lena.bmp");
		MED med = new MED();
		//med.imgPyramid("lena140.bmp");
		//med.imgPyramid("lena16.bmp");
		med.imgPyramid("lena.bmp");
		/*for(int i=0;i<sideLen.length;i++){
			System.out.println(sideLen[i]);
		}
		System.out.println(sideLen);*/
		
	}

	// ͼ�����������
	public  void imgPyramid(String imgName) {

		double[][] covImg = ImageOperation.readImg(imgName);
		double[][] markImg = new double[h][w];// ���ɱ��ͼ��

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				covImg[i][j] = toFixPointCount(covImg[i][j] / 255.0, 4);// �������ع�һ��������4λС��
				markImg[i][j] = 0;
				//System.out.print(covImg[i][j] + " ");
			}
			//System.out.println();
		}

		double[][][] pyramidImg = GeImgPyramid(covImg, true);// ����ÿ�������,false��ʾ����ΪС������

		

		// ��������ͼ��ΪX��������E=errorMax-B�Ͷ�ֵ������BinMax��BinMax��ʼ��Ϊȫ0����
		double[][] errorMax = new double[h][w];// ������
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				errorMax[i][j] = covImg[i][j];// ��ʼ����������һ��
				//System.out.print(errorMax[i][j] + " ");
			}
			//System.out.println();
		}

		double[][] BinMax = new double[h][w];// 2ֵ������
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				BinMax[i][j] = 0;// ��ʼ����������һ��
				//System.out.print(BinMax[i][j] + " ");
			}
			//System.out.println();
		}

		// ���������ع�һ��������4λС�����ڴ˻������ҵ������������ֵ���õ���λ�ã���Ӧ��ֵ�����λ��Ϊ1��
		// �ؼ��Ǹ��������󣬼����λ�õ�����ɢ����Χ���أ�����������������
		// �ؼ�һ�㣺��ǲ��ҹ������ء�

		// !!���ɱ�ǽ���������ǵĹ��򣺳�ʼ��Ϊ0�����ʹ�����Ϊ1��������Ҫȷ�����������һ��λ��
		double[][][] MarkPyraImg = GeImgPyramid(markImg, true);
		
				
		//���������ԭʼ������ͼ��
		//printPramidImg(pyramidImg);
		
		
		double lastLayerFlagVal = pyramidImg[PyramidCount][0][0];
		System.out.println(lastLayerFlagVal);
		int count = 1;
		
		//count<=20  138835
		//lastLayerFlagVal >= 128//256��һ�룬�൱�ڹ�һ�����0.5;  lastLayerFlagVal >= 0.5
		
		while(count <= 138835){
			//System.out.println("��"+count+"�β��ҿ�ʼ");
			lastLayerFlagVal =searchMaxPix(errorMax, MarkPyraImg,covImg,BinMax);//û����һ�Σ�sum++;
			count++;
			//System.out.println();
		}
		
		//printBinImg(BinMax);//��ӡ��2ֵ����
		//printBinImg(errorMax,h,w);		
		ImageOperation.printBufferedImage(BinMax,"multiScaleHalfImg");
		System.out.println("�������н���");
	}

	

	// ��������תΪ4λС��
	public static double toFixPointCount(double f, int pointCount) {
		BigDecimal b = new BigDecimal(f);
		double f1 = b.setScale(pointCount, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}

	// �������������ֵ�������,�βΣ����ͼ�񣬱�Ǿ���ԭͼ�������
	//������ͼ����������ж���������ֵ�Ƿ�С��0.5
	public static double searchMaxPix(double[][] errorMax, double[][][] MarkPyraImg,double[][] covImg,double[][] BinMax) {
		// �ؼ����⣺��α������,��α�֤��Ҫ�ظ����ʣ�

		// ����Ѱ��δ���ʵ㣬�������ڵĽ��������������ݸ��������������ܣ�Ѱ�ҵ���ײ��Ǹ����أ�����Ǿ���
		// ���⣺ֱ����ԭ�����ϸ��£������ɢ��������Ϊ0����Ǿ����ǵ����ģ�

		// ���ݷ��ʵ�����������λ����һ�㡣�𽥼�ÿ����������������ڵ���0�����ڸò㣬��������һ�㣬��С��0�����ڸ�
		// �㰴˳��Ѱ�ҡ�
	
		int layer = -1;
			
		//˼�룺ֻ���Ǳ�Ǿ���-1�ĸ����Ƿ���ڱ߳���2������Ӧ���ڵڼ������
		for (int i = PyramidCount; i > -1; i--) {// ��16*16Ϊ����i=5;		
			boolean f = calSumInMarkmatri(i,MarkPyraImg);
			if(!f){
				layer = i;
				break;
			}
		}
						
		//System.out.println("Ӧ�ڵ�" + layer + "�����");

		//������ͼ�������
		double lastLayerFlagVal = searchBigPix(layer,errorMax,MarkPyraImg,BinMax);
		return 	lastLayerFlagVal;
		 
	}
		 
	//���Ǿ�����ֵΪ-1�ĺ�
	public static boolean calSumInMarkmatri(int layer,double [][][] markMatrix){
		int sum = 0;
		for (int i = 0; i < sideLen[layer]; i++) {
			for (int j = 0; j < sideLen[layer]; j++) {
				sum+=markMatrix[layer][i][j];
			}
		}
		if(Math.abs(sum) == Math.pow(sideLen[layer], 2)){
			return true;
		}
		return false;
	}
	
    		 		 	
	//һһһһһ ���Ĵ���
	//��������:���ҵ�0��ϴ�����ֵ;�������������ڲ�������Ǿ���ԭʼ����ͼ��
	public static double searchBigPix(int layer,double[][] errorMax, 
			double[][][] MarkPyraImg,double[][] BinMax){

		//�������ڲ㣬�𽥲��ҵ���0�����ڵ�����ֵ
		
		//ȡ����layer�������������δ���ʵ�����
		double[][][] pyramidImg = GeImgPyramid(errorMax, true);//true��ʾ����Ϊ��������
		
		//������Ǿ��󣬲���layer��δ���ʹ�������
		
		Point p =new Point(-1,-1);
				
		//����λ��(i,j),��һ������ͼ�������ֵ��
		Point point = selectMaxPixInAllUnmark(layer,MarkPyraImg,pyramidImg);
		int a=point.x,b=point.y;
					
										
		//System.out.println("layer:"+layer);
		MarkPyraImg[layer][a][b] =-1;//���ʹ�Ԫ�ر��
					
					
					
		p = new Point(a,b);
		//System.out.print("��"+layer+"��ѡ�е����꣺"+p.x+" "+p.y);
		//System.out.println("ѡȡ��"+layer+"��������������ֵ��"+pyramidImg[layer][a][b]);
						
		for(int m=layer -1;m>-1;m--){
		  double temp = 0;
							  							  
		  Point lastLayP =new Point(p.x,p.y);//��������������ǣ�����Ӱ��
							  
		  for (int k = 0; k < bSize; k++) { 
			for (int l = 0; l < bSize; l++) {
				int x=lastLayP.x * bSize + k, y=lastLayP.y * bSize + l; 
				double temp2	= pyramidImg[m][x][y];
									  
				//System.out.println("("+x+","+y+")="+pyramidImg[m][x][y]);
									  
				if(temp2>temp){//��2*2��С�Ŀ��� ��Ѱ�����ֵ
					temp=temp2; p.x = x; p.y = y; 
				}
									  // System.out.println();								  
			}
		 }
		//System.out.print("��"+m+"��ѡ�е����꣺"+p.x+" "+p.y);
							  
		MarkPyraImg[m][p.x][p.y] =-1;//���ʹ�Ԫ�ر��
							  
		//System.out.println("ѡȡ��"+m+"��������������ֵ��"+temp); 
		//System.out.println(); 
							  
		if(m == 0){//����ֵ�����λ����Ϊ1
			//System.out.println("ѡ�е�λ�ã�("+p.x+","+p.y+")");
			BinMax[p.x][p.y] = 1;//δ��һ������дΪ255
			
			//��������ɢ������errorMax����
			//ErrDifToOtherPixel(BinMax[p.x][p.y],errorMax,p.x,p.y);
			errorMax[p.x][p.y] = 0;//���������ɢ����Χ���أ�(�Ȳ���������ɢ)������λ��ֵ��Ϊ0���þ�������˥����			
		}												 						  						  
	 }
						
					
		
				
	//�������һ������ֵ
	  double[][][] UpdatePyramidImg = GeImgPyramid(errorMax, true);//true��ʾ����Ϊ��������
		
		
		//printPramidImg(MarkPyraImg);//��ӡ����Ǿ���
		
		//printPramidImg(UpdatePyramidImg);
		
		//System.out.println(UpdatePyramidImg[PyramidCount][0][0]);
		
		return UpdatePyramidImg[PyramidCount][0][0];
							
   }
		
	//������δ��ǵ�����ֵ��ѡȡ�������ֵ
	public static Point selectMaxPixInAllUnmark(int layer,double [][][] MarkPyraImg,double[][][] pyramidImg){
		//ʹ�����������㷨���ҳ�һά���������ֵ		

		//����������������ֵ�����Ż�
		//printBinImg(MarkPyraImg[layer], sideLen[layer], sideLen[layer]);
		
		Point p = new Point(-1,-1);
		double temp1 = -1;
		for(int i=0;i<sideLen[layer];i++)
			for(int j=0;j<sideLen[layer];j++){
				double tempPix = MarkPyraImg[layer][i][j];
				if(tempPix == 0){
					double temp2 = pyramidImg[layer][i][j];
					if(temp2>temp1){
						temp1=temp2; 
						p.x = i; 
						p.y = j; 
					}
				}			
		}
		
		return p;
		
	}
			
	//��ӡ����������ͼ��
	public static void printPramidImg(double[][][] pyramidImg){
				
		for(int i=0;i<PyramidCount + 1;i++){
			System.out.println("��"+i+"���������");
			for(int m=0;m<sideLen[i];m++){
				for(int n=0;n<sideLen[i];n++){
					System.out.print(pyramidImg[i][m][n]+" ");
			    }
				System.out.println();
			}		
		}
		
		
	}
	
	public static void printBinImg(double[][] BinMax,int h,int w){	
		for(int m=0;m<h;m++){
			for(int n=0;n<w;n++){
				System.out.print(BinMax[m][n]+" ");
		    }
			System.out.println();
		}	
	}
	
	//���������������ͽ������������������ܣ��ж�����һ�����
	public static void locateSumInLayer(double sum,int layer){
		
	}
		
	// ͨ��һ����ά��������ͼ�������,����һ����ά���鱣�����ͼ��
	public static double[][][] GeImgPyramid(double[][] covImg, Boolean flag) {

			//System.out.println("������������" + PyramidCount);// �������Ĳ�������16*16Ϊ����PyramidCount=4������ԭʼͼ��
			// һ��5��

			// ��ά���鱣����������ͼ��
			double[][][] pyramidImg = new double[PyramidCount + 1][h][w];

			//System.out.println("��" + 0 + "��");
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					pyramidImg[0][i][j] = covImg[i][j];// ��ʼ����������һ��
					//System.out.print(covImg[i][j] + " ");
				}
				//System.out.println();
			}

			
			for (int c = 1; c < PyramidCount + 1; c++) {
				
				// һ�����鱣��ÿһ���Ӧ��ͼ��

				// �ҵ�������С��Ķ�Ӧ��ϵ����ôͨ��һ�����ؿ��ҵ���Ӧ�����ؿ�

				// ����ԭʼͼ�����һ������� ��������������0��ʼ����һ��Ϊԭʼͼ�񣬵�PyramidCount=4��Ϊһ������
				//System.out.println("��" + c + "��");
				for (int i = 0; i < sideLen[c]; i++) {
					for (int j = 0; j < sideLen[c]; j++) {
						double temp = 0;
						for (int k = 0; k < bSize; k++) {
							for (int l = 0; l < bSize; l++) {
								
								 /* System.out.println(pyramidImg[c-1][i * bSize +
								  k][j * bSize + l]);*/
								 /*if(i == 4&&j==4){
									 System.out.println(pyramidImg[c-1][i * bSize +
									      							  k][j * bSize + l]);
								 }*/
								temp += pyramidImg[c - 1][i * bSize + k][j * bSize + l];
							}
						}

						if (flag) {
							//pyramidImg[c][i][j] = Math.round(temp / 4.0);
							// pyramidImg[c][i][j] = temp;
							//pyramidImg[c][i][j] = temp;
							pyramidImg[c][i][j] = toFixPointCount(temp / 4.0,4);
						} else {
							//pyramidImg[c][i][j] = temp;
							pyramidImg[c][i][j] = toFixPointCount(temp / 4.0, 4);// ������ֵΪ����ʱ������ȡ������ΪС��ʱ��ȡ��λС����
							// ����һ������flag��ʶΪ����������С��
						}
						/* System.out.println(temp); */
						//System.out.print(pyramidImg[c][i][j] + " ");
					}
					//System.out.println();
				}
				//System.out.println();
				// ͨ���ײ�Ľ��������Ҷ�Ӧ��

			}
			return pyramidImg;
		}
	
	//�������ɢ����������
	public  static double [][] ErrDifToOtherPixel(double BinaryValue,double [][]uniformImg,int i,int j)
	{
		/*int h=uniformImg.length;
		int w=uniformImg[0].length;*/
		
		double ErrorValue=uniformImg[i][j]-BinaryValue;
		int [][] v = {{0,0,7},{3,5,1}};
		
		/*����ߡ����ұߺ����±�����ʹ�ò����˲���������ʹ�������˲���*/
		  if(i!=w-1 && j==0 )//�����ʹ�ò����˲���
		  {
			  /*�˲������£�P  7
			            5  1
			   */
			  uniformImg[i][j+1]+=(ErrorValue*7)/13;
			  uniformImg[i+1][j]+=(ErrorValue*5)/13;
			  uniformImg[i+1][j+1]+=(ErrorValue*1)/13;
			  
			  /*��ֹ���*/
			  /*if(uniformImg[i][j+1]>1)  uniformImg[i][j+1]=1;
	     	  if(uniformImg[i][j+1]<0)  uniformImg[i][j+1]=0;
			  
			   for(int k=1;k<2;k++)
				  for(int l=0;l<2;l++)
		       {
			      if(uniformImg[i+k][j+l]>1)  uniformImg[i+k][j+l]=1;
	     	      if(uniformImg[i+k][j+l]<0)  uniformImg[i+k][j+l]=0;
			   }*/
			  
			  
		  }
		  else if(i!=w-1&&j==h-1)//���ұ�ʹ�ò����˲���;ʹ�ö�̬��ֵ
		       {
			      /*�˲������£�0  P
	                         3  5
	              */
			     uniformImg[i+1][j]+=(ErrorValue*5)/8;
			     uniformImg[i+1][j-1]+=(ErrorValue*3)/8;
			     
			     /*��ֹ���*/
				  /* for(int k=1;k<2;k++)
					  for(int l=-1;l<1;l++)
			     {
				    if(uniformImg[i+k][j+l]>1)  uniformImg[i+k][j+l]=1;
		     	    if(uniformImg[i+k][j+l]<0)  uniformImg[i+k][j+l]=0;
				 }*/
				   
		       } 
		     
	          else  if(i==w-1&&j!=h-1)//���һ��Ԫ�أ��������һ��Ԫ�أ�ʹ�ò����˲���
	                {
	        	      uniformImg[i][j+1]+=(ErrorValue*7)/7;
	        	      
	        	      /*��ֹ���*/
	        		 /* if(uniformImg[i][j+1]>1)  uniformImg[i][j+1]=1;
	             	  if(uniformImg[i][j+1]<0)  uniformImg[i][j+1]=0;*/
	                }
	               else  if(i==w-1&&j==h-1)//��������һ�У����һ��Ԫ�أ����˲�������
	               {
	            	   
	               }
		            else//һ��Ԫ��ʹ�������˲�����
		           {		    
						     /*�˲�������0  P  7
						      *       3  5  1
	                          */
		            	 for(int k = 0;k<2;k++)// ����k<2
			                 for(int l =-1;l<2;l++){			                       
			                       uniformImg[i+k][j+l]+= ErrorValue*v[k][l+1]/16;
			                      /* if(uniformImg[i+k][j+l]>1)  uniformImg[i+k][j+l]=1;
			                       if(uniformImg[i+k][j+l]<0)  uniformImg[i+k][j+l]=0;*/
			                         
			                  }
		           }
		  return uniformImg;
	 }
	
	
}