class player extends circle{
  boolean detected;
  float lastFired,shotRate,maxSpeed; 
  
 player(PVector pos,PVector vel,float size){
   super(pos,vel,size);
   isPirate = true;
   detected = false; 
    maxDamage = 30; 
   minDamage = 30; 
   shotRate = 1000; 
   health = 100;
   maxHealth = 100;
   maxSpeed = 3; 
   rotation = 0; 
 }
  
  // Player Movement 
  void movex(int x){
   if(x < 0) {
     rotation -= PI/32;   
   }
   else{
     rotation += PI/32 ;  
   }
   
  }
  
  void movey(int y){
    PVector direction = new PVector(cos(rotation), sin(rotation)).mult(0.2); 
    if(y < 0) {
      vel.sub(direction);
    }
    else{
      vel.add(direction) ;   
    }
    if(vel.mag() > maxSpeed){
      vel.normalize().mult(maxSpeed) ;   
    }
    
    
  }
  
  // Checks whether player can fire after firing previous cannon 
  boolean playerCanFire(){
    if(gamePaused){
      return false;  
    }
    if(lastFired == 0){
      lastFired = millis();
      return true; 
    }
    if(millis() - lastFired > shotRate){
      lastFired = 0;  
      return true; 
    }
    return false;
    
  }
  
   void create(){
     //System.out.println(health);
         
    if(!gamePaused){
      if(getNewPos()){
        rotation = atan2(vel.y,vel.x); 
      }
    }
    fill(0,0,0); 
    stroke(0,0,0);
    strokeWeight(5);
    PVector dir = new PVector(cos(rotation), sin(rotation)).mult(16) ;
    line(pos.x,pos.y, pos.x + dir.x, pos.y + dir.y); 
    
    noStroke();
    strokeWeight(5);

    fill(0,0,0);
    if(hasTreasure == 1){
      stroke(255,0,0);
    }
    else if(hasTreasure == 2){
      stroke(255,165,0);
    }
    else if(hasTreasure == 3){
      stroke(255,105,180);
      
    }else if(hasTreasure == 4){
      stroke(0,255,0);
    }
      fill(100,149,237);
    ellipse(pos.x,pos.y,size,size);
    pirateImg.resize((int)size,(int)size); 
    image(pirateImg,pos.x-size/2,pos.y-size/2);
    
    if(canDropOff()){
      stolen.trigger();
      playerTreasure += random(75,125);
      hasTreasure = 0;
    }
   
    fill(0,0,0); 
    stroke(0,0,0);
    
    strokeWeight(1);
    
  }
  
 // Upgrades player at end of level 
 void playerUpgrade(){
   maxHealth += random(10,50);
   health = maxHealth;
   
   int toAdd = (int)random(5,25);
   maxDamage += toAdd;
   minDamage += random(5, min(25, maxDamage-minDamage));
   shotRate = max(20, shotRate - random(5,10));
   //speed += 0.1;
 }
  
}
