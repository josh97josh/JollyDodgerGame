class shadow extends terrain{
  float angle,size; 
  ArrayList<PVector> allVerteces; 
  
  shadow(ArrayList<PVector> allVerteces,float angle,float size){
    super();
    this.allVerteces = allVerteces;
    this.numberVerteces = verteces.size();
    averageVertex = new PVector(gameArea.x + (gameSize.x)/2,gameArea.y + (gameSize.y)/2);
    this.angle = angle; 
    this.size = size; 
  }
  
  void drawShape(){
    
    if(pl.detected){
      fill(255,0,0); 
    }
    else{
      fill(255,255,0);
    }
    
    noStroke();
    PVector center = new PVector(gameArea.x + (gameSize.x)/2,gameArea.y + (gameSize.y)/2);
    if(size + angle > PI){
      crossesPI(center);
      return; 
    }
    else{
      for(PVector v: allVerteces){
        float ag = atan2(v.y - center.y,v.x - center.x); 
        if(isContained(ag) || PVector.dist(center,v) == 0){
            verteces.add(v);    
        }
      }
      
      numberVerteces = verteces.size();
      averageVertex = center;
      orderVerteces(0,numberVerteces -1);
      
      drawNormal(verteces, center);
    }
    
  }
  
  // Orders and draws beam created by lighthouse
  void drawNormal(ArrayList<PVector> verteces, PVector center){
    
    beginShape();
    if(size!= TWO_PI){
      vertex(center.x,center.y);
    }  
    for(PVector v: verteces){
          vertex(v.x,v.y);     
    }
    endShape(CLOSE);  
    stroke(0,0,0);
    fill(0,0,0);
  }
  
  // beam passes over PI and - PI 
  void drawOverPi(terrain ps, terrain neg, PVector center){
    beginShape();
    if(size != TWO_PI){
      vertex(center.x,center.y);
    }
    for(PVector v: ps.verteces){
          vertex(v.x,v.y);     
    }
    for(PVector v: neg.verteces){
          vertex(v.x,v.y);     
    }
    
    endShape(CLOSE);  
    stroke(0,0,0);
    fill(0,0,0);
  }
  
  boolean isContained(float ag){
    if(ag > angle){
      return (ag - angle) <= size; 
    }
    else{
      return ((PI - angle) + (ag +PI)) <= size ; 
    }
    
  }
  
  // Orders rays when beam passes over pi/ -pi 
  void crossesPI(PVector center){
   
    terrain posTr = new terrain();
    terrain negTr = new terrain();
    
    for(PVector v: allVerteces){
      float ag = atan2(v.y - center.y,v.x - center.x); 
      if(isContained(ag) || PVector.dist(center,v) == 0){
          if(ag > 0) {
            posTr.verteces.add(v);
          }
          else{
            negTr.verteces.add(v);
          }
      }
    }
    posTr.averageVertex = center;
    negTr.averageVertex = center;
    posTr.numberVerteces = posTr.verteces.size();
    negTr.numberVerteces = negTr.verteces.size();
    
    if(posTr.numberVerteces > 1){
      posTr.orderVerteces(0,posTr.numberVerteces -1);
    }
    if (negTr.numberVerteces > 1){
      negTr.orderVerteces(0,negTr.numberVerteces-1);
    }
    drawOverPi(posTr, negTr, center);
  }
  
  
  
  
}
