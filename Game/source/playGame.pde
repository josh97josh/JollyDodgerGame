class playGame{
void performMovements(){
   if(movements[0]){
     pl.movey(-1);
   }
   if(movements[1]){
     pl.movey(1);
   }
   if(movements[2]){
     pl.movex(1); 
   }
   if(movements[3]){
     pl.movex(-1); 
   }
}

// Aimer used for player cannon firing 
void drawAimer(){
  noFill();
  line(mouseX,mouseY-15, mouseX,mouseY+15);
    line(mouseX-15,mouseY, mouseX+15,mouseY);
    ellipse(mouseX,mouseY,10,10);
    fill(0,0,0);

}

// Manages cannon fire and destroys cannon if travels off the screen or hits a player
void cannonCollisions(){
  for(int i = 0; i< cannons.size() && i>=0; i++){
    projectile c = cannons.get(i);
    c.create();
    if(!c.offScreen()){
      
      for(int j = 0; j < enemies.size();j++){
        enemy e = enemies.get(j);
        if(e.pos.x != c.firedBy.pos.x && e.pos.y != c.firedBy.pos.y){
        if(new collision(e,c).isColliding()){
          cannons.remove(c);
          c.dealDamage(enemies.get(j));
          i--;
        }
        }
      }
      
      if(new collision(pl,c).isColliding()){
        if(pl.pos.x != c.firedBy.pos.x && pl.pos.y != c.firedBy.pos.y){
          cannons.remove(c);
          c.dealDamage(pl);
          i--;
        }
      }
      
    }
  }
}


void drawEnemies(){
  for(int i = 0; i< enemies.size() && i >= 0;i++){
    enemy e = enemies.get(i);
    if(e.health<= 0){
      enemies.remove(i);
      i--;
    }
    if(PVector.dist(e.pos, pl.pos) < 200)  {
      if(e.canFire()){
        
        cannons.add(new projectile(new PVector(e.pos.x,e.pos.y),new PVector(pl.pos.x- e.pos.x,pl.pos.y- e.pos.y),5,e));
      }
      else{
        //System.out.println("idnt fire") ;
      }
    }
    
    e.create();
    e.enemyMovement(pl,tr,treasureLocations);
  }
  
  for (int i = 0; i< enemies.size();i++){
    collisions.add(new collision(pl,enemies.get(i))); 

    for(int j = 0; j< i; j++){
      collisions.add(new collision(enemies.get(i),enemies.get(j))); 
    }
  }
  islandCollisions.clear();
  for(enemy e: enemies){
    islandCollisions.add(new islandCollision(e,tr));
  }
}

// Resolves collisions between all game objects 
void resolveCollisions(){
 pg.cannonCollisions();
  
   for(int i = 0; i < islandCollisions.size(); i++){
    islandCollisions.get(i).isColliding();
  }
  playerCollision.isColliding();
  
  for(int i = 0; i < collisions.size(); i++){
    collisions.get(i).isColliding();
  } 
}

// Checks whether level has been completed or if player has died
void checkEndLevel(){
  if(treasureLocations.size() == 0 && pl.hasTreasure == 0){
      if(enemyHasAnyTreasure() ==0){
        gameStage = 2; 
      }
    }
    
    if(pl.health <= 0){
      killed.trigger(); 
      gameStage = 4;
    }
  }
  
  int enemyHasAnyTreasure(){
  int count= 0; 
  for(enemy e: enemies){
    if(e.hasTreasure != 0){
      count ++;
    }
  }
  return count; 
}

  
}
