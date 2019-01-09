class collision{
  float e, ratio,overlap;
  circle c1,c2;
  PVector normal,dist,impulse,momentum1,momentum2;
  

  collision(circle c1, circle c2){
    this.c1 = c1; 
    this.c2 = c2;
    this.e=min(c1.e,c2.e);    
    
  }
  
  // Detects whether a circle has come into contact with another circle
  boolean isColliding(){
    this.dist = PVector.sub(c1.pos, c2.pos) ;    

    //System.out.println(dist.mag());
    if(dist.mag() < c1.size/2 + c2.size/2){
      
      moveApart();
      normal = dist.normalize();

      resolveImpulse();
      boolean place =  c1.leftRound;
      c1.leftRound = c2.leftRound;
      c2.leftRound = place;
      return true;
            
    }
    return false;
  }
  
  // Calculates the impulse of a collision
  void calculateImpulse(){
    float j  = PVector.sub(c2.vel,c1.vel).dot(normal);
    float inversemass = 1/c1.mass + 1/c2.mass; 
    j = j * -(1+e);
    j = j/inversemass;
    impulse = PVector.mult(normal, j);
  }
  
  // resolves the new velocities after collision
  void resolveImpulse(){
    
    calculateImpulse();
    
    c1.vel.sub(PVector.mult(impulse, 1/c1.mass));
    c2.vel.add(PVector.mult(impulse, 1/c2.mass));
  }
  
  // Moves circles apart to prevent overlap 
  void moveApart(){
    if(c1.hasTreasure!= 0 && c2.hasTreasure == 0 && c2.isPirate){
      c2.hasTreasure = c1.hasTreasure; 
      c1.hasTreasure = 0; 
    }
    else if(c1.hasTreasure ==0 && c2.hasTreasure!= 00 && c1.isPirate){
      c1.hasTreasure = c2.hasTreasure; 
      c2.hasTreasure = 0; 
    }
    float overlap = c1.size/2 + c2.size/2 - dist.mag(); 
    normal = dist.normalize();
    PVector move = PVector.mult(normal, overlap/2);
    c1.pos.add(move); 
    c2.pos.sub(move);
    
    
  }
  
}
