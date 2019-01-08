import java.util.Random;
import ddf.minim.*;

Minim minim; 

// Declare sounds 
AudioPlayer startMusic,gameMusic, warning;
AudioSample cannon,treasurePickup,enemySteal,stolen,enemyPickup,splosh,killed,impact;

// declare images 
PImage lhimg, pirateImg, enemyImg, treasureImg, waterImg, treeImg;

// declare global variables 
boolean gamePaused;
int bossHappinessLevel,highScore,numBalls,level,playerTreasure,enemyTreasure,gameStage, oldHighScore; 
ArrayList<enemy> enemies; 
ArrayList<collision> collisions = new ArrayList<collision>();
ArrayList<islandCollision> islandCollisions = new ArrayList<islandCollision>();
islandCollision playerCollision; 
float objectSizes; 
ArrayList<PVector> treasureLocations;
ArrayList< projectile> cannons; 
boolean[] movements = new boolean[4]; 
PVector gameArea, gameSize, spawnLocation ;
playerInfo pinfo; 
lighthouse lh = new lighthouse();
Random rand = new Random();
player pl;
ArrayList<island> tr = new ArrayList<island> (); 
ArrayList<ray> newRay = new ArrayList<ray>();
BinaryTree bn;
setupGame sg = new setupGame();
playGame pg = new playGame(); 
background bg ;


// Game initialisation 
void setup() {
  size(966, 568);
  initialiseVariables();
  pinfo = new playerInfo();
  sg.getImages(); 
  minim=new Minim(this) ;
  sg.getSounds(); 
  
  startMusic.loop();
}
   
void initialiseVariables(){
    gamePaused = false; 
    bossHappinessLevel = 100; 
    highScore = 0; 
    oldHighScore = 0;
    level = 0; 
    playerTreasure = 0; 
    enemyTreasure = 0;
    gameStage = 0;
    gameArea = new PVector(0,0); 
    gameSize = new PVector(966,568);
   bg = new background();
} 

void draw() {
  background( 100,149,237);
  bg.drawBackground();
   //waterImg.resize((int) gameSize.x,(int)gameSize.y + 100); 
   //image(waterImg,gameArea.x,gameArea.y);
  
  if(gameStage == 0) {
    sg.drawStartScreen(); 
  }
  else if(gameStage == 2){
    sg.drawLevelCompletedScreen();
  }
  else if(gameStage == 3 || gameStage == 4){
    sg.drawDeathScreen();
    
  }else {
    
    lh.create(newRay,enemies,tr);
    sg.drawPlayScene();
    fill(0,0,0);
    
    pl.create();
    collisions.clear();
  
  pg.drawEnemies();
  
   for(terrain ter : tr){
    ter.drawShape();
  }
   pg.resolveCollisions();
   
    bn.create();
    bossHappinessLevel = playerTreasure - enemyTreasure + 100;  
    bossHappinessLevel = min(bossHappinessLevel, 100); 
    bossHappinessLevel = max(bossHappinessLevel, 0); 
    
    pinfo.create();
    
    pg.checkEndLevel();
    
  pg.drawAimer();
  pg.performMovements();
  if(playerTreasure > highScore){
      highScore = playerTreasure;
    }

  }
}

// Cannon fire 
void mousePressed(){
  if(gameStage == 1 ){
    if(pl.playerCanFire()){
    cannons.add(new projectile(new PVector(pl.pos.x,pl.pos.y),new PVector(mouseX-pl.pos.x,mouseY-pl.pos.y),5,pl));
    }
  }
}

// Movement and game navigation 
void keyPressed(){
  if(gameStage == 1){
 if(keyCode== LEFT || keyCode == 65){
   movements[3] = true; 
 }
 if(keyCode== RIGHT || keyCode == 68){
   movements[2] = true; 
   
 }if(keyCode== UP || keyCode == 87){
      movements[1] = true; 

 }if(keyCode== DOWN || keyCode == 83){
      movements[0] = true; 
 }
  }
 if(keyCode == 78){
   sg.newGame();
 }
 if(keyCode == 88){
   sg.newLevel();
 }
 
 if((gameStage == 3 || gameStage == 4 )&& keyCode == 32){
   gameStage = 0;
   startMusic.loop();
   gameMusic.pause(); 
   gameMusic.rewind();
 }
 if(gameStage == 2 && keyCode == 32){
   sg.newLevel();
   gameStage = 1; 
 }
 
 if(keyCode == 80){
   gamePaused = !gamePaused;
   
 }
 
}


void keyReleased(){
   if(gameStage == 1){
 if(keyCode== LEFT || keyCode == 65){
   movements[3] = false; 
 }
 if(keyCode== RIGHT || keyCode == 68){
   movements[2] = false; 
   
 }if(keyCode== UP || keyCode == 87){
      movements[1] = false; 

 }if(keyCode== DOWN || keyCode == 83){
      movements[0] = false; 
 }
  }
}









     
