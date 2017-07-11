package multiScaleErrorDiffusion;

import java.awt.Point;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import ImgOperation.ImageOperation;
import publicClass.publicClass;

/*1.��ͼ���²����ֽ�Ϊ�������������Ϊһ������
 *2.������ײ㣬��Ѱ�ҿ������ֵ�������ɱ�Ǿ��󣬽���λ�ñ��Ϊtrue�����ɲв���󣬽������ɢ��
 *�в������Χ���أ�������λ�ñ��Ϊ0������������ײ��Ǹ�����ֵ�Ƿ�С��0.5����ֹת���� */
public class MED extends publicClass {

	public static int bSize = 2;// ��Ĵ�С
	public static int PyramidCount = (int) (Math.log(h * w) / Math.log(2 * 2));// �������㼶,����ֵΪ4����ʵ��ԭʼͼ��
	public static int[] sideLen = new int[PyramidCount + 1];
	// public static double[][][] pyramidImg;// ����ÿ�������,false��ʾ����ΪС������
	// public static double[][] covImg;
	/*
	 * public static HashMap[] map=new HashMap[PyramidCount + 1]; public static
	 * ArrayList[] list=new ArrayList[PyramidCount + 1];
	 */

	public MED() {// ���캯������ʼ��ÿ�������ͼ��ı߳�

		// �������鱣��ÿ��ͼ��߳�
		sideLen[0] = h;// ��0��Ϊ16����PyramidCount��Ϊ1
		for (int c = 1; c < PyramidCount + 1; c++) {
			// �����i�������ͼ��Ĵ�С
			int ImgWidth = (int) Math.sqrt(h * w / (Math.pow((2 * 2), (c))));
			sideLen[c] = ImgWidth;// �����c��������ı߳�����0��߳�Ϊ16����4��߳�Ϊ1.
		}

		// ����ÿһ��������������кõĴ�С��list�������кõ����飬hashMap����ԭʼ����

		// ������ͼ�������
		/*
		 * covImg = ImageOperation.readImg(imgName); pyramidImg =
		 * GeImgPyramid(covImg, true); for(int c = 0; c < PyramidCount + 1;
		 * c++){ double[] b = ImageOperation.matrixToArray(pyramidImg[0]); int
		 * oneDiaArrLen = (int) Math.pow(sideLen[c], 2);
		 * 
		 * for(int i=0;i<oneDiaArrLen;i++) { map[c].put(b[i],i); //��ֵ���±�����c��Map
		 * }
		 * 
		 * //���� Arrays.sort(b); //��������
		 * 
		 * for(int i=0;i<oneDiaArrLen;i++) { list[c].add(b[i]); }
		 * 
		 * //��ʱlist[c]Ϊ�������е�Ԫ�� Collections.reverse(list[c]); //��������,��Ϊ���� }
		 */

	}

	// �����
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();    //��ȡ��ʼʱ��

		// imgPyramid("lena.bmp");
				MED med = new MED();
				// med.imgPyramid("lena140.bmp");
				// med.imgPyramid("lena16.bmp");
				med.imgPyramid("lena~128.bmp");
				/*
				 * for(int i=0;i<sideLen.length;i++){ System.out.println(sideLen[i]); }
				 * System.out.println(sideLen);
		*/

		long endTime = System.currentTimeMillis();    //��ȡ����ʱ��

		System.out.println("��������ʱ�䣺" + (endTime - startTime)/1000.0 + "s");    //�����������ʱ��
		
		
		

	}

	// ͼ�����������
	public void imgPyramid(String imgName) {

		double[][] covImg = ImageOperation.readImg(imgName);
		double[][] markImg = new double[h][w];// ���ɱ��ͼ��

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				covImg[i][j] = toFixPointCount(covImg[i][j] / 255.0, 4);// �������ع�һ��������4λС��
				markImg[i][j] = 0;
				// System.out.print(covImg[i][j] + " ");
			}
			// System.out.println();
		}

		double[][][] pyramidImg = GeImgPyramid(covImg, true);

		// ��������ͼ��ΪX��������E=errorMax-B�Ͷ�ֵ������BinMax��BinMax��ʼ��Ϊȫ0����
		double[][] errorMax = new double[h][w];// ������
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				errorMax[i][j] = covImg[i][j];// ��ʼ����������һ��
				// System.out.print(errorMax[i][j] + " ");
			}
			// System.out.println();
		}

		double[][] BinMax = new double[h][w];// 2ֵ������
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				BinMax[i][j] = 0;// ��ʼ����������һ��
				// System.out.print(BinMax[i][j] + " ");
			}
			// System.out.println();
		}

		// ���������ع�һ��������4λС�����ڴ˻������ҵ������������ֵ���õ���λ�ã���Ӧ��ֵ�����λ��Ϊ1��
		// �ؼ��Ǹ��������󣬼����λ�õ�����ɢ����Χ���أ�����������������
		// �ؼ�һ�㣺��ǲ��ҹ������ء�

		// !!���ɱ�ǽ���������ǵĹ��򣺳�ʼ��Ϊ0�����ʹ�����Ϊ1��������Ҫȷ�����������һ��λ��
		double[][][] MarkPyraImg = GeImgPyramid(markImg, true);

		// ���������ԭʼ������ͼ��
		//printPramidImg(pyramidImg);

		double lastLayerFlagVal = pyramidImg[PyramidCount][0][0];
		System.out.println(lastLayerFlagVal);
		int count = 1;

		// count<=20 138835
		// lastLayerFlagVal >= 128//256��һ�룬�൱�ڹ�һ�����0.5; lastLayerFlagVal >= 0.5

		while (lastLayerFlagVal >= 0.5) {
			/*System.out.println();
			System.out.println();*/
			//System.out.println("��" + count + "�β��ҿ�ʼ");
			lastLayerFlagVal = searchMaxPix(errorMax, MarkPyraImg, covImg, BinMax);// û����һ�Σ�sum++;
			count++;
			// System.out.println();
		}

		System.out.println("�ܹ����ң�"+count+"��");
		//printBinImg(BinMax, h, w);// ��ӡ��2ֵ����
		//printBinImg(errorMax, h, w);
		ImageOperation.printBufferedImage(BinMax, "multiScaleHalfImg");
		System.out.println("�������н���");
	}

	// ��������תΪ4λС��
	public static double toFixPointCount(double f, int pointCount) {
		BigDecimal b = new BigDecimal(f);
		double f1 = b.setScale(pointCount, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}

	// �������������ֵ�������,�βΣ����ͼ�񣬱�Ǿ���ԭͼ�������
	// ������ͼ����������ж���������ֵ�Ƿ�С��0.5
	public static double searchMaxPix(double[][] errorMax, double[][][] MarkPyraImg, double[][] covImg,
			double[][] BinMax) {
		// �ؼ����⣺��α������,��α�֤��Ҫ�ظ����ʣ�

		// ����Ѱ��δ���ʵ㣬�������ڵĽ��������������ݸ��������������ܣ�Ѱ�ҵ���ײ��Ǹ����أ�����Ǿ���
		// ���⣺ֱ����ԭ�����ϸ��£������ɢ��������Ϊ0����Ǿ����ǵ����ģ�

		// ���ݷ��ʵ�����������λ����һ�㡣�𽥼�ÿ����������������ڵ���0�����ڸò㣬��������һ�㣬��С��0�����ڸ�
		// �㰴˳��Ѱ�ҡ�

		int layer = -1;

		// ˼�룺ֻ���Ǳ�Ǿ���-1�ĸ����Ƿ���ڱ߳���2������Ӧ���ڵڼ������
		for (int i = PyramidCount; i > -1; i--) {// ��16*16Ϊ����i=5;
			boolean f = calSumInMarkmatri(i, MarkPyraImg);
			if (!f) {
				layer = i;
				break;
			}
		}

		// System.out.println("Ӧ�ڵ�" + layer + "�����");

		// ������ͼ�������
		double lastLayerFlagVal = searchBigPix(layer, errorMax, MarkPyraImg, BinMax);
		return lastLayerFlagVal;

	}

	// ���Ǿ�����ֵΪ-1�ĺ�
	public static boolean calSumInMarkmatri(int layer, double[][][] markMatrix) {
		int sum = 0;
		for (int i = 0; i < sideLen[layer]; i++) {
			for (int j = 0; j < sideLen[layer]; j++) {
				sum += markMatrix[layer][i][j];
			}
		}
		if (Math.abs(sum) == Math.pow(sideLen[layer], 2)) {
			return true;
		}
		return false;
	}

	// һһһһһ ���Ĵ���
	// ��������:���ҵ�0��ϴ�����ֵ;�������������ڲ�������Ǿ���ԭʼ����ͼ��
	public static double searchBigPix(int layer, double[][] errorMax, double[][][] MarkPyraImg, double[][] BinMax) {

		// �������ڲ㣬�𽥲��ҵ���0�����ڵ�����ֵ

		// ȡ����layer�������������δ���ʵ�����
		double[][][] pyramidImg = GeImgPyramid(errorMax, true);// true��ʾ����Ϊ��������

		// ������Ǿ��󣬲���layer��δ���ʹ�������

		Point p = new Point(-1, -1);

		// ����λ��(i,j),��һ������ͼ�������ֵ��
		Point point = selectMaxPixInAllUnmark(layer, MarkPyraImg, pyramidImg);
		int a = point.x, b = point.y;

		// System.out.println("layer:"+layer);
		MarkPyraImg[layer][a][b] = -1;// ���ʹ�Ԫ�ر��

		p = new Point(a, b);
		//System.out.print("��" + layer + "��ѡ�е����꣺" + p.x + " " + p.y);
		//System.out.println("ѡȡ��" + layer + "��������������ֵ��" + pyramidImg[layer][a][b]);
		int m = layer - 1;

		for (; m > -1; m--) {
			double temp = 0;

			Point lastLayP = new Point(p.x, p.y);// ��������������ǣ�����Ӱ��

			for (int k = 0; k < bSize; k++) {
				for (int l = 0; l < bSize; l++) {
					int x = lastLayP.x * bSize + k, y = lastLayP.y * bSize + l;
					double temp2 = pyramidImg[m][x][y];

					// System.out.println("("+x+","+y+")="+pyramidImg[m][x][y]);

					if (temp2 > temp) {// ��2*2��С�Ŀ��� ��Ѱ�����ֵ
						temp = temp2;
						p.x = x;
						p.y = y;
					}
					// System.out.println();
				}
			}
			
			MarkPyraImg[m][p.x][p.y] = -1;// ���ʹ�Ԫ�ر��
			//System.out.print("��" + m + "��ѡ�е����꣺" + p.x + " " + p.y);
			//System.out.println("ѡȡ��" + m + "��������������ֵ��" + pyramidImg[m][p.x][p.y]);
			
		} // mѭ������
		m = m + 1;

		//System.out.print("��" + m + "��ѡ�е����꣺" + p.x + " " + p.y);

		
		//System.out.println("m=" + m);
		//System.out.println("ѡȡ��" + m + "��������������ֵ��" + pyramidImg[m][p.x][p.y]);

		if (m == 0) {// ����ֵ�����λ����Ϊ1
			//System.out.println("ѡ�е�λ�ã�(" + p.x + "," + p.y + ")");
			BinMax[p.x][p.y] = 1;// δ��һ������дΪ255

			// ��������ɢ������errorMax����
			ErrDifToOtherPixel(BinMax[p.x][p.y], errorMax, MarkPyraImg ,p.x, p.y);
			errorMax[p.x][p.y] = 0;// ���������ɢ����Χ���أ�(�Ȳ���������ɢ)������λ��ֵ��Ϊ0���þ�������˥����
		}

		// �������һ������ֵ
		double[][][] UpdatePyramidImg = GeImgPyramid(errorMax, true);// true��ʾ����Ϊ��������

		//System.out.println("��Ǿ���");
		//printPramidImg(MarkPyraImg);// ��ӡ����Ǿ���

		//System.out.println("������ͼ��");
		//printPramidImg(UpdatePyramidImg);

		// System.out.println(UpdatePyramidImg[PyramidCount][0][0]);

		return UpdatePyramidImg[PyramidCount][0][0];

	}

	// ������δ��ǵ�����ֵ��ѡȡ�������ֵ
	public static Point selectMaxPixInAllUnmark(int layer, double[][][] MarkPyraImg, double[][][] pyramidImg) {
		// ʹ�����������㷨���ҳ�һά���������ֵ

		// ����������������ֵ�����Ż�
		// printBinImg(MarkPyraImg[layer], sideLen[layer], sideLen[layer]);

		Point p = new Point(-1, -1);
		double temp1 = -1;
		for (int i = 0; i < sideLen[layer]; i++)
			for (int j = 0; j < sideLen[layer]; j++) {
				double tempPix = MarkPyraImg[layer][i][j];
				if (tempPix == 0) {
					double temp2 = pyramidImg[layer][i][j];
					if (temp2 > temp1) {
						temp1 = temp2;
						p.x = i;
						p.y = j;
					}
				}
			}

		/*
		 * double maxPix = (double)list[layer].get(0); //����ԭʼ�±� int index =
		 * (int) map[layer].get(b[i]); Point point = new Point(index/3,index%3);
		 * System.out.println(b[i]+"("+point.x+","+point.y+")");
		 * list[layer].remove(0);
		 */

		return p;

	}

	// ��ӡ����������ͼ��
	public static void printPramidImg(double[][][] pyramidImg) {

		for (int i = 0; i < PyramidCount + 1; i++) {
			System.out.println("��" + i + "���������");
			for (int m = 0; m < sideLen[i]; m++) {
				for (int n = 0; n < sideLen[i]; n++) {
					System.out.print(pyramidImg[i][m][n] + " ");
				}
				System.out.println();
			}
		}

	}

	public static void printBinImg(double[][] BinMax, int h, int w) {
		for (int m = 0; m < h; m++) {
			for (int n = 0; n < w; n++) {
				System.out.print(BinMax[m][n] + " ");
			}
			System.out.println();
		}
	}

	// ���������������ͽ������������������ܣ��ж�����һ�����
	public static void locateSumInLayer(double sum, int layer) {

	}

	// ͨ��һ����ά��������ͼ�������,����һ����ά���鱣�����ͼ��
	public static double[][][] GeImgPyramid(double[][] covImg, Boolean flag) {

		// System.out.println("������������" + PyramidCount);//
		// �������Ĳ�������16*16Ϊ����PyramidCount=4������ԭʼͼ��
		// һ��5��

		// ��ά���鱣����������ͼ��
		double[][][] pyramidImg = new double[PyramidCount + 1][h][w];

		// System.out.println("��" + 0 + "��");
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				pyramidImg[0][i][j] = covImg[i][j];// ��ʼ����������һ��
				// System.out.print(covImg[i][j] + " ");
			}
			// System.out.println();
		}

		for (int c = 1; c < PyramidCount + 1; c++) {

			// һ�����鱣��ÿһ���Ӧ��ͼ��

			// �ҵ�������С��Ķ�Ӧ��ϵ����ôͨ��һ�����ؿ��ҵ���Ӧ�����ؿ�

			// ����ԭʼͼ�����һ������� ��������������0��ʼ����һ��Ϊԭʼͼ�񣬵�PyramidCount=4��Ϊһ������
			// System.out.println("��" + c + "��");
			for (int i = 0; i < sideLen[c]; i++) {
				for (int j = 0; j < sideLen[c]; j++) {
					double temp = 0;
					for (int k = 0; k < bSize; k++) {
						for (int l = 0; l < bSize; l++) {

							/*
							 * System.out.println(pyramidImg[c-1][i * bSize +
							 * k][j * bSize + l]);
							 */
							/*
							 * if(i == 4&&j==4){
							 * System.out.println(pyramidImg[c-1][i * bSize +
							 * k][j * bSize + l]); }
							 */
							temp += pyramidImg[c - 1][i * bSize + k][j * bSize + l];
						}
					}

					if (flag) {
						//pyramidImg[c][i][j] = Math.round(temp / 4.0);
						// pyramidImg[c][i][j] = temp;					
						//pyramidImg[c][i][j] = toFixPointCount(temp / 4.0, 4);
						pyramidImg[c][i][j] = toFixPointCount(temp , 4);
					} else {
						 //pyramidImg[c][i][j] = Math.round(temp / 4.0);
						// pyramidImg[c][i][j] = temp;
						//pyramidImg[c][i][j] = toFixPointCount(temp / 4.0, 4);// ������ֵΪ����ʱ������ȡ������ΪС��ʱ��ȡ��λС����
						// ����һ������flag��ʶΪ����������С��
						pyramidImg[c][i][j] = toFixPointCount(temp , 4);
					}
					/* System.out.println(temp); */
					// System.out.print(pyramidImg[c][i][j] + " ");
				}
				// System.out.println();
			}
			// System.out.println();
			// ͨ���ײ�Ľ��������Ҷ�Ӧ��

		}
		return pyramidImg;
	}

	// �������ɢ����������
	public static double[][] ErrDifToOtherPixel(double BinaryValue, double[][] uniformImg, double[][][] MarkPyraImg,int i, int j) {
		/*
		 * int h=uniformImg.length; int w=uniformImg[0].length;
		 */
       
		double ErrorValue = toFixPointCount(uniformImg[i][j] - BinaryValue, 4);
		// System.out.println(ErrorValue);
		 
		//double ErrorValue = uniformImg[i][j] - BinaryValue;
		int[][] v = { { 2, 1, 2 },{ 1, 0, 1 }, { 2, 1, 2 } };

		/* ����ߡ����ұߺ����±�����ʹ�ò����˲���������ʹ�������˲��� */
		if (i != 0 && i != w - 1 && j == h - 1)// ���ұ�һ��ʹ�ò����˲���(���һ�У��������ϽǺ����½�������Ԫ��)
		{
			/*
			 * 
			 * �˲������£�
			 *  2 1 
			 *  1 P 
			 *  2 1        
			 */
			/*
			 * (i-1,j-1) (i-1,j) 
		   *   (i,j-1)  (i,j)  
		 *    (i+1,j-1) (i+1,j) 
			 * 
			 * 
			 */
			uniformImg[i - 1][j-1] += toFixPointCount((ErrorValue * 2) / 7,4);
			uniformImg[i - 1][j] += toFixPointCount((ErrorValue * 1) / 7,4);
			uniformImg[i][j - 1] += toFixPointCount((ErrorValue * 1) / 7,4);
			uniformImg[i + 1][j - 1] += toFixPointCount((ErrorValue * 2) / 7,4);
			uniformImg[i + 1][j ] += toFixPointCount((ErrorValue * 1) / 7,4);

			/* ��ֹ��� */
			/*
			 * if(uniformImg[i][j+1]>1) uniformImg[i][j+1]=1;
			 * if(uniformImg[i][j+1]<0) uniformImg[i][j+1]=0;
			 * 
			 * for(int k=1;k<2;k++) for(int l=0;l<2;l++) {
			 * if(uniformImg[i+k][j+l]>1) uniformImg[i+k][j+l]=1;
			 * if(uniformImg[i+k][j+l]<0) uniformImg[i+k][j+l]=0; }
			 */
			return uniformImg;

		}
		if (i != 0 && i != w - 1 && j == 0)// �����һ��ʹ�ò����˲���(�ұ�һ�У��������ϽǺ����½�������Ԫ��)
		{
			/*
			 * �˲������£�1 2  
			 *          P 1 
			 *          1 2
			 */         
			/*
			 * (i-1,j) (i-1,j+1) 
		 *       (i,j)  (i,j+1)
		 *      (i+1,j) (i+1,j+1)
		 * 
			 * 
			 * 
			 */
			uniformImg[i - 1][j ] += toFixPointCount((ErrorValue * 1) / 7,4) ;
			uniformImg[i - 1][j+1] += toFixPointCount((ErrorValue * 2) / 7,4) ;
			uniformImg[i][j + 1] += toFixPointCount((ErrorValue * 1) / 7,4) ;
			uniformImg[i + 1][j] += toFixPointCount((ErrorValue * 1) / 7,4) ;
			uniformImg[i + 1][j + 1] +=toFixPointCount((ErrorValue * 2) / 7,4);

			/* ��ֹ��� */
			/*
			 * for(int k=1;k<2;k++) for(int l=-1;l<1;l++) {
			 * if(uniformImg[i+k][j+l]>1) uniformImg[i+k][j+l]=1;
			 * if(uniformImg[i+k][j+l]<0) uniformImg[i+k][j+l]=0; }
			 */
			return uniformImg;
		}

		if (i == w - 1 && j != 0 && j != h - 1)// ���һ��Ԫ�أ��������½Ǻ����½ǣ�ʹ�ò����˲���
		{

			/*
			 * �˲������£� 2 1 2 
			 *          1 P 1
			 * 
			 */
			/*
			 * (i-1,j-1) (i-1,j) (i-1,j+1) (i,j-1) (i,j) (i,j+1)
			 * 
			 * 
			 */
			uniformImg[i - 1][j - 1] += toFixPointCount((ErrorValue * 2) / 7,4) ;
			uniformImg[i - 1][j] += toFixPointCount((ErrorValue * 1) / 7,4) ;
			uniformImg[i - 1][j + 1] += toFixPointCount((ErrorValue * 2) / 7,4) ;
			uniformImg[i][j - 1] += toFixPointCount((ErrorValue * 1) / 7,4) ;
			uniformImg[i][j + 1] += toFixPointCount((ErrorValue * 1) / 7,4) ;
			/* ��ֹ��� */
			/*
			 * if(uniformImg[i][j+1]>1) uniformImg[i][j+1]=1;
			 * if(uniformImg[i][j+1]<0) uniformImg[i][j+1]=0;
			 */
			return uniformImg;
		}

		if (i == 0 && j != 0 && j != h - 1)// �����������һ��Ԫ��(�������ϽǺ����Ͻ�)
		{
			/*
			 * �˲������£� 1 P 1 
			 *           2 1 2
			 * 
			 */
			/*
			 * (i,j-1) (i,j) (i,j+1) (i+1,j-1) (i+1,j) (i+1,j+1)
			 * 
			 * 
			 */
			uniformImg[i][j - 1] += toFixPointCount((ErrorValue * 1) / 7,4);
			uniformImg[i][j + 1] += toFixPointCount((ErrorValue * 1) / 7,4);
			uniformImg[i + 1][j - 1] += toFixPointCount((ErrorValue * 2) / 7,4);
			uniformImg[i + 1][j] += toFixPointCount((ErrorValue * 1) / 7,4);
			uniformImg[i + 1][j + 1] += toFixPointCount((ErrorValue * 2) / 7,4);

			return uniformImg;
		}

		if (i == 0 && j == 0)// ��������Ͻ�Ԫ��
		{
			/*
			 * �˲������£� P 1 
			 *          1 2
			 * 
			 */
			/*
			 * (i,j) (i,j+1) (i+1,j) (i+1,j+1)
			 * 
			 * 
			 */
			uniformImg[i][j + 1] += toFixPointCount((ErrorValue * 1) / 4,4);
			uniformImg[i + 1][j] += toFixPointCount((ErrorValue * 1) / 4,4);
			uniformImg[i + 1][j + 1] += toFixPointCount((ErrorValue * 2) / 4,4);

			return uniformImg;
		}

		if (i == 0 && j == h - 1)// ��������Ͻ�Ԫ��
		{
			/*
			 * �˲������£� 1 P 
			 *          2 1
			 * 
			 */
			/*
			 * (i,j-1) (i,j) (i+1,j-1) (i+1,j)
			 * 
			 * 
			 */
			uniformImg[i][j - 1] += toFixPointCount((ErrorValue * 1) / 4,4);
			uniformImg[i + 1][j - 1] += toFixPointCount((ErrorValue * 2) / 4,4);
			uniformImg[i + 1][j] += toFixPointCount((ErrorValue * 1) / 4,4);

			return uniformImg;
		}

		if (i == w - 1 && j == 0)// ��������½�Ԫ��
		{
			/*
			 * �˲������£� 1 2 
			 *          P 1
			 * 
			 */
			/*
			 * (i-1,j) (i-1,j+1) (i,j) (i,j+1)
			 * 
			 * 
			 */
			uniformImg[i - 1][j] += toFixPointCount((ErrorValue * 1) / 4,4);
			uniformImg[i - 1][j + 1] += toFixPointCount((ErrorValue * 2) / 4,4);
			uniformImg[i][j + 1] += toFixPointCount((ErrorValue * 1) / 4,4);

			return uniformImg;
		}

		if (i == w - 1 && j == h - 1)// ��������½�Ԫ��
		{
			/*
			 * �˲������£� 2 1 
			 *          1 P
			 * 
			 */
			/*
			 * (i-1,j-1) (i-1,j) (i,j-1) (i,j)
			 * 
			 * 
			 */
			uniformImg[i - 1][j - 1] += toFixPointCount((ErrorValue * 2) / 4,4);
			uniformImg[i - 1][j] += toFixPointCount((ErrorValue * 1) / 4,4);
			uniformImg[i][j - 1] += toFixPointCount((ErrorValue * 1) / 4,4);

			return uniformImg;
		}

		
		/*
			 * �˲�������
			 * 2 1 2
			 * 1 P 1
			 * 2 1 2
			 */
		  /*
		 * (i-1,j-1) (i-1,j) (i-1,j+1) 
		 * (i,j-1)  (i,j)  (i,j+1)
		 *  (i+1,j-1) (i+1,j) (i+1,j+1)
		 * 
		 * 
		 */
		 //���һ��λ������ 
		  for (int k = -1; k < 2; k++)// ����k<2
			for (int l = -1; l < 2; l++){
				//System.out.println(uniformImg[i + k][j + l]);
				if(MarkPyraImg[0][i+ k][j+ l] == 0){
					uniformImg[i + k][j + l] += toFixPointCount(ErrorValue * v[k + 1][l + 1] / 12,4) ;
					uniformImg[i + k][j + l] = toFixPointCount(uniformImg[i + k][j + l],4);
				}
				
				//System.out.println(toFixPointCount(ErrorValue * v[k + 1][l + 1] / 12,4));
				//System.out.println(uniformImg[i + k][j + l]);
					/*
					 * if(uniformImg[i+k][j+l]>1) uniformImg[i+k][j+l]=1;
					 * if(uniformImg[i+k][j+l]<0) uniformImg[i+k][j+l]=0;
					 */
			 }
		

		/*
		 * (i-1,j-1) (i-1,j) (i-1,j+1) 
		 * (i,j-1)  (i,j)  (i,j+1)
		 *  (i+1,j-1) (i+1,j) (i+1,j+1)
		 * 
		 * 
		 */
		return uniformImg;
	}

}