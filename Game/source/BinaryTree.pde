class BinaryTree{
  PVector pos,size; 
  BinaryTree childLeft,childRight;
  float splitMin,objectSize;
  boolean specialSquare = false; 
  ArrayList<PVector> treasureLocations; 
  
  // Creates random areas for where islands can be drawn 
  BinaryTree(PVector pos, PVector size, float splitMin, ArrayList<island> is,ArrayList<PVector> treasureLocations, float objectSize){
    this.pos = pos; 
    this.size = size; 
    this.splitMin = splitMin; 
    this.objectSize = objectSize;
    this.treasureLocations = treasureLocations;
    generateChildren(is);
  }
  
  // Performs split algorithm 
  void generateChildren(ArrayList<island> is){
    Random rand = new Random();
        int  n = rand.nextInt(2) ;
        if (n ==1 && size.x > splitMin) {
            HorizontalSplit(is);
        }
        else if(size.y > splitMin) {
            verticalSplit(is);
        }
        else{
          if(containsPoint(new PVector(gameArea.x + (gameSize.x)/2,gameArea.y + (gameSize.y)/2))){
              //specialSquare = true; 
              return;
          }
          if(containsPoint(new PVector(gameArea.x + 5,gameArea.y +5)) || containsPoint(new PVector(gameArea.x + gameSize.x-5,gameArea.y +5)) || containsPoint(new PVector(gameArea.x + gameSize.x-5,gameArea.y + gameSize.y-5) )|| containsPoint(new PVector(gameArea.x +5,gameArea.y+gameSize.y-5))){
            //specialSquare = true;
            return;
          }
          if(containsPoint(new PVector(gameSize.x/2 + gameArea.x + 5, gameArea.y + 5))){
            return;
          }
          n = rand.nextInt(2);
          if(n==1){
           
              island toAdd = new island(new PVector(pos.x , pos.y ),new PVector(size.x, size.y),6,treasureLocations);
            
            tr.add(toAdd );
            toAdd.putInTreasure();
            if(toAdd.hasTreasure != 0){
              treasureLocations.add(toAdd.treasurePos);
            }
          }
        }
    
  }
  
  // Used to prevent island being drawn on lighthouse 
  boolean containsPoint(PVector position){
    if(pos.x < position.x  && pos.x + size.x > position.x){
            if(pos.y < position.y && pos.y + size.y > position.y){
              return true;
            }
          }
          return false;
  }
  
  // Split area vertically 
  public void verticalSplit(ArrayList<island> is){

        float n = random( splitMin/2,size.y-splitMin/2);
        
        childLeft = new BinaryTree(pos,new PVector (size.x,n),splitMin,is,treasureLocations,objectSize);
        childRight = new BinaryTree(new PVector (pos.x,pos.y + n), new PVector(size.x,size.y - n),splitMin,is,treasureLocations,objectSize);

    }

    // Split area horizontally 
    public void HorizontalSplit(ArrayList<island> is){

        float n = random( splitMin/2,size.x-splitMin/2);

        childLeft = new BinaryTree(pos,new PVector(n,size.y),splitMin,is,treasureLocations,objectSize);
        childRight = new BinaryTree(new PVector(pos.x + n,pos.y), new PVector(size.x -n,size.y),splitMin,is,treasureLocations,objectSize);
    }
    
    // Draw areas created by binary tree split 
    void create(){
      if(gamePaused){
        noFill();
        if( specialSquare) {
          stroke(255,255,0); 
        }
        rect(pos.x,pos.y,size.x,size.y);
        if(childLeft != null){
          childLeft.create();
          childRight.create();
        }
        fill(0,0,0);
        stroke(0,0,0);
      }
      
    }
  
  
  
  
}
