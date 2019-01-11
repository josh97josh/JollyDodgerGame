class projectile extends circle{
  
  float minDamage,maxDamage; 
  
  circle firedBy;
  
  projectile(PVector pos,PVector vel,float size,circle firedBy){
    super(pos,vel,size);
    this.firedBy = firedBy;
    PVector toMove = new PVector(vel.x,vel.y);
    toMove.normalize().mult(size/2 + firedBy.size/2);
    this.pos = PVector.add(pos,toMove);
    
    dampener = 1; 
    
    vel = vel.normalize().mult(6);
    cannon.trigger();
    
    
  }
  
  // Single ray used for small circle, Checks whether ball has hit an circle 
  boolean canCreate(){
    ray r = new ray(tr,enemies,pl);
     r.findRays(vel,0,pos);
     circle sh = r.hitCircle;
     if(sh!= null && firedBy != null){
       if(sh.pos != firedBy.pos){
         if(PVector.dist(pos,sh.pos) < size/2 + sh.size/2){
           dealDamage(sh);
           return false;
         }
       }
     }
     return true;
  }
  
  void dealDamage(circle sh){
    Random rand = new Random();
    int toDeal  = rand.nextInt(firedBy.maxDamage+1 - firedBy.minDamage) + firedBy.minDamage ;
    //System.out.println(toDeal); 
    sh.health -= toDeal ;
    if(sh.health <=0 && firedBy.isPirate && sh.hasTreasure != 0 && firedBy.hasTreasure == 0){
      firedBy.hasTreasure = sh.hasTreasure;
    }
    impact.trigger(); 
    
  }
  
  // Gets movement for the projectile 
  boolean getNewPos(){
    
    pos.x = pos.x + vel.x; 
    pos.y = pos.y + vel.y;
    applyDampener();
    return true; 
    
  }
  
  // Checks whether cannon ball has moved off the screen 
  boolean offScreen(){
    if(pos.x< gameArea.x + size/2 || pos.x > gameArea.x + gameSize.x - size/2 || pos.y< gameArea.y + size/2 || pos.y > gameArea.y + gameSize.y  - size/2){
      return true; 
    }
    return false; 
  }
}
