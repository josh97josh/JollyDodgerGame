class playerInfo{ 

  void create(){
    fill(0,0,0); 
    textSize(20); 
    textAlign(CENTER,CENTER);

    drawInfo(0); 
    fill(255,255,255);
    drawInfo(2); 
    
    fill(0,0,0);
  }
  
  // Used in drawing the whole HUD for the player
  public void drawInfo(float offset){
    text( "HighScore: ", gameArea.x + 170 + offset ,gameArea.y + 10); 
    text(highScore,gameArea.x + 170 + offset,gameArea.y + 30);
    
    text("Boss", gameArea.x + gameSize.x - 170 + offset,10);
    text("Happiness: ", gameArea.x + gameSize.x - 170 + offset,30);
    text(bossHappinessLevel,gameArea.x + gameSize.x - 170 + offset,50);
    
        
    text("Enemies: " + numBalls  ,gameArea.x + gameSize.x - 170 + offset,gameArea.y + gameSize.y -20);
    text("Treasure:" + (treasureLocations.size() + pg.enemyHasAnyTreasure()),gameArea.x + gameSize.x - 170 + offset,gameArea.y + gameSize.y -40);
    
    PVector position = new PVector(gameArea.x + 170 + offset, gameArea.y + gameSize.y -20 );
    
    text("Health: " + pl.health, position.x, position.y -60); 
    //text("Max Health: " + pl.maxHealth,position.x, position.y +30);
    text("Fire Rate: " + (int)pl.shotRate,position.x,position.y -40);
    text("Max Dam: " + pl.maxDamage,position.x, position.y -20);
    text("Min Dam: " + pl.minDamage,position.x, position.y);
    
    
    PVector posTitle = new PVector(gameSize.x/2 + gameArea.x + offset, 10);
    text("Level: " + level,posTitle.x, posTitle.y);
    text("PlayerTreasure: " + playerTreasure , posTitle.x, posTitle.y + 20); 
    text("Enemy Treasure: " + enemyTreasure,posTitle.x, posTitle.y +40);
  }
  
}
