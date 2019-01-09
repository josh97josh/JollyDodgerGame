class ray{
  ArrayList<island> tr = new ArrayList<island> (); 
  ArrayList<enemy> circles = new ArrayList<enemy>();
  terrain shape;
  circle circ,hitCircle;
  player pl; 
  PVector pos = new PVector(gameArea.x + (gameSize.x)/2, gameArea.y + (gameSize.y)/2);
  PVector[] dir,minRay;
  PVector centreShape;
  island hitShape;
  ArrayList<PVector> vertecesToReturn = new ArrayList<PVector>();
  
  // Used for getting the rays from lightHouse for an island 
  ray(ArrayList<island> tr, terrain shape, player pl){
    this.tr = tr;
    this.shape = shape;
    this.pl = pl;
    this.dir = new PVector[shape.numberVerteces * 2];
    this.minRay = new PVector[shape.numberVerteces * 2];
    this.centreShape = new PVector();
  }
  
  // Used for getting the rays from lighthouse beam for a circle
  ray(ArrayList<island> tr, circle en, player pl){
    this.tr = tr;
    this.circ = en; 
    this.pl = pl;
    this.dir = new PVector[4]; 
    this.minRay = new PVector[4]; 
    this.centreShape = new PVector(); 
  }
  
  // Used for generic use of rays, e.g for enemy collision avoidance
  ray(ArrayList<island> tr , ArrayList<enemy> circles,player pl){
    this.tr  = tr; 
    this.circles = circles;
    this.pl = pl;
    this.minRay = new PVector[3];
    this.dir = new PVector[3];
    this.centreShape = new PVector();
  }
  
  // Get angles to shine rays for island for beam
   ArrayList<PVector> getAnglesForShape(ArrayList<island> tr, ArrayList<enemy> circles){
     this.circles = circles; 
     this.tr = tr;
     vertecesToReturn.clear();
     if(shape == null){
       return vertecesToReturn;
     }
    for(int i = 0; i< shape.numberVerteces; i++){
      
      PVector toVertex = PVector.sub(shape.verteces.get(i),pos);
      toVertex.normalize();
      float angle = atan2(toVertex.y,toVertex.x);
      int j = i*2;
      toVertex = new PVector(cos(angle + 0.00001),sin(angle+ 0.00001));
      findRays(toVertex,j,pos);
     addVertex(j);
      j+=1;
      toVertex = new PVector(cos(angle - 0.00001),sin(angle- 0.00001));
      findRays(toVertex,j,pos);
     addVertex(j);
    }
    return vertecesToReturn;
  }
  
  // Get tangent angle for circle to shine rays for beam 
  ArrayList<PVector> getAnglesForCircle(ArrayList<island> tr , ArrayList<enemy> circles){
    this.tr = tr; 
    this.circles = circles; 
    vertecesToReturn.clear();
    if(circ == null){
       return vertecesToReturn;
     }
     PVector toVertex = PVector.sub(circ.pos,pos);
     float angle = atan2(toVertex.y, toVertex.x);
     float dist = PVector.dist(pos,circ.pos);
     float adj = dist * dist - (circ.size/2) * (circ.size/2);
     float offset1 = dist * dist - ((circ.size/2) + 1) * ((circ.size/2) + 1);
     float offset2 = dist * dist - ((circ.size/2) - 1) * ((circ.size/2) - 1);
     
     if(adj > 0 && offset1 > 0 && offset2 > 0){
       adj = sqrt(adj);
       offset1 = sqrt(offset1);
       offset2 = sqrt(offset2);
     }
     else{
      return vertecesToReturn; 
     }
     
     float tangentUp = atan2((circ.size/2)+1,offset1);
     float tangentDown = atan2((circ.size/2)-1, offset2);
     
     float[] toTestArray = new float[]{angle + tangentUp, angle + tangentDown, angle - tangentUp, angle - tangentDown};
     
     for(int i = 0; i< 4; i++){
       float toTest = toTestArray[i]; 
       toVertex = new PVector(cos(toTest),sin(toTest));
       findRays(toVertex,i,pos);
       addVertex(i);
     }
     return vertecesToReturn;
  }
  
  void addVertex(int i){
    if(minRay[i] != null){
      vertecesToReturn.add(minRay[i]);
    }
  }
  
  // Gets minimum distance and the point of closest contact for a ray shined from point 'pos' in the direction 'd' 
  PVector findRays(PVector d, int i, PVector pos){
   this.pos = pos;
    dir[i] = d;
    minRay[i] = new PVector(10000,10000);
    islandCheck(i);
    minRay[i] = new PVector(pos.x + dir[i].x * minRay[i].x, pos.y + dir[i].y * minRay[i].x);
    
    circleCheck(d,i);
    playerCheck(d,i);
    
    if(gamePaused){
      drawRay(i);
    }
    return minRay[i];
  }
  
  // Checks for ray contact with island
  void islandCheck(int i){
    for(island sh: tr){
      for(int j = 0; j< sh.numberVerteces; j++){
        PVector seg = sh.verteces.get(j);
        PVector linedr = sh.edges[j];
        boolean hasUpdated = updateMinRay(seg,linedr,i);
        if(hasUpdated){
          sh.getAverageVertex();
          centreShape = new PVector(sh.averageVertex.x,sh.averageVertex.y);
          hitShape = sh;
        }
      }
    }
    addScreenEdges(i);
  }
  
  // checks for ray contact with enemies
  void circleCheck(PVector d, int i){
    for(enemy en: circles){
      PVector crossOver = en.getMinDistance(d,pos);
      //System.out.println(crossOver);
      if(crossOver != null){
        if(PVector.dist(crossOver,pos) < PVector.dist(minRay[i],pos)){
          minRay[i] = crossOver;
          hitCircle = en;
        }
      }
    }
  }
  
  // checks for ray contact with player
  void playerCheck(PVector d, int i){
    if(pl!= null){
      PVector crossOver = pl.getMinDistance(d,pos);
        //System.out.println(crossOver);
        if(crossOver != null){
          if(PVector.dist(crossOver,pos) < PVector.dist(minRay[i],pos)){
            minRay[i] = crossOver;
            hitCircle = pl;
          }
        }
    }
  }
  
  
  // Update closest point for ray if it was closer than previously recorded value 
  boolean updateMinRay(PVector seg, PVector linedr,int i){
      PVector calc = new PVector(0,0);
      float gradient = dir[i].y/dir[i].x;
      float gradientLine = linedr.y/linedr.x;
        if(abs(gradientLine) == abs(gradient) ){
          return false;
        }
      calc.y = ((dir[i].x * (seg.y - pos.y)) + (dir[i].y * (pos.x - seg.x)))/(linedr.x * dir[i].y - linedr.y * dir[i].x);
      calc.x = (seg.x + (linedr.x*calc.y) - pos.x)/dir[i].x;
      
      if(calc.x >= 0 && calc.y >= 0 && calc.y <= 1){
        if(minRay[i] == null){
          minRay[i] = calc; 
          return true;
        }
        else if(calc.x < minRay[i].x){
          minRay[i] = calc;
          return true;
        }
        else{
          return false;
        }
      }
      return false;
    }
    
    // Adds screen edges as a shape to be included in collision point detection 
    void addScreenEdges(int i){
      updateMinRay(new PVector(gameArea.x,gameArea.y), new PVector(gameArea.x + gameSize.x,0),i); 
      updateMinRay(new PVector(gameArea.x + gameSize.x,gameArea.y), new PVector(0,gameArea.y + gameSize.y),i); 
      updateMinRay(new PVector(gameArea.x,gameArea.y + gameSize.y), new PVector(gameArea.x + gameSize.x,0),i); 
      updateMinRay(new PVector(gameArea.x,gameArea.y), new PVector(0,gameArea.y + gameSize.y),i);
    }
  
  // Draws the direction and collision point of an array 
  void drawRay(int i){
    //System.out.println(minRay);
    fill(0,0,0);
    if(minRay[i] != null){
     line(pos.x,pos.y, minRay[i].x, minRay[i].y);
     //System.out.println(pos.x + dir.x *minRay.x + " " + );
     ellipse(minRay[i].x,minRay[i].y, 10,10);
    }
  }
}
