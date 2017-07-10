package multiScaleErrorDiffusion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class quadtree {
   
	//构建一棵多叉树
    public static TreeNode creatTree(ArrayList<Integer> ids, ArrayList<Integer> parents, 
    		ArrayList<Integer> costs){
        //创建根节点
        TreeNode root = new TreeNode();
        Map<Integer, TreeNode> maps = new HashMap<Integer, TreeNode>();
        //遍历所有结点信息，将所有结点相关信息放入maps中
        for(int i = 0; i < ids.size(); i++){
            TreeNode node = new TreeNode();
            node.setId(ids.get(i));
            node.setParentId(parents.get(i));
            node.setCost(costs.get(i));
            maps.put(ids.get(i), node);
        }
        //遍历map，将父结点为0的结点放到根结点下，否则在相应父节点下添加子结点
        for(Map.Entry<Integer, TreeNode> entry : maps.entrySet()){
            TreeNode node = entry.getValue();
            Integer partentId = node.getParentId();
            if(partentId == 0){
                root.getChilds().put(node.getId(), node);//de.getId()等价于node.getKey()
            }else{
                TreeNode pNode = maps.get(partentId);
                pNode.getChilds().put(node.getId(), node);
            }
        }
        return root;
    }

}//quadtree类结束

//定义树的结点
class TreeNode{
    private Integer id;
    private Integer parentId;
    private int cost;
    private Map<Integer,TreeNode> childs = new HashMap<Integer,TreeNode>();//存放所有子结点 <子结点Id，子结点>
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


//遍历多叉树  输出遍历结果
        private static StringBuilder builder = new StringBuilder();
        public static void IteratorTree(TreeNode root){
          
	          if(root != null){
	              for(Map.Entry<Integer, TreeNode> entry : root.getChilds().entrySet()){//遍历map
	                  
	                  builder.append(entry.getKey());
	                  
	                 if(entry.getValue().getChilds() != null && entry.getValue().getChilds().size() > 0){
	                     IteratorTree(entry.getValue());
	                 }
	             }
	         }
       }
        
        
        //细分为两个问题：1.如何在树中标记优先权 2.遍历树(遍历之前排好序，添加好优先权),标记为已访问，再回溯遍历(递归的过程)
        

}
