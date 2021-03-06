package multiScaleErrorDiffusion;

import java.awt.Point;
import java.math.BigDecimal;
import ImgOperation.ImageOperation;
import publicClass.publicClass;

//程序的问题：误差扩散有问题？？，并没有均匀分配到周围像素，再用8*8验证一下

/*1.将图像按下采样分解为逐层金字塔，最后为一个像素
 *2.根据最底层，逐渐寻找块中最大值，并生成标记矩阵，将此位置标记为-1，生成残差矩阵，将误差扩散到
 *残差矩阵周围像素，并将残差矩阵此位置标记为0，再逐层计算最底层那个像素值是否小于0.5，终止转换。
 */
public class MED extends publicClass {

	public static int bSize = 2;// 块的大小
	public static int PyramidCount = (int) (Math.log(h * w) / Math.log(2 * 2));// 金字塔层级,它的值为0，其实为原始图像层级
	public static int[] sideLen = new int[PyramidCount + 1];//每层图像边长
	
	// 构造函数：初始化每层金字塔图像的边长
	public MED() {

		// 定义数组保存每层图像边长
		sideLen[0] = h;// 第0层为16，第PyramidCount层为1
		for (int c = 1; c < PyramidCount + 1; c++) {
			// 计算第i层金字塔图像的大小
			int ImgWidth = (int) Math.sqrt(h * w / (Math.pow((2 * 2), (c))));
			sideLen[c] = ImgWidth;// 保存第c层金字塔的边长，第0层边长为16，第4层边长为1.
		}
			
	}

	// 金字塔有五层
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();    //获取开始时间
	
		MED med = new MED();
		// med.imgPyramid("lena140.bmp");
		// med.imgPyramid("lena16.bmp");
		med.grayImgToHalfImgBaseMED("lena~8.bmp");
	    /*
		 * for(int i=0;i<sideLen.length;i++){ 
		 *   System.out.println(sideLen[i]); 
		 * }
		 * System.out.println(sideLen);
		 */

		long endTime = System.currentTimeMillis();    //获取结束时间

		System.out.println("程序运行时间：" + (endTime - startTime)/(1000.0*60) + " minutes");    //输出程序运行时间					
	}

	// 基于MED将灰度图像转换为半色调图像
	public void grayImgToHalfImgBaseMED(String imgName) {

		double[][] covImg = ImageOperation.readImg(imgName); //定义载体图像covImg
		double[][] markImg = new double[h][w];// 生成标记图像

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				covImg[i][j] = toFixPointCount(covImg[i][j] / 255.0, 4);// 载体像素归一化，保留4位小数
				markImg[i][j] = 0;
				// System.out.print(covImg[i][j] + " ");
			}
			// System.out.println();
		}

		double[][][] pyramidImg = GeImgPyramid(covImg, true);//灰度图像生成金字塔图像

		// 定义误差图误差矩阵  errorMax=covImg-BinMax
		double[][] errorMax = new double[h][w];// 误差矩阵
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				errorMax[i][j] = covImg[i][j];// 初始化金字塔第一级
				// System.out.print(errorMax[i][j] + " ");
			}
			// System.out.println();
		}

		//定义二值化矩阵BinMax，BinMax初始化为全0矩阵
		double[][] BinMax = new double[h][w];// 2值化矩阵
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				BinMax[i][j] = 0;// 初始化金字塔第一级
				// System.out.print(BinMax[i][j] + " ");
			}
			// System.out.println();
		}

		
		// 生成标记金字塔，标记的规则：初始化为0，访问过则标记为-1，现在是要确定标记搜索下一个位置
		double[][][] MarkPyraImg = GeImgPyramid(markImg, true);

		// ！！！输出原始金字塔图像
		//printPramidImg(pyramidImg);

		double lastLayerFlagVal = pyramidImg[PyramidCount][0][0];
		System.out.println(lastLayerFlagVal);
		int count = 1;

		// count<=20 138835  10560
		// lastLayerFlagVal >= 128 //2 56的一半，相当于归一化后的0.5; lastLayerFlagVal >= 0.5
		while (lastLayerFlagVal >= 0.5) {//停机条件
			
			/*System.out.println();
			  System.out.println();
			*/
			//System.out.println("第" + count + "次查找开始");
			lastLayerFlagVal = singlePosMEDToHalfImg(errorMax, MarkPyraImg, BinMax);// 没调用一次，count++;
			count++;
			System.out.println(lastLayerFlagVal);//误差矩阵底层金字塔的值
			// System.out.println();
		}

		System.out.println("总共查找："+count+"次");
		
		//printBinImg(BinMax, h, w);// 打印出2值矩阵
		//printBinImg(errorMax, h, w);// 打印出误差矩阵
		
		System.out.println(lastLayerFlagVal);//最底层金字塔的值，即全局误差
		
		ImageOperation.printBufferedImage(BinMax, "multiScaleHalfImg");//输出半色调图像
		
		System.out.println("程序运行结束");
	}

		
	 /*一一一一一 核心代码
		 逐层查找最大像素值，并标记该位置已遍历,并更新图像金字塔，判断最后的像素值是否小于0.5
		 将载体像素归一化，保留4位小数，在此基础上找到载体像素最大值，得到其位置，对应二值矩阵该位置为1，
		 关键是更新误差矩阵，计算该位置的误差，扩散到周围像素		
	 */
		
	// 函数功能:通过图像金字塔，逐层查找，直到找道第0层较大像素值;函数参数：误差矩阵，标记矩阵，二值矩阵
	public static double singlePosMEDToHalfImg(double[][] errorMax, double[][][] MarkPyraImg, double[][] BinMax) {
       
		/* 关键问题：如何标记搜索,如何保证不要重复访问，
                     首先寻找未访问点，找它它所在的金字塔层数，函数功能：寻找到最底层那个像素，并标记矩阵。
		*/
		
		int layer = -1;//访问金字塔层数

		// 思想：只求标记标记矩阵-1的个数是否等于边长的2倍，求应该在第几层查找
		for (int i = PyramidCount; i > -1; i--) {// 以16*16为例，i=5;
			boolean f = calSumInMarkmatri(i, MarkPyraImg);
			if (!f) {
				layer = i;
				break;
			}
		}

		// System.out.println("应在第" + layer + "层查找");
	
		// 根据所在层，逐渐查找到第0层所在的像素值

		//取出第layer层金字塔，遍历未访问的像素
		double[][][] pyramidImg = GeImgPyramid(errorMax, true);// true表示传入为整数数组

	    //记录原始图像最大像素值位置 
		Point p = new Point(-1, -1);

		// 查找位置(i,j),在一个像素图像中最大值。
		Point point = selectMaxPixInAllUnmark(layer, MarkPyraImg, pyramidImg);
		int a = point.x, b = point.y;

		// System.out.println("layer:"+layer);
		MarkPyraImg[layer][a][b] = -1;// 访问过元素标记

		p = new Point(a, b);
		//System.out.print("第" + layer + "层选中的坐标：" + p.x + " " + p.y);
		//System.out.println("选取第" + layer + "层金字塔最大像素值：" + pyramidImg[layer][a][b]);
		int m = layer - 1;

		for (; m > -1; m--) {		
			double temp = 0;

			Point lastLayP = new Point(p.x, p.y);// 避免下面变量覆盖，互相影响

			for (int k = 0; k < bSize; k++) {
				for (int l = 0; l < bSize; l++) {
					int x = lastLayP.x * bSize + k, y = lastLayP.y * bSize + l;
					double temp2 = pyramidImg[m][x][y];

					// System.out.println("("+x+","+y+")="+pyramidImg[m][x][y]);

					if (temp2 > temp) {// 在2*2大小的块中 ，寻找最大值
						temp = temp2;
						p.x = x;
						p.y = y;
					}
					// System.out.println();
				}
			}
			
			MarkPyraImg[m][p.x][p.y] = -1;// 访问过元素标记
			//System.out.print("第" + m + "层选中的坐标：" + p.x + " " + p.y);
			//System.out.println("选取第" + m + "层金字塔最大像素值：" + pyramidImg[m][p.x][p.y]);
			
		} // m循环结束
		m = m + 1;

		//System.out.print("第" + m + "层选中的坐标：" + p.x + " " + p.y);

		
		//System.out.println("m=" + m);
		System.out.println("选取第" + m + "层金字塔最大像素值：" + pyramidImg[m][p.x][p.y]);

		if (m == 0) {// 将二值矩阵该位置置为1
			//System.out.println("选中的位置：(" + p.x + "," + p.y + ")");
			BinMax[p.x][p.y] = 1;// 未归一化，先写为255

			// 添加误差扩散，更新errorMax数组
			ErrDifToOtherPixel(BinMax[p.x][p.y], errorMax, MarkPyraImg ,p.x, p.y);
			//ErrDifToOtherPixel(BinMax[p.x][p.y], errorMax ,p.x, p.y);
			errorMax[p.x][p.y] = 0;// 计算误差扩散到周围像素，(先不添加误差扩散)并将该位置值置为0，该矩阵能量衰减。
		}

		// 计算最后一个像素值
		double[][][] UpdatePyramidImg = GeImgPyramid(errorMax, true);// true表示传入为整数数组

		//System.out.println("标记矩阵：");
		//printPramidImg(MarkPyraImg);// 打印出标记矩阵

		//System.out.println("金字塔图像：");
		//printPramidImg(UpdatePyramidImg);
		
		printBinImg(errorMax,h,w);//打印出更新后误差矩阵

		// System.out.println(UpdatePyramidImg[PyramidCount][0][0]);

		return UpdatePyramidImg[PyramidCount][0][0];//返回更新后金字塔图像底层像素值

	}

	// 将浮点数转为4位小数
	public static double toFixPointCount(double f, int pointCount) {
		BigDecimal b = new BigDecimal(f);
		double f1 = b.setScale(pointCount, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}

			
	// 求标记矩阵中值为-1的和
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
	
	// 在所有未标记的像素值中选取最大像素值
	public static Point selectMaxPixInAllUnmark(int layer, double[][][] MarkPyraImg, double[][][] pyramidImg) {
		// 使用最快的排序算法，找出一维数组中最大值

		// 先用最笨方法，找最大值，再优化
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
		 * double maxPix = (double)list[layer].get(0); //查找原始下标 int index =
		 * (int) map[layer].get(b[i]); Point point = new Point(index/3,index%3);
		 * System.out.println(b[i]+"("+point.x+","+point.y+")");
		 * list[layer].remove(0);
		 */

		return p;

	}

	// 打印出逐层金字塔图像
	public static void printPramidImg(double[][][] pyramidImg) {

		for (int i = 0; i < PyramidCount + 1; i++) {
			System.out.println("第" + i + "层金字塔：");
			for (int m = 0; m < sideLen[i]; m++) {
				for (int n = 0; n < sideLen[i]; n++) {
					System.out.print(pyramidImg[i][m][n] + " ");
				}
				System.out.println();
			}
		}

	}

	// 打印出二维数组
	public static void printBinImg(double[][] BinMax, int h, int w) {
		for (int m = 0; m < h; m++) {
			for (int n = 0; n < w; n++) {
				System.out.print(BinMax[m][n] + " ");
			}
			System.out.println();
		}
	}

	// 函数参数：总数和金字塔层数；函数功能：判断在哪一层查找
	public static void locateSumInLayer(double sum, int layer) {

	}

	//通过一个二维数组生成图像金字塔,返回一个三维数组保存各层图像
	public static double[][][] GeImgPyramid(double[][] covImg, Boolean flag) {

		// System.out.println("金字塔层数：" + PyramidCount);//
		// 金字塔的层数，以16*16为例，PyramidCount=4，算上原始图像，
		// 一共5层

		// 三维数组保存金字塔逐层图像
		double[][][] pyramidImg = new double[PyramidCount + 1][h][w];

		// System.out.println("第" + 0 + "层");
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				pyramidImg[0][i][j] = covImg[i][j];// 初始化金字塔第一级
				// System.out.print(covImg[i][j] + " ");
			}
			// System.out.println();
		}

		for (int c = 1; c < PyramidCount + 1; c++) {

			// 一个数组保存每一层对应的图像长

			// 找到像素与小块的对应关系，怎么通过一个像素块找到对应的像素块

			// 产生原始图像的下一层金字塔 ，金字塔层数从0开始，第一层为原始图像，第PyramidCount=4层为一个像素
			// System.out.println("第" + c + "层");
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
						//pyramidImg[c][i][j] = toFixPointCount(temp / 4.0, 4);// 当像素值为整数时，向下取整，当为小数时，取四位小数，
						// 设置一个变量flag标识为整数，还是小数
						pyramidImg[c][i][j] = toFixPointCount(temp , 4);
					}
					/* System.out.println(temp); */
					// System.out.print(pyramidImg[c][i][j] + " ");
				}
				// System.out.println();
			}
			// System.out.println();
			// 通过底层的金字塔逐渐找对应块

		}
		return pyramidImg;
	}

	//将误差扩散到其他像素，滤波器为3*3
	public static double[][] ErrDifToOtherPixel(double BinaryValue, double[][] uniformImg, double[][][] MarkPyraImg,int i, int j) {
		/*
		 * int h=uniformImg.length; int w=uniformImg[0].length;
		 */
       
		double ErrorValue = toFixPointCount((uniformImg[i][j] - BinaryValue), 4);
		// System.out.println(ErrorValue);
		 
		//double ErrorValue = uniformImg[i][j] - BinaryValue;
		int[][] v = { { 2, 1, 2 },{ 1, 0, 1 }, { 2, 1, 2 } };

		/* 最左边、最右边和最下边像素使用部分滤波器，其余使用完整滤波器 */
		if (i != 0 && i != w - 1 && j == h - 1)// 最右边一列使用部分滤波器(左边一列，除了左上角和左下角这两个元素)
		{
			/*
			 * 
			 * 滤波器如下：
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

			/* 防止溢出 */
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
		if (i != 0 && i != w - 1 && j == 0)// 最左边一列使用部分滤波器(右边一列，除了右上角和右下角这两个元素)
		{
			/*
			 * 滤波器如下：1 2  
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

			/* 防止溢出 */
			/*
			 * for(int k=1;k<2;k++) for(int l=-1;l<1;l++) {
			 * if(uniformImg[i+k][j+l]>1) uniformImg[i+k][j+l]=1;
			 * if(uniformImg[i+k][j+l]<0) uniformImg[i+k][j+l]=0; }
			 */
			return uniformImg;
		}

		if (i == w - 1 && j != 0 && j != h - 1)// 最后一行元素（除了左下角和右下角）使用部分滤波器
		{

			/*
			 * 滤波器如下： 2 1 2 
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
			/* 防止溢出 */
			/*
			 * if(uniformImg[i][j+1]>1) uniformImg[i][j+1]=1;
			 * if(uniformImg[i][j+1]<0) uniformImg[i][j+1]=0;
			 */
			return uniformImg;
		}

		if (i == 0 && j != 0 && j != h - 1)// 如果是最上面一行元素(除了左上角和右上角)
		{
			/*
			 * 滤波器如下： 1 P 1 
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

		if (i == 0 && j == 0)// 如果是左上角元素
		{
			/*
			 * 滤波器如下： P 1 
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

		if (i == 0 && j == h - 1)// 如果是右上角元素
		{
			/*
			 * 滤波器如下： 1 P 
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

		if (i == w - 1 && j == 0)// 如果是左下角元素
		{
			/*
			 * 滤波器如下： 1 2 
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

		if (i == w - 1 && j == h - 1)// 如果是右下角元素
		{
			/*
			 * 滤波器如下： 2 1 
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
			 * 滤波器如下
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
		 //针对一般位置像素 
		  for (int k = -1; k < 2; k++)// 错误：k<2
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
	
	//16那个滤波器
	public  static double [][] ErrDifToOtherPixel(double BinaryValue,double [][]uniformImg,int i,int j)
	{
		
		
		double ErrorValue=uniformImg[i][j]-BinaryValue;
		System.out.println(ErrorValue);
		
		
		int [][] v = {{0,0,7},{3,5,1}};
		
		/*最左边、最右边和最下边像素使用部分滤波器，其余使用完整滤波器*/
		  if(i!=w-1 && j==0 )//最左边使用部分滤波器
		  {
			  /*滤波器如下：P  7
			            5  1
			   */
			  uniformImg[i][j+1]+=(ErrorValue*7)/13;
			  uniformImg[i+1][j]+=(ErrorValue*5)/13;
			  uniformImg[i+1][j+1]+=(ErrorValue*1)/13;
			  
			  /*防止溢出*/
			 /* if(uniformImg[i][j+1]>1)  uniformImg[i][j+1]=1;
	     	  if(uniformImg[i][j+1]<0)  uniformImg[i][j+1]=0;
			  
			   for(int k=1;k<2;k++)
				  for(int l=0;l<2;l++)
		       {
			      if(uniformImg[i+k][j+l]>1)  uniformImg[i+k][j+l]=1;
	     	      if(uniformImg[i+k][j+l]<0)  uniformImg[i+k][j+l]=0;
			   }
			  */
			  
		  }
		  else if(i!=w-1&&j==h-1)//最右边使用部分滤波器;使用动态阈值
		       {
			      /*滤波器如下：0  P
	                         3  5
	              */
			     uniformImg[i+1][j]+=(ErrorValue*5)/8;
			     uniformImg[i+1][j-1]+=(ErrorValue*3)/8;
			     
			     /*防止溢出*/
				   for(int k=1;k<2;k++)
					  for(int l=-1;l<1;l++)
			     {/*
				    if(uniformImg[i+k][j+l]>1)  uniformImg[i+k][j+l]=1;
		     	    if(uniformImg[i+k][j+l]<0)  uniformImg[i+k][j+l]=0;*/
				 }
				   
		       } 
		     
	          else  if(i==w-1&&j!=h-1)//最后一行元素（除了最后一个元素）使用部分滤波器
	                {
	        	      uniformImg[i][j+1]+=(ErrorValue*7)/7;
	        	      
	        	      /*防止溢出*/
	        		  /*if(uniformImg[i][j+1]>1)  uniformImg[i][j+1]=1;
	             	  if(uniformImg[i][j+1]<0)  uniformImg[i][j+1]=0;*/
	                }
	               else  if(i==w-1&&j==h-1)//如果是最后一行，最后一列元素，无滤波器参与
	               {
	            	   
	               }
		            else//一般元素使用完整滤波器。
		           {		    
						     /*滤波器如下0  P  7
						      *       3  5  1
	                          */
		            	 for(int k = 0;k<2;k++)// 错误：k<2
			                 for(int l =-1;l<2;l++){			                       
			                       uniformImg[i+k][j+l]+= ErrorValue*v[k][l+1]/16;
			                      /* if(uniformImg[i+k][j+l]>1)  uniformImg[i+k][j+l]=1;
			                       if(uniformImg[i+k][j+l]<0)  uniformImg[i+k][j+l]=0;*/
			                         
			                  }
		           }
		  return uniformImg;
	}


}