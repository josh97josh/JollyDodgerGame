class setupGame{
 
  // Initialise images
void getImages(){
  lhimg = loadImage("lighthouse.png");
  pirateImg = loadImage("playerBoat.png");
  enemyImg = loadImage("enemyBoat.png");
  treasureImg = loadImage("treasure.png");
  waterImg = loadImage("water.jpg"); 
  treeImg = loadImage("tree.png");
}
  
  // Initialise sounds
  void getSounds(){
    startMusic = minim.loadFile("startMusic.wav")  ;  
    gameMusic = minim.loadFile("gameMusic.wav"); 
    warning = minim.loadFile("warning.wav");
    cannon  = minim.loadSample("cannon.wav"); 
    treasurePickup = minim.loadSample("treasureP.wav");
    enemySteal = minim.loadSample("enemySteal.wav"); 
    stolen = minim.loadSample("stolen.wav"); 
    enemyPickup = minim.loadSample("enemyPickup.wav"); 
    splosh = minim.loadSample("splosh.wav");
    killed = minim.loadSample("killed.wav"); 
    impact = minim.loadSample("impact.wav") ; 
  }
  
  void drawStartScreen(){
    textSize(50); 
    fill(255,255,255);
    textAlign(CENTER,CENTER);
    text("The Jolly Dodger", gameArea.x + gameSize.x/2, 200);
    textSize(30) ;
    text("Press n key to start a new game", gameArea.x + gameSize.x/2,  400);
    fill(0,0,0);
  }
  
  // Screen drawn at the end of every level 
  void drawLevelCompletedScreen(){
    textSize(50); 
    fill(255,255,255);
    textAlign(CENTER,CENTER);
    
    if(bossHappinessLevel <= 0){
      gameStage = 3;  
      splosh.trigger();
    }
    else{
      textSize(50); 
      fill(255,255,255);
      textAlign(CENTER,CENTER);
      text("Day " + level + " completed", gameArea.x + gameSize.x/2, gameArea.y + 100);
      textSize(30) ;
      text("PlayerTreasure = " + playerTreasure, gameArea.x + gameSize.x/2, gameArea.y + 200);
      text("Enemy Treasure = " + enemyTreasure, gameArea.x + gameSize.x/2, gameArea.y + 300);
      
      text("Boss Happiness " + bossHappinessLevel + "%", gameArea.x + gameSize.x/2, gameArea.y + 400);
  
      text("Press space key to start the next level", gameArea.x + gameSize.x/2, gameArea.y + 500);
      
      fill(0,0,0);
    }
  }
  
  void drawDeathScreen(){
    textSize(50); 
    fill(255,255,255);
    textAlign(CENTER,CENTER);
    PVector middle = PVector.add(gameArea, gameSize).div(2); 
     text("GameOver", middle.x,200);
     text("Press space to continue",middle.x , 300 );
     
     if(playerTreasure > oldHighScore){
       oldHighScore = playerTreasure;
     }
     else if(playerTreasure == oldHighScore){
      text("New highscore", middle.x, 400); 
      text(highScore, middle.x, 450);
     }
    fill(0,0,0);
  }
  
  // Draw coloured corners
  void drawPlayScene(){
  //Draw corners 
  fill(0,255,0);
  arc(gameArea.x,gameArea.y,200,200, 0, HALF_PI);
 
  fill(255,0,0);
  arc(gameArea.x + gameSize.x,gameArea.y,200,200, HALF_PI,PI);
  
  fill(255,165,0);
  arc(gameArea.x + gameSize.x,gameArea.y + gameSize.y,200,200, -PI, -HALF_PI);
  
  fill(255,105,180);
  arc(gameArea.x,gameArea.y + gameSize.y,200,200, -HALF_PI,0);
    
  }
  
  // Setup a new game
  void newGame(){
    startMusic.pause();
    startMusic.rewind();
    gameMusic.loop();
    pl = new player(new PVector(gameArea.x + gameSize.x -20,gameArea.x + gameSize.y -20),new PVector(0,0),28);
    playerTreasure = 0 ;
    enemyTreasure = 0; 
    level = 0; 
    gameStage = 1; 
    numBalls = 3; 

    newLevel();
}
  
  // Setup a new level
  void newLevel(){
    pl.playerUpgrade();
    level = level + 1; 
    pl.pos= new PVector(gameArea.x +20,gameArea.y + 20);
    movements = new boolean[4]; 
    pl.detected = false;
    warning.pause();
    warning.rewind();
    lh = new lighthouse();
    collisions = new ArrayList<collision>();
    islandCollisions = new ArrayList<islandCollision>();
    cannons = new ArrayList<projectile>();
    tr = new ArrayList<island> (); 
    //int numTerrains = 3; 
    newRay = new ArrayList<ray>();
    //int numballs = 3;
    objectSizes = 15;
    enemies = new ArrayList<enemy>();
    for (int i = 0; i< numBalls;i++){
      enemies.add(new enemy(getSpawnLocation(),new PVector(random(-3,3),random(-3,3)),getEnemySize()));
      collisions.add(new collision(pl,enemies.get(i))); 
  
      for(int j = 0; j< i; j++){
        collisions.add(new collision(enemies.get(i),enemies.get(j))); 
      }
    }
    
    for(enemy e: enemies){
      islandCollisions.add(new islandCollision(e,tr));
    }
    
    playerCollision = new islandCollision(pl,tr);
    treasureLocations = new ArrayList<PVector>();
    bn = new BinaryTree(new PVector(gameArea.x,gameArea.y), new PVector(gameSize.x,gameSize.y),350,tr,treasureLocations,objectSizes);
    
    while(tr.size() <=5 || treasureLocations.size() <= 2){
        tr.clear();
        treasureLocations.clear();
      
        bn = new BinaryTree(new PVector(gameArea.x,gameArea.y), new PVector(gameSize.x,gameSize.y),350,tr,treasureLocations,objectSizes);
    }
    ArrayList<PVector> corner = new ArrayList<PVector>();
    corner.add(new PVector(gameArea.x,gameArea.y));
    corner.add(new PVector(gameArea.x + gameSize.x,gameArea.y));
    corner.add(new PVector(gameArea.x + gameSize.x,gameArea.y + gameSize.y));
    corner.add(new PVector(gameArea.x,gameArea.y + gameSize.y));
    tr.add(new island(corner));
    
    for(terrain t: tr){
        newRay.add(new ray(tr,t,pl));
    }
    
    for(enemy e: enemies){
      newRay.add(new ray(tr,e,pl));
    }
    newRay.add(new ray(tr,pl,pl));
  
}

// Get random corner spawn loation for enemy 
PVector getSpawnLocation(){
  int n = rand.nextInt(3);
  if(n==0){
    return new PVector(gameArea.x + gameSize.x-5,gameArea.y + gameSize.y -5);
  }
  if(n==1){
    return new PVector(gameArea.x + gameSize.x -5 ,gameArea.y+5);
  }
  if(n==2){
    return new PVector(gameArea.x +5,gameArea.y + gameSize.y -5);
  }
    return new PVector(gameArea.x + gameSize.x-5,gameArea.y + gameSize.y -5);
  
}

// Get random size for new enemy 
float getEnemySize(){
 return random(20,28); 
}
  
}
