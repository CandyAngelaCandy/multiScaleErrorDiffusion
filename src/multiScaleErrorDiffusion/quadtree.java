package multiScaleErrorDiffusion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class quadtree {
   
	//����һ�ö����
    public static TreeNode creatTree(ArrayList<Integer> ids, ArrayList<Integer> parents, 
    		ArrayList<Integer> costs){
        //�������ڵ�
        TreeNode root = new TreeNode();
        Map<Integer, TreeNode> maps = new HashMap<Integer, TreeNode>();
        //�������н����Ϣ�������н�������Ϣ����maps��
        for(int i = 0; i < ids.size(); i++){
            TreeNode node = new TreeNode();
            node.setId(ids.get(i));
            node.setParentId(parents.get(i));
            node.setCost(costs.get(i));
            maps.put(ids.get(i), node);
        }
        //����map���������Ϊ0�Ľ��ŵ�������£���������Ӧ���ڵ�������ӽ��
        for(Map.Entry<Integer, TreeNode> entry : maps.entrySet()){
            TreeNode node = entry.getValue();
            Integer partentId = node.getParentId();
            if(partentId == 0){
                root.getChilds().put(node.getId(), node);//de.getId()�ȼ���node.getKey()
            }else{
                TreeNode pNode = maps.get(partentId);
                pNode.getChilds().put(node.getId(), node);
            }
        }
        return root;
    }

}//quadtree�����

//�������Ľ��
class TreeNode{
    private Integer id;
    private Integer parentId;
    private int cost;
    private Map<Integer,TreeNode> childs = new HashMap<Integer,TreeNode>();//��������ӽ�� <�ӽ��Id���ӽ��>
    public Integer getId() {
        return id;
    }
   public void setId(Integer id) {
       this.id = id;
   }
   public Integer getParentId() {
       return parentId;
   }
   public void setParentId(Integer parentId) {
       this.parentId = parentId;
   }
   public int getCost() {
       return cost;
   }
   public void setCost(int cost) {
       this.cost = cost;
   }
   public Map<Integer, TreeNode> getChilds() {
       return childs;
   }
   public void setChilds(Map<Integer, TreeNode> childs) {
       this.childs = childs;
   }


//���������  ����������
        private static StringBuilder builder = new StringBuilder();
        public static void IteratorTree(TreeNode root){
          
	          if(root != null){
	              for(Map.Entry<Integer, TreeNode> entry : root.getChilds().entrySet()){//����map
	                  
	                  builder.append(entry.getKey());
	                  
	                 if(entry.getValue().getChilds() != null && entry.getValue().getChilds().size() > 0){
	                     IteratorTree(entry.getValue());
	                 }
	             }
	         }
       }
        
        
        //ϸ��Ϊ�������⣺1.��������б������Ȩ 2.������(����֮ǰ�ź�����Ӻ�����Ȩ),���Ϊ�ѷ��ʣ��ٻ��ݱ���(�ݹ�Ĺ���)
        

}
