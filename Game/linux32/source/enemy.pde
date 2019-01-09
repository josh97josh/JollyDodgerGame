class enemy extends circle{
  boolean willWander = true;
  PVector approaching = new PVector(0,0);
  island avoidingShape;
  ArrayList<PVector> treasureLocations;
  float lastFired, shotRate; 
  PVector[] possibleContacts = new PVector[3];
  
  enemy(PVector pos,PVector vel,float size){
   super(pos,vel,size);
   lastFired = 0; 
   Random rand = new Random();
   int  n = rand.nextInt(2) ;
   if(n==1){
     isPirate = true;
   }

   generateStats();
 }
  
  // Returns whether an enemy can fire again 
  boolean canFire(){
    if(gamePaused){
      return false;  
    }
    if(lastFired == 0){
      lastFired = millis();
      return true; 
    }
    if(millis() - lastFired > shotRate){
      lastFired = 0;  
      return false; 
    }
    return false;
  }
  
 // calculates what type of movement the enemy will perform 
 void enemyMovement(player pl, ArrayList<island> islands, ArrayList<PVector> treasureLocations){
   if(gamePaused){
     return; 
   }
   this.treasureLocations = treasureLocations; 
   if (isPirate && hasTreasure == 0){
     if (treasureLocations.size() == 0){
       seek(pl.pos,islands);
       return;
     }
     seek(getClosest(),islands);
     return;
     
   }
   else if(isPirate && hasTreasure != 0){
     seek(getDropOffPoint(),islands);
     return ;
   }
   else if(!isPirate && pl.detected){
     if(PVector.dist(pl.pos,pos)> 100){
       seek(pl.pos,islands);
     }

   }
   else{
     if(PVector.dist(pos, pl.pos) < 200 && PVector.dist(pl.pos,pos)> 100 ){
       seek(pl.pos,islands); 
     }
     else{
       wander();
     }
     return; 
   }
   
 }
 
 // Get direction of drop off point if has treasure
 PVector getDropOffPoint(){
   if(hasTreasure == 4){
     return new PVector(gameArea.x+5,gameArea.y+5);
   }
   if(hasTreasure == 1){
     return new PVector(gameArea.x + gameSize.x-5,gameArea.y + 5);
   }
   if(hasTreasure == 2){
     return new PVector(gameArea.x + gameSize.x-5,gameArea.y + gameSize.y-5);
     
   }
   if(hasTreasure == 3){
     return new PVector(gameArea.x + 5,gameArea.y + gameSize.y-5);
   }
   return null;
 }
 
 // Gets closest treasure to current position 
 PVector getClosest(){
   PVector closest = null; 
   float dist = 10000;
   for(PVector p: treasureLocations){
     float toTreasure = PVector.dist(pos,p);
     if(toTreasure < dist){
       closest = new PVector(p.x,p.y);
       dist = toTreasure;
     }
   }
   return closest;
 }
 
 // Move towards location using collision avoidance
 void seek(PVector location, ArrayList<island> islands){
   if(location == null){
     return; 
   }
   
   PVector dir = PVector.sub(location ,pos);
   PVector[] positions = getPositions(dir);
   ray[] rays = getRays(dir, positions,islands);
   int bestRay = getClosestCollision(dir, positions); 
   
   if(bestRay == -1 ) { 
     PVector towards = new PVector(dir.x, dir.y).normalize(); 
     approach(towards) ; 
     return ; 
   }
   else if(PVector.dist(rays[bestRay].hitShape.verteces.get(0),gameArea) == 0  ){
     PVector towards = new PVector(dir.x, dir.y).normalize(); 
     approach(towards) ; 
     return ; 
   }
   
   PVector toCorner = getCornerToAvoid(rays[bestRay].hitShape, dir,positions[bestRay]); 
   getPositionToMoveTo(toCorner, rays[bestRay].hitShape,positions[bestRay]); 
   
   
 }
 
 // Get expected collisions based on current velocity 
 PVector[] getPositions(PVector dir){
   PVector norm = new PVector(-dir.y,dir.x).normalize().mult(size/2);
   PVector pos1 = PVector.add(pos,norm);
   norm.mult(-1); 
   PVector pos2 = PVector.add(pos,norm);
   return new PVector[]{pos,pos1,pos2};
   
 }
 
 // Get rays based on tangents and current position 
 ray[] getRays(PVector dir, PVector[] positions, ArrayList<island> islands){
   ray[] rays = new ray[3];
   for(int i = 0; i< 3; i++){
      rays[i] = new ray(islands, new ArrayList<enemy>(),null); 
      possibleContacts[i] = rays[i].findRays(dir,0,positions[i]); 
   }
  return rays;    
 }
 
 // return closest collision
 int getClosestCollision(PVector dir, PVector[] positions){
   int bestRay = -1; 
   float closest = dir.mag() -  15 ;
   for(int i = 0; i< 3; i++){
      float toCheck = PVector.sub(positions[i], possibleContacts[i]).mag();
     if(toCheck < closest){
       closest = toCheck; 
        bestRay = i; 
     }
   }
   return bestRay; 
 }
 
 // Get corner needed to avoid 
 PVector getCornerToAvoid(island is, PVector dir, PVector pos1){
   PVector posToAvoid1 = new PVector(0,0) ; 
   PVector maxValue = new PVector(0,0);
   float biggestAngle1 = 0; 
   
   for(PVector v: is.verteces){
     PVector toPoint = PVector.sub(v,pos1); 
     float angleToCheck = PVector.angleBetween(dir,toPoint); 
     if(angleToCheck > biggestAngle1){
       biggestAngle1 = angleToCheck; 
       maxValue = new PVector(toPoint.x,toPoint.y) ; 
       posToAvoid1 = new PVector(v.x,v.y); 
     }
   }
   
   PVector posToAvoid2 = new PVector (0,0) ; 
   float biggestAngle2 = 0; 
  
   for(PVector v: is.verteces){
     PVector toPoint = PVector.sub(v,pos1); 
     float angleToCheck = PVector.angleBetween(maxValue, toPoint) ; 
     if(angleToCheck > biggestAngle2){
       biggestAngle2 = angleToCheck; 
       posToAvoid2 = new PVector(v.x,v.y); 
     }
   }
   
   if( abs(biggestAngle2 - 2*biggestAngle1) < 5) {
     
     PVector toLeft = PVector.sub(posToAvoid1, pos1);
     PVector toRight = PVector.sub(posToAvoid2, pos1); 
     if(PVector.angleBetween(toLeft, vel) < PVector.angleBetween(toRight, vel) ){
       return posToAvoid1; 
     }
     else{
       return posToAvoid2; 
     }
   }
   else{
     return posToAvoid2; 
   }
 }
 
 // Get position required to move towards inorder to avoid shape 
 void getPositionToMoveTo(PVector corner , island is, PVector pos1 ){
      fill(0,0,0); 
   PVector direction = PVector.sub(corner,pos1); 
   PVector norm = new PVector(-direction.y, direction.x); 
   norm.normalize().mult(size); 
   
   PVector option1 = PVector.add(norm, direction);
   PVector option2 = PVector.sub(direction, norm ) ; 
    
   //is.lightUp = true; 
   //ellipse(option1.x + pos1.x, option1.y + pos1.y,10,10); 
   //   ellipse(option2.x + pos1.x, option2.y + pos1.y,10,10); 

   PVector toTest1 = PVector.add(option1, pos1); 
   PVector toTest2 = PVector.add(option2,pos1); 
   
   if(PVector.dist(toTest1,is.averageVertex) > PVector.dist(toTest2, is.averageVertex)){
     approach(option1); 
     approaching = option1;
   }
   else{
     approach(option2); 
          approaching = option2; 

   }
 }
 
 // Random movement 
 void wander(){
   float angle = atan2(vel.y,vel.x);
   angle += random(-PI/36, PI/36);   
   vel = new PVector(cos(angle), sin(angle)).mult(1);
 }
 
 // Move velocity towards position to prevent zigzagging movement 
 void approach(PVector towards){
  
   //System.out.println(towards); 
   //line(pos.x, pos.y, pos.x + towards.x, pos.y + towards.y) ; 
   towards.normalize(); 
   towards.mult(2);
   float diffy = vel.y - towards.y;
   float diffx = vel.x - towards.x;
   
     diffy *= 0.2;
     diffx *= 0.2;
   
   
   vel.y = vel.y - diffy;
   vel.x = vel.x - diffx;
 }
 
 
 // Draw and update enemy position 
 void create(){
   noStroke();

    fill(0,0,0);
    
    fill(255,255,255);
    if(gamePaused) {
      if(isPirate){
        strokeWeight(5);  
        stroke(0,0,0);
      }
    }
    
    if(hasTreasure!= 0){
      strokeWeight(5);
            stroke(255,215,0);
    }
      fill(100,149,237);
    ellipse(pos.x,pos.y,size,size);
    PImage ei = enemyImg.copy();
    ei.resize((int)size,(int)size);
    image(ei,pos.x - size/2 ,pos.y-size/2);
      fill(0,0,0);
      textAlign(CENTER,CENTER);
      textSize(12);
      text(health,pos.x, pos.y + size/2 + 5);
      textSize(30);
      textAlign(LEFT);
      
    if(canDropOff()){
      enemySteal.trigger();
      enemyTreasure += random(50,100);
      hasTreasure = 0;
    }
    if(!gamePaused){
      getNewPos();
    }
        fill(0,0,0);
        stroke(0,0,0);
        strokeWeight(1);
  }
  
  // Get enemt stats based on current player level 
  void generateStats(){
    
    maxHealth = (int)(10 + random(level*20,level* 30));
    health = maxHealth;
   maxDamage = (int)(10 + random(level*5, level*10));
   minDamage = (int)random(maxDamage/2,maxDamage);
   shotRate = random(max(20,2000-level*5),2000);
    
  }
  
}
