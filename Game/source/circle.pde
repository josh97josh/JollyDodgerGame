class circle{
  PVector pos,vel; 
  float  mass,size,e,dampener, rotation;
  boolean leftRound = true;
  boolean isPirate; 
  int hasTreasure,health,maxHealth,minDamage,maxDamage; 
  
  
  circle(PVector pos,PVector vel,float size){
    this.pos = pos;
    this.vel = vel;
    this.size = size;
    this.mass = size;
    this.e = 0.7;
    this.dampener = 0.97;
    this.hasTreasure = 0; 
  }
  
  
  void create(){
    fill(0,0,0);
    ellipse(pos.x,pos.y,size,size);
    getNewPos();
  }
  
  // Applies velocity update on moving circle 
  boolean getNewPos(){
    pos.x = pos.x + vel.x; 
    pos.y = pos.y + vel.y;
    applyDampener();
    return wallCollision();
  }
  
  // Applies a collision with a wall to the circle 
  boolean wallCollision(){
    boolean needUpdate = false; 
    if(pos.x > gameArea.x + gameSize.x  -  size/2 ) {
      vel.x = - e*vel.x;
      pos.x = gameArea.x + gameSize.x - size/2;
      leftRound = !leftRound;
      needUpdate = true; 
    }
    else if(pos.x < gameArea.x + size/2){
      vel.x = -e*vel.x;
      pos.x = gameArea.x + size/2;
      leftRound = !leftRound;
      needUpdate = true; 
    }
    
    if(pos.y > gameArea.y + gameSize.y - size/2) {
      vel.y = -e*vel.y;
      pos.y =  gameArea.y + gameSize.y - size/2;
            leftRound = !leftRound;
      needUpdate = true; 

    }
    else if (pos.y < gameArea.y + size/2){
      vel.y = -e*vel.y;
      pos.y = gameArea.y + size/2;
            leftRound = !leftRound;
            needUpdate = true; 
    }
    return needUpdate; 
  }
  
  PVector getMomentum(){
    return PVector.mult(vel,mass);
  }
  
  // Applys water resistance to circle 
  void applyDampener(){
    vel.mult(dampener);
  }
  
  // Calculates the point of intersection of a line segment and the circle 
  PVector getMinDistance(PVector d,PVector p){
     float r = size/2;
     float x1 = pos.x; 
     float y1 = pos.y;
     PVector min,max;
    
   if(abs(d.x) < abs(d.y)  ){
     float m = d.x/d.y;
     float c = p.x-m*p.y;
     float ax = 1 + m * m; 
     float bx = -2*y1 + 2 * m * c - 2 * m * x1;
     float cx = y1 * y1 + c*c - 2 * x1 * c + x1 * x1 - r * r;
     float check = bx*bx - 4*ax*cx;
     if(check < 0.0){
       return null;
     }
     
     float miny = (-bx-sqrt(check))/(2*ax);
     float maxy = (-bx+sqrt(check))/(2*ax);
     
     min = new PVector(m*miny + c,miny);
     max = new PVector(m*maxy + c,maxy); 
     
   }
   else{
     float m = d.y/d.x;
     float c = p.y - m*p.x;
     
     float ax = 1 + m * m;
     float bx = -2*x1+2*m*c -2*m*y1;
     float cx = x1*x1 + c*c - 2*y1*c+y1*y1-r*r;
     
     float check = bx*bx - 4*ax*cx;
     if(check < 0.0){
       return null;
     }
     
     float minx = (-bx-sqrt(check))/(2*ax);
     float maxx = (-bx+sqrt(check))/(2*ax);
     
     min = new PVector(minx,m*minx + c);
     max = new PVector(maxx,m*maxx + c);
   }
   
   if(PVector.dist(min,p)< PVector.dist(max,p) && (min.x-p.x)/d.x > 0 ){
       return min;
   }
   else if ((max.x - p.x)/d.x > 0){
     return max;
   }
   else return null; 
 }
 
 // Works out whether treasure can be dropped off by a pirate in a drop off zone
 boolean canDropOff(){
   if(PVector.dist(pos,new PVector(gameArea.x,gameArea.y))< 100 + size/2 && hasTreasure == 4){
     return true;
   }
   if(PVector.dist(pos,new PVector(gameArea.x + gameSize.x,gameArea.y))< 100 + size/2 && hasTreasure == 1){
     return true;
   }
   if(PVector.dist(pos,new PVector(gameArea.x + gameSize.x,gameArea.y + gameSize.y))< 100 + size/2 && hasTreasure == 2){
     return true;
   }
   if(PVector.dist(pos,new PVector(gameArea.x,gameArea.y + gameSize.y))<100 + size/2 && hasTreasure == 3){
     return true;
   }
   
   return false;
   
 }
  
  
}
