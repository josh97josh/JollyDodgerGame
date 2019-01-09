class island extends terrain{
  PVector pos,size; 
  int hasTreasure = 0;
  PVector treasurePos = new PVector(0,0); 
  ArrayList<PVector> treasureLocations;
  int originalVerteces;
  boolean hasTree; 
  
  
  
  island(PVector pos, PVector size, int numberVerteces, ArrayList<PVector> treasureLocations){
    
    super();
    this.numberVerteces = numberVerteces;
    this.originalVerteces = numberVerteces; 
    this.treasureLocations = treasureLocations;
    doesShrink(pos,size);
    this.pos = new PVector(pos.x + 2*objectSizes, pos.y + 2*objectSizes); 
    this.size = new PVector(size.x - 4* objectSizes, size.y - 4* objectSizes);
    this.hasTree = true; 
    addVerteces(); 
  }
  
  // create space between island and edge of game area 
  void doesShrink(PVector pos , PVector size){
    if(pos.x == gameArea.x){
      pos.x += objectSizes;  
      size.x -= objectSizes; 
    }
    if(pos.x + size.x == gameArea.x + gameSize.x ){
      size.x -= objectSizes; 
    }
    if(pos.y == gameArea.y){
      pos.y += objectSizes ;
      size.y -= objectSizes;
    }
    if(pos.y + size.y == gameArea.y + gameSize.y){
      size.y -= objectSizes;
    }
  }
  
  
  island(ArrayList<PVector> verteces){
    this.verteces = verteces; 
    this.numberVerteces = verteces.size();
    this.edges = new PVector[numberVerteces];
    getEdges();
    toColor = false;
  }
  
  // Get verteces within given area 
  void generateVerteces(){
    for( int i = 0; i< numberVerteces ; i++){
        verteces.add(new PVector(random(pos.x,pos.x + size.x),random( pos.y, pos.y + size.y)));      
    }
    getAverageVertex();    
    orderVerteces(0, verteces.size()-1);
  }
  
  void addVerteces(){
    generateVerteces();
    getEdges();
    
  }
  
  float toDegrees(float angle){
      return angle/TWO_PI * 360;
  }
  
  // Get edges between verteces 
  void getEdges(){
    edges = new PVector [numberVerteces];
    for(int i = 0; i< numberVerteces - 1 ;i++){
        edges[i] =  PVector.sub(verteces.get(i+1),verteces.get(i));
      }
      edges[numberVerteces-1] = PVector.sub(verteces.get(0),verteces.get(verteces.size() -1));
}

 // Add treasure onto random vertex
void putInTreasure(){
  Random rand = new Random();
  int willPutin = rand.nextInt(2);
  if(willPutin == 1){
  int  n = rand.nextInt(verteces.size()) ;
  int j = rand.nextInt(5);
    hasTreasure = j; 
    hasTree = false;
    PVector v = verteces.get(n);
    treasurePos = new PVector(v.x,v.y);
  }
    
}

// Remove treasure in collision 
void removeTreasure(){
  hasTreasure = 0; 
    for(PVector t: treasureLocations){
      if(t.x == treasurePos.x && t.y == treasurePos.y) {
        treasureLocations.remove(t);
        break;
      }
    }

  treasurePos = null; 
}

void drawShape(){
       super.drawShape();
  if(hasTreasure!= 0){
    noStroke();
    treasureImg.resize(40,30);
    image(treasureImg,treasurePos.x -10,treasurePos.y-10);
  }
  else if(hasTree ){
    treeImg.resize((int) 50,(int)50); 
   image(treeImg,averageVertex.x -25 ,averageVertex.y -50 );
  }
  
  
  
  fill(0,0,0);
  stroke(0,0,0);
}




  
}
